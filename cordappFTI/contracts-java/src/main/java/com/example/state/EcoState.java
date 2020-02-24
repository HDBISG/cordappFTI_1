package com.example.state;

import com.example.contract.EcoIssueContract;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/* Our state, defining a shared fact on the ledger.
 * See src/main/java/examples/ArtState.java for an example. */
@BelongsToContract(EcoIssueContract.class)
public class EcoState implements ContractState {

    private Party fti;
    private Party vcc;
    private String ecoContent;
    private String docNo = null;

/*
    public EcoState(Party fti, Party vcc, String ecoContent ) {
        this.fti = fti;
        this.vcc = vcc;
        this.ecoContent = ecoContent;
        this.linearId = new UniqueIdentifier();
    }
*/

    public EcoState(Party fti, Party vcc, String docNo, String ecoContent) {
        this.fti = fti;
        this.vcc = vcc;
        this.docNo = docNo;
        this.ecoContent = ecoContent;
    }

    public Party getFti() {
        return fti;
    }

    public Party getVcc() {
        return vcc;
    }

    public String getEcoContent() {
        return ecoContent;
    }

    public String getDocNo() {
        return docNo;
    }

    @Override
    public String toString() {
        return "EcoState{" +
                "fti=" + fti +
                ", vcc=" + vcc +
                ", ecoContent='" + ecoContent + '\'' +
                ", docNo='" + docNo + '\'' +
                '}';
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of( fti, vcc);
    }
}