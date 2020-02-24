package com.vcc.corda.eco.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * An ecoState schema.
 */
public class EcoSchemaV1 extends MappedSchema {
    public EcoSchemaV1() {
        super(EcoSchema.class, 1, ImmutableList.of(PersistentEco.class));
    }

    @Entity
    @Table(name = "eco_states")
    public static class PersistentEco extends PersistentState {

        @Column(name = "fti") private final String fti;
        @Column(name = "vcc") private final String vcc;
        @Column(name = "eco_content") private final String ecoContent;
        @Column(name = "doc_no") private final String docNo;
        @Column(name = "linear_id") private final UUID linearId;

        public PersistentEco(String fti, String vcc, String docNo, String ecoContent, UUID linearId) {
            this.fti = fti;
            this.vcc = vcc;
            this.ecoContent = ecoContent;
            this.docNo = docNo;
            this.linearId = linearId;
        }

        // Default constructor required by hibernate.
        public PersistentEco() {
            this.fti = null;
            this.vcc = null;
            this.ecoContent = null;
            this.docNo = null;
            this.linearId = null;
        }

        public String getFti() {
            return fti;
        }

        public String getVcc() {
            return vcc;
        }

        public String getEcoContent() {
            return ecoContent;
        }

        public String getDocNo() {
            return docNo;
        }

        public UUID getId() {
            return linearId;
        }
    }
}