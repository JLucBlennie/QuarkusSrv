package org.jluc.ctr.tools.calendrier.server.evenements.json;

import java.util.Date;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.evenements.Status;

public class EvenementJSON {

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

    public EvenementJSON() {
    }

    public EvenementJSON(UUID uuid, String evtidforms, Date datedemande, Date datedebut, Date datefin, UUID uuidtype,
    UUID uuiddemandeur, UUID uuidpartenaire, String mailcontact, String lieu, UUID uuidpresidentjury,
    UUID uuiddeleguectr, UUID uuidrepcibpl, Status statut, Date datevalidation, UUID uuidorganisateur,
    String comment, String calendareventid) {
        this.uuid = uuid;
        this.evtidforms = evtidforms;
        this.datedemande = datedemande;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.uuidtype = uuidtype;
        this.uuiddemandeur = uuiddemandeur;
        this.uuidpartenaire = uuidpartenaire;
        this.mailcontact = mailcontact;
        this.lieu = lieu;
        this.uuidpresidentjury = uuidpresidentjury;
        this.uuiddeleguectr = uuiddeleguectr;
        this.uuidrepcibpl = uuidrepcibpl;
        this.statut = statut;
        this.datevalidation = datevalidation;
        this.uuidorganisateur = uuidorganisateur;
        this.comment = comment;
        this.calendareventid = calendareventid;
    }
    
    public EvenementJSON(Evenement evenement) {
        this.uuid = evenement.getUUID();
        this.evtidforms = evenement.getEvtidforms();
        this.datedemande = evenement.getDatedemande();
        this.datedebut = evenement.getDatedebut();
        this.datefin = evenement.getDatefin();
        this.uuidtype = evenement.getUuidtype();
        this.uuiddemandeur = evenement.getUuiddemandeur();
        this.uuidpartenaire = evenement.getUuidpartenaire();
        this.mailcontact = evenement.getMailcontact();
        this.lieu = evenement.getLieu();
        this.uuidpresidentjury = evenement.getUuidpresidentjury();
        this.uuiddeleguectr = evenement.getUuiddeleguectr();
        this.uuidrepcibpl = evenement.getUuidrepcibpl();
        this.statut = evenement.getStatut();
        this.datevalidation = evenement.getDatevalidation();
        this.uuidorganisateur = evenement.getUuidorganisateur();
        this.comment = evenement.getComment();
        this.calendareventid = evenement.getCalendareventid();
    }
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEvtidforms() {
        return evtidforms;
    }

    public void setEvtidforms(String evtidforms) {
        this.evtidforms = evtidforms;
    }

    public Date getDatedemande() {
        return datedemande;
    }

    public void setDatedemande(Date datedemande) {
        this.datedemande = datedemande;
    }

    public Date getDatedebut() {
        return datedebut;
    }

    public void setDatedebut(Date datedebut) {
        this.datedebut = datedebut;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public UUID getUuidtype() {
        return uuidtype;
    }

    public void setUuidtype(UUID uuidtype) {
        this.uuidtype = uuidtype;
    }

    public UUID getUuiddemandeur() {
        return uuiddemandeur;
    }

    public void setUuiddemandeur(UUID uuiddemandeur) {
        this.uuiddemandeur = uuiddemandeur;
    }

    public UUID getUuidpartenaire() {
        return uuidpartenaire;
    }

    public void setUuidpartenaire(UUID uuidpartenaire) {
        this.uuidpartenaire = uuidpartenaire;
    }

    public String getMailcontact() {
        return mailcontact;
    }

    public void setMailcontact(String mailcontact) {
        this.mailcontact = mailcontact;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public UUID getUuidpresidentjury() {
        return uuidpresidentjury;
    }

    public void setUuidpresidentjury(UUID uuidpresidentjury) {
        this.uuidpresidentjury = uuidpresidentjury;
    }

    public UUID getUuiddeleguectr() {
        return uuiddeleguectr;
    }

    public void setUuiddeleguectr(UUID uuiddeleguectr) {
        this.uuiddeleguectr = uuiddeleguectr;
    }

    public UUID getUuidrepcibpl() {
        return uuidrepcibpl;
    }

    public void setUuidrepcibpl(UUID uuidrepcibpl) {
        this.uuidrepcibpl = uuidrepcibpl;
    }

    public Status getStatut() {
        return statut;
    }

    public void setStatut(Status statut) {
        this.statut = statut;
    }

    public Date getDatevalidation() {
        return datevalidation;
    }

    public void setDatevalidation(Date datevalidation) {
        this.datevalidation = datevalidation;
    }

    public UUID getUuidorganisateur() {
        return uuidorganisateur;
    }

    public void setUuidorganisateur(UUID uuidorganisateur) {
        this.uuidorganisateur = uuidorganisateur;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCalendareventid() {
        return calendareventid;
    }

    public void setCalendareventid(String calendareventid) {
        this.calendareventid = calendareventid;
    }

}
