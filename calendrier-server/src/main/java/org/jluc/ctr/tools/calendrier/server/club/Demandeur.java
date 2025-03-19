package org.jluc.ctr.tools.calendrier.server.club;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Demandeur extends PanacheEntity {

    private String uuid;
    private String name;
    private String numerostructure;

    public Demandeur() {
        this.uuid = UUID.randomUUID().toString();
    }

    public Demandeur(String name, String numerostructure) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.numerostructure = numerostructure;
    }
}
