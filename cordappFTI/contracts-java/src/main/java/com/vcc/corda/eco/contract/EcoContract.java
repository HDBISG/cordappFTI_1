package com.vcc.corda.eco.contract;

import com.vcc.corda.eco.state.EcoState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class EcoContract implements Contract {

    public static String ID = "com.vcc.corda.eco.contract.EcoContract";

    private static final Logger logger = LoggerFactory.getLogger(EcoContract.class);

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        if( tx.getCommands().size() != 1 )
            throw new IllegalArgumentException("Transaction must have one command");

        Command command = tx.getCommand(0);

        if( command.getValue() instanceof Commands.Issue ) {
            // parse to UBL
            if (tx.getOutputStates().size() != 1) throw new IllegalArgumentException("Eco transfer should have one output.");
            if (tx.outputsOfType(EcoState.class).size() != 1) throw new IllegalArgumentException("Eco transfer output should be an EcoState.");

            // Grabbing the transaction's contents.
            final EcoState ecoStateOutput = tx.outputsOfType(EcoState.class).get(0);

            logger.info("EcoIssueContract = "+ ecoStateOutput);

        } else if( command.getValue() instanceof Commands.Replace ) {

        } else if( command.getValue() instanceof Commands.Cancel ) {

        } else {
            throw new IllegalArgumentException("Unrecognized command");
        }

    }


    public interface Commands extends CommandData {
        class Issue implements Commands { }
        class Replace extends TypeOnlyCommandData implements Commands{}
        class Cancel extends TypeOnlyCommandData implements Commands{}
    }
}