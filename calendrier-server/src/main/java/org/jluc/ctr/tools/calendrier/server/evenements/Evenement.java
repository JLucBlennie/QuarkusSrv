package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.Date;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Evenement extends PanacheEntityBase {
    @Id
    private UUID uuid;
    private String evtidforms;
    
    private Date datedemande;
    private Date datedebut;
    private Date datefin;
    private UUID uuidtype;
    private UUID uuiddemandeur;
    private UUID uuidpartenaire;
    private String mailcontact;
    private String lieu;
    private UUID uuidpresidentjury;
    private UUID uuiddeleguectr;
    private UUID uuidrepcibpl;
    private Status statut = Status.DEMANDE;
    private Date datevalidation;
    private UUID uuidorganisateur;
    private String comment;
    private String calendareventid;
    
    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
    
    public String getEvtidforms() {
        return evtidforms;
    }

    public Date getDatedemande() {
        return datedemande;
    }

    public Date getDatedebut() {
        return datedebut;
    }

    public Date getDatefin() {
        return datefin;
    }

    public UUID getUuidtype() {
        return uuidtype;
    }

    public UUID getUuiddemandeur() {
        return uuiddemandeur;
    }

    public UUID getUuidpartenaire() {
        return uuidpartenaire;
    }

    public String getMailcontact() {
        return mailcontact;
    }

    public String getLieu() {
        return lieu;
    }

    public UUID getUuidpresidentjury() {
        return uuidpresidentjury;
    }

    public UUID getUuiddeleguectr() {
        return uuiddeleguectr;
    }

    public UUID getUuidrepcibpl() {
        return uuidrepcibpl;
    }

    public Status getStatut() {
        return statut;
    }

    public Date getDatevalidation() {
        return datevalidation;
    }

    public UUID getUuidorganisateur() {
        return uuidorganisateur;
    }

    public String getComment() {
        return comment;
    }

    public String getCalendareventid() {
        return calendareventid;
    }
    
    public Evenement() {
    }

    public String toString() {
        return "Evènement [uuid = " + uuid + ", lieu = " + lieu + "]";
    }

    public UUID getUUID() {
        return uuid;
    }
}