package org.jluc.ctr.tools.calendrier.server.model.evenements;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.club.ClubStructure;
import org.jluc.ctr.tools.calendrier.server.model.club.Demandeur;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Evenement extends PanacheEntityBase {
    @Id
    private UUID uuid;
    private String evtidforms;
    
    private Date datedemande;
    private Date datedebut;
    private Date datefin;
    @ManyToOne
    private TypeEvenement typeEvenement;
    @ManyToOne
    private Demandeur demandeur;
    @ManyToOne
    private Demandeur partenaire;
    private String mailcontact;
    private String lieu;
    @ManyToOne
    private Moniteur presidentjury;
    @ManyToOne
    private Moniteur deleguectr;
    @ManyToOne  
    private Moniteur repcibpl;
    private Status statut = Status.DEMANDE;
    private Date datevalidation;
    @ManyToOne
    private ClubStructure organisateur;
    private String comment;
    private String calendareventid;
    @OneToMany(mappedBy = "evenement")
    private List<Session> sessions;
    private Integer nbparticipants = 0;
    
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

    public TypeEvenement getType() {
        return typeEvenement;
    }

    public Demandeur getDemandeur() {
        return demandeur;
    }

    public Demandeur getPartenaire() {
        return partenaire;
    }

    public String getMailcontact() {
        return mailcontact;
    }

    public String getLieu() {
        return lieu;
    }

    public Moniteur getPresidentjury() {
        return presidentjury;
    }

    public Moniteur getDeleguectr() {
        return deleguectr;
    }

    public Moniteur getRepcibpl() {
        return repcibpl;
    }

    public Status getStatut() {
        return statut;
    }

    public Date getDatevalidation() {
        return datevalidation;
    }

    public ClubStructure getOrganisateur() {
        return organisateur;
    }

    public String getComment() {
        return comment;
    }

    public String getCalendareventid() {
        return calendareventid;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public int getNbparticipants() {
        return nbparticipants;
    }

    public Evenement() {
    }

    public Evenement(Date datedemande, Date datedebut, Date datefin, String activite, String demandeurStr,
            String partenaireStr, String mailcontact, String lieu, String organisateurStr, String comment,
            int nbparticipants) {
        this.datedemande = datedemande;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.lieu = lieu;
        this.mailcontact = mailcontact;
        this.nbparticipants = nbparticipants;
        this.comment = comment;
        this.evtidforms = "evt-" + datedemande.getTime() + "-" + activite.replace(" ", "");
    }

    public String toString() {
        return "Evènement [uuid = " + uuid + ", lieu = " + lieu + "]";
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setCalendareventid(String calendareventid) {
        this.calendareventid = calendareventid;
    }
}