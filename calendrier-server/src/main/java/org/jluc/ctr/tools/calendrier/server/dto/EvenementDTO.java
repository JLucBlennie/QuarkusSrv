package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Status;

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
}
