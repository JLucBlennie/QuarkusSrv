package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class TypeEvenement extends PanacheEntity {

    private String uuid;
    private String name;
    private TypeActivite activite;
    private String valeurforms;

    public TypeEvenement(){
        this.uuid = UUID.randomUUID().toString();
    }
}
