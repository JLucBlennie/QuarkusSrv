package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Status;

import io.quarkus.logging.Log;

public class EvenementDTO {
    public UUID uuid;
    public String evtidforms;
    public Date datedemande;
    public Date datedebut;
    public Date datefin;
    public TypeEvenementDTO typeEvenement;
    public DemandeurDTO demandeur;
    public DemandeurDTO partenaire;
    public String mailcontact;
    public String lieu;
    public MoniteurDTO presidentjury;
    public MoniteurDTO deleguectr;
    public MoniteurDTO repcibpl;
    public Status statut = Status.DEMANDE;
    public Date datevalidation;
    public ClubStructureDTO organisateur;
    public String comment;
    public String calendareventid;
    public int nbparticipants;
    public List<SessionDTO> sessions;

    public static EvenementDTO fromEntity(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        EvenementDTO dto = new EvenementDTO();
        dto.uuid = evenement.getUUID();
        dto.evtidforms = evenement.getEvtidforms();
        dto.datedemande = evenement.getDatedemande();
        dto.datedebut = evenement.getDatedebut();
        dto.datefin = evenement.getDatefin();
        dto.typeEvenement = TypeEvenementDTO.fromEntity(evenement.getType());
        dto.demandeur = DemandeurDTO.fromEntity(evenement.getDemandeur());
        dto.partenaire = DemandeurDTO.fromEntity(evenement.getPartenaire());
        dto.mailcontact = evenement.getMailcontact();
        dto.lieu = evenement.getLieu();
        dto.presidentjury = MoniteurDTO.fromEntity(evenement.getPresidentjury());
        dto.deleguectr = MoniteurDTO.fromEntity(evenement.getDeleguectr());
        dto.repcibpl = MoniteurDTO.fromEntity(evenement.getRepcibpl());
        dto.statut = evenement.getStatut();
        dto.datevalidation = evenement.getDatevalidation();
        dto.organisateur = ClubStructureDTO.fromEntity(evenement.getOrganisateur());
        dto.comment = evenement.getComment();
        dto.calendareventid = evenement.getCalendareventid();
        dto.nbparticipants = evenement.getNbparticipants();

        if (evenement.getSessions() == null) {
            dto.sessions = null;
        } else {
            dto.sessions = evenement.getSessions().stream()
                    .map(SessionDTO::fromEntity)
                    .toList();
        }
        return dto;
    }

    public Evenement toEntity() {
        Log.debug("Conversion d'un evenement JSON en evenement Quarkus");
        Log.debug("EvenementJSON" + this.uuid + ", " + this.evtidforms + ", " + this.datedemande + ", " + this.datedebut
                + ", "
                + this.datefin + ", " + this.typeEvenement + ", " + this.demandeur + ", " + this.partenaire
                + ", " + this.mailcontact + ", " + this.lieu + ", " + this.presidentjury + ", "
                + this.deleguectr + ", " + this.repcibpl + ", " + this.statut + ", " + this.datevalidation
                + ", " + this.organisateur + ", " + this.comment + ", " + this.calendareventid + ", "
                + this.nbparticipants + ", " + this.sessions);

        Evenement evenement = null;
        if (this.uuid != null) {
            evenement = Evenement.findById(this.uuid);
            if (evenement == null) {
                evenement = new Evenement();
                evenement.setUUID(this.uuid != null ? this.uuid : UUID.randomUUID());
            }
        } else {
            evenement = new Evenement();
            evenement.setUUID(UUID.randomUUID());
        }
        evenement.setEvtidforms(this.evtidforms);
        evenement.setDatedemande(this.datedemande);
        evenement.setDatedebut(this.datedebut);
        evenement.setDatefin(this.datefin);
        evenement.setType(this.typeEvenement != null ? this.typeEvenement.toEntity() : null);
        evenement.setDemandeur(this.demandeur != null ? this.demandeur.toEntity() : null);
        evenement.setPartenaire(this.partenaire != null ? this.partenaire.toEntity() : null);
        evenement.setMailcontact(this.mailcontact);
        evenement.setLieu(this.lieu);
        evenement.setPresidentjury(this.presidentjury != null ? this.presidentjury.toEntity() : null);
        evenement.setDeleguectr(this.deleguectr != null ? this.deleguectr.toEntity() : null);
        evenement.setRepcibpl(this.repcibpl != null ? this.repcibpl.toEntity() : null);
        evenement.setStatut(this.statut);
        evenement.setDatevalidation(this.datevalidation);
        evenement.setOrganisateur(this.organisateur != null ? this.organisateur.toEntity() : null);
        evenement.setComment(this.comment);
        evenement.setCalendareventid(this.calendareventid);
        evenement.setNbparticipants(this.nbparticipants);

        if (this.sessions != null) {
            Log.debug("Il y a des sessions");
            List<Session> sessionEntities = this.sessions.stream()
                    .map(SessionDTO::toEntity)
                    .toList();
            for (Session session : sessionEntities) {
                session.setEvenement(evenement);
            }
            evenement.setSessions(sessionEntities);
        } else {
            Log.debug("Il n'y a pas de sessions");
            evenement.setSessions(null);
        }

        return evenement;
    }
}
