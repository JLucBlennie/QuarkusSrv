package org.jluc.ctr.tools.calendrier.server.model.club;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Demandeur extends PanacheEntityBase {

    @Id
    private UUID uuid;
    private String name;
    private String numerostructure;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Demandeur() {
    }

    public Demandeur(String name, String numerostructure) {
        this.name = name;
        this.numerostructure = numerostructure;
    }
    public UUID getUUID() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public String getNumeroStructure() {
        return numerostructure;
    }
}
