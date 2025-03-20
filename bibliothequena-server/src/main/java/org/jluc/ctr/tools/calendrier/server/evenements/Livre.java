package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.Date;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.club.ClubStructure;
import org.jluc.ctr.tools.calendrier.server.club.Demandeur;
import org.jluc.ctr.tools.calendrier.server.moniteurs.Moniteur;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Livre extends PanacheEntity {
    private String uuid;
    private String name;
    private Date datedemande;
    private Date datedebut;
    private Date datefin;
    private String uuidtype;
    private String uuiddemandeur;
    private String uuidpartenaire;
    private String mailcontact;
    private String lieu;
    private String uuidpresidentjury;
    private String uuiddeleguectr;
    private String uuidrepcibpl;
    private Status statut = Status.DEMANDE;
    private Date datevalidation;
    private String uuidorganisateur;
    private String comment;
    private String calendareventid;

    public Evenement() {
        uuid = UUID.randomUUID().toString();
    }

    public Evenement(String uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public Evenement(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    public String toString() {
        return "Evènement [uuid = " + uuid + ", name = " + name + "]";
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }
}