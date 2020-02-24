package com.vcc.corda.eco.state;

import com.vcc.corda.eco.contract.EcoContract;
import com.vcc.corda.eco.schema.EcoSchemaV1;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/* Our state, defining a shared fact on the ledger.
 * See src/main/java/examples/ArtState.java for an example. */
@BelongsToContract(EcoContract.class)
public class EcoState implements LinearState, QueryableState {

    private Party fti;
    private Party vcc;
    private String ecoContent;
    private String docNo = null;
    private final UniqueIdentifier linearId;

/*
    public EcoState(Party fti, Party vcc, String ecoContent ) {
        this.fti = fti;
        this.vcc = vcc;
        this.ecoContent = ecoContent;
        this.linearId = new UniqueIdentifier();
    }
*/

    public EcoState(Party fti, Party vcc, String docNo, String ecoContent,
                    UniqueIdentifier linearId) {
        this.fti = fti;
        this.vcc = vcc;
        this.docNo = docNo;
        this.ecoContent = ecoContent;
        this.linearId = linearId;
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

    @Override public UniqueIdentifier getLinearId() { return linearId; }

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


    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof EcoSchemaV1) {
            return new EcoSchemaV1.PersistentEco (
                    this.fti.getName().toString(),
                    this.vcc.getName().toString(),
                    this.docNo,
                    this.ecoContent,
                    this.linearId.getId() );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new EcoSchemaV1());
    }


}