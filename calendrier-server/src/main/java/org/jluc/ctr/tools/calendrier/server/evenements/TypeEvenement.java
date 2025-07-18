package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class TypeEvenement extends PanacheEntityBase {

    @Id
    private UUID uuid;
    private String name;
    private TypeActivite activite;
    private String valeurforms;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public TypeEvenement() {
    }
}
