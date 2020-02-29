package com.vcc.corda.eco.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.vcc.corda.eco.contract.EcoContract;
import com.vcc.corda.eco.state.EcoState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;

@InitiatingFlow
@StartableByRPC
public class EcoIssueFlowInitiator extends FlowLogic<SignedTransaction> {
    // private final Party vcc;
    // private final String ecoContent;
    EcoState ecoState;

    private static final Logger logger = LoggerFactory.getLogger(EcoIssueFlowInitiator.class);

    public EcoIssueFlowInitiator(EcoState ecoState ) {
        this.ecoState = ecoState;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party fti = getOurIdentity();

        /* ============================================================================
         *         TODO 1 - Create our EcoState to represent on-ledger tokens!
         * ===========================================================================*/
        // We create our new TokenState.

        //EcoIssueContract.Commands.Issue command = new EcoIssueContract.Commands.Issue();

        final Command<EcoContract.Commands.Issue> txCommand = new Command<>(
                new EcoContract.Commands.Issue(),
                ImmutableList.of(fti.getOwningKey(), ecoState.getVcc().getOwningKey()));
        /* ============================================================================
         *      TODO 3 - Build our Eco issuance transaction to update the ledger!
         * ===========================================================================*/
        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.setNotary( notary );
        transactionBuilder.addOutputState( ecoState, EcoContract.ID);
        //transactionBuilder.addCommand( command, ecoState.getFti().getOwningKey(), ecoState.getFti().getOwningKey() );
        transactionBuilder.addCommand( txCommand );

        /* ============================================================================
         *          TODO 2 - Write our EcoContract to control token issuance!
         * ===========================================================================*/
        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow( ecoState.getVcc() );

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