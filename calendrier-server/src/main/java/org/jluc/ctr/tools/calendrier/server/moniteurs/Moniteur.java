package org.jluc.ctr.tools.calendrier.server.moniteurs;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Moniteur extends PanacheEntity {
    private String uuid;
    private String lastname;
    private String firstname;
    private NiveauMoniteur niveau;

    public Moniteur() {
        uuid = UUID.randomUUID().toString();
    }

    public Moniteur(String uuid, String name) {
        this.lastname = name;
        this.uuid = uuid;
    }

    public Moniteur(String name) {
        this.lastname = name;
        this.uuid = UUID.randomUUID().toString();
    }

    public String toString() {
        return "Moniteur [uuid = " + uuid + ", name = " + lastname + "]";
    }

    public String getLastname() {
        return lastname;
    }

    public String getUUID() {
        return uuid;
    }
}