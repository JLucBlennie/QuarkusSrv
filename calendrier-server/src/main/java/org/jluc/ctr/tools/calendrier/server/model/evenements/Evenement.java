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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@NamedEntityGraph(name = "evenement-with-type", attributeNodes = @NamedAttributeNode("typeEvenement"))
@NamedEntityGraph(name = "evenement-with-demandeur", attributeNodes = @NamedAttributeNode("demandeur"))
@NamedEntityGraph(name = "evenement-with-partenaire", attributeNodes = @NamedAttributeNode("partenaire"))
@NamedEntityGraph(name = "evenement-with-organisateur", attributeNodes = @NamedAttributeNode("organisateur"))
@NamedEntityGraph(name = "evenement-with-presidentjury", attributeNodes = @NamedAttributeNode("presidentjury"))
@NamedEntityGraph(name = "evenement-with-deleguectr", attributeNodes = @NamedAttributeNode("deleguectr"))
@NamedEntityGraph(name = "evenement-with-presdelegue", attributeNodes = { @NamedAttributeNode("presidentjury"),
        @NamedAttributeNode("deleguectr") })
@NamedEntityGraph(name = "evenement-with-all", attributeNodes = { @NamedAttributeNode("typeEvenement"),
        @NamedAttributeNode("demandeur"), @NamedAttributeNode("partenaire"), @NamedAttributeNode("organisateur"),
        @NamedAttributeNode("presidentjury"), @NamedAttributeNode("deleguectr") })
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
        // Récupération du TypeEvenement dans la liste des types existants
        this.typeEvenement = TypeEvenement.find("valeurforms", activite).firstResult();
        ClubStructure organisateur = ClubStructure
                .find("name", organisateurStr).firstResult();
        if (organisateur == null) {
            System.out.println("Organisateur non trouvé pour : " + organisateurStr);
            organisateur = new ClubStructure(organisateurStr);
            organisateur.persist();
        }
        this.organisateur = organisateur;
        Demandeur demandeur = Demandeur
                .find("numerostructure",
                        demandeurStr.substring(demandeurStr.indexOf(" (") + 2, demandeurStr.indexOf(")")))
                .firstResult();
        if (demandeur == null) {
            System.out.println("Demandeur non trouvé pour : " + demandeurStr);
            demandeur = new Demandeur(demandeurStr.substring(0, demandeurStr.indexOf(" (")),
                    demandeurStr.substring(demandeurStr.indexOf(" (") + 2, demandeurStr.indexOf(")")));
            demandeur.persist();
        }
        this.demandeur = demandeur;
        if (partenaireStr == null || partenaireStr.isEmpty()) {
            this.partenaire = null;
        } else
            this.partenaire = Demandeur.find("numerostructure", partenaireStr.substring(partenaireStr.indexOf(" (") + 2,
                    partenaireStr.indexOf(")"))).firstResult();
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

    public void setStatut(Status statut) {
        this.statut = statut;
    }

    public void setDatevalidation(Date datevalidation) {
        this.datevalidation = datevalidation;
    }

    public void setNbparticipants(int nbparticipants) {
        this.nbparticipants = nbparticipants;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPresidentjury(Moniteur presidentjury) {
        this.presidentjury = presidentjury;
    }

    public void setDeleguectr(Moniteur deleguectr) {
        this.deleguectr = deleguectr;
    }

    public void setRepcibpl(Moniteur repcibpl) {
        this.repcibpl = repcibpl;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setEvtidforms(String evtidforms) {
        this.evtidforms = evtidforms;
    }

    public void setDatedemande(Date datedemande) {
        this.datedemande = datedemande;
    }

    public void setDatedebut(Date datedebut) {
        this.datedebut = datedebut;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public void setType(TypeEvenement typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public void setDemandeur(Demandeur demandeur) {
        this.demandeur = demandeur;
    }

    public void setPartenaire(Demandeur partenaire) {
        this.partenaire = partenaire;
    }

    public void setMailcontact(String mailcontact) {
        this.mailcontact = mailcontact;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setOrganisateur(ClubStructure organisateur) {
        this.organisateur = organisateur;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
}