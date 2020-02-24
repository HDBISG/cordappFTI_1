package com.vcc.corda.eco;

import co.paralleluniverse.fibers.Suspendable;
import com.vcc.corda.eco.state.EcoState;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(EcoIssueFlowInitiator.class)
public class EcoIssueFlowResponder extends FlowLogic<Void> {

    private final FlowSession otherSide;

    private static final Logger logger = LoggerFactory.getLogger(EcoIssueFlowResponder.class);

    public EcoIssueFlowResponder(FlowSession otherSide) {
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
                requireThat(require -> {
                    ContractState output = stx.getTx().getOutputs().get(0).getData();
                    require.using("This must be an eco transaction.", output instanceof EcoState);
                    EcoState ecoState = (EcoState) output;
                    //require.using("I won't accept IOUs with a value over 100.", iou.getValue() <= 100);
                    logger.info("ecoState=" + ecoState );
                    return null;
                });
            }
        });
        logger.info("before ReceiveFinalityFlow()"  );
        subFlow(new ReceiveFinalityFlow(otherSide, signedTransaction.getId()));
        logger.info("end ReceiveFinalityFlow()"  );

        return null;
    }
}