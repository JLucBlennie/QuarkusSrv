package org.jluc.ctr.tools.calendrier.server.model.moniteurs;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Moniteur extends PanacheEntityBase {
    @Id
    private UUID uuid;
    private String lastname;
    private String firstname;
    private NiveauMoniteur niveau;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Moniteur() {
    }

    public Moniteur(UUID uuid, String name) {
        this.lastname = name;
        this.uuid = uuid;
    }

    public Moniteur(String name) {
        this.lastname = name;
    }

    public String toString() {
        return "Moniteur [uuid = " + uuid + ", name = " + firstname + " " + lastname + ", niveau = " + niveau + "]";
    }

    public String getLastName() {
        return lastname;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getFirstName() {
        return firstname;
    }

    public NiveauMoniteur getNiveau() {
        return niveau;
    }
}