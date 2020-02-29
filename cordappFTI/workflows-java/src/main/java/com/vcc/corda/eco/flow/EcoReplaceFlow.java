package com.vcc.corda.eco.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.vcc.corda.eco.contract.EcoContract;
import com.vcc.corda.eco.schema.EcoSchemaV1;
import com.vcc.corda.eco.state.EcoState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

public class EcoReplaceFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class EcoReplaceFlowInitiator extends FlowLogic<SignedTransaction> {

        private final String oldDocNo;
        private EcoState newEcoState;

        private  final Logger logger = LoggerFactory.getLogger(EcoReplaceFlowInitiator.class);

        public EcoReplaceFlowInitiator( String oldDocNo, EcoState newEcoState  ) {
            this.oldDocNo = oldDocNo;
            this.newEcoState = newEcoState;
        }

        private final ProgressTracker progressTracker = new ProgressTracker();

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            // 1. Retrieve the IOU State from the vault using LinearStateQueryCriteria
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo attributeDocNo = null;
            try {
                attributeDocNo = getField("docNo", EcoSchemaV1.PersistentEco.class);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            //CriteriaExpression docNoIndex = Builder.like(attributeDocNo, "%");
            CriteriaExpression docNoIndex = Builder.equal(attributeDocNo, oldDocNo );
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria( docNoIndex );

            // Vault.Page<EcoState> page =  proxy.vaultQueryByCriteria( customCriteria, EcoState.class );
            Vault.Page<EcoState> page = getServiceHub().getVaultService().queryBy(EcoState.class, customCriteria);

            // 2. Get a reference to the inputState data that we are going to settle.
            StateAndRef inputStateAndRefToSettle = (StateAndRef) page.getStates().get(0);
            // EcoState oldEcoState = (EcoState) ((StateAndRef) page.getStates().get(0)).getState().getData();

            // We choose our transaction's notary (the notary prevents double-spends).
            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            // We get a reference to our own identity.
            Party fti = getOurIdentity();
            /////////////////////////////////////////////////


            /* ============================================================================
             *         TODO 1 - Create our EcoState to represent on-ledger tokens!
             * ===========================================================================*/
            // We create our new TokenState.
            final Command<EcoContract.Commands.Replace> txCommand = new Command<>(
                    new EcoContract.Commands.Replace(),
                    newEcoState.getParticipants()
                            .stream().map(AbstractParty::getOwningKey)
                            .collect(Collectors.toList()) );
            /* ============================================================================
             *      TODO 3 - Build our Eco issuance transaction to update the ledger!
             * ===========================================================================*/
            // We build our transaction.
            TransactionBuilder transactionBuilder = new TransactionBuilder( notary );
            transactionBuilder.addCommand( txCommand );
            transactionBuilder.addInputState(inputStateAndRefToSettle);
            transactionBuilder.addOutputState( newEcoState, EcoContract.ID);

            /* ============================================================================
             *          TODO 2 - Write our EcoContract to control token issuance!
             * ===========================================================================*/
            // We check our transaction is valid based on its contracts.
            transactionBuilder.verify(getServiceHub());

            logger.info("cancel Before initiateFlow()");
            FlowSession session = initiateFlow( newEcoState.getVcc() );

            // We sign the transaction with our private key, making it immutable.
            logger.info("Before signInitialTransaction()");
            SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

            // The counterparty signs the transaction
            logger.info("Before CollectSignaturesFlow()");
            SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

            // We get the transaction notarised and recorded automatically by the platform.
            logger.info("Before FinalityFlow()");
            return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
        }
    }


    @InitiatedBy(EcoReplaceFlowInitiator.class)
    public static class EcoReplaceFlowResponder extends FlowLogic<Void> {

        private final FlowSession otherSide;

        private final Logger logger = LoggerFactory.getLogger(EcoReplaceFlowResponder.class);

        public EcoReplaceFlowResponder(FlowSession otherSide) {
            this.otherSide = otherSide;
        }

        @Override
        @Suspendable
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(otherSide) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    // Implement responder flow transaction checks here

                }
            });
            logger.info("before ReceiveFinalityFlow()"  );
            subFlow(new ReceiveFinalityFlow(otherSide, signedTransaction.getId()));
            logger.info("end ReceiveFinalityFlow()"  );

            return null;
        }
    }
}
