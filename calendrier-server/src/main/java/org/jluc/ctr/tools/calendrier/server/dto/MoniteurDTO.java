package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.NiveauMoniteur;

public class MoniteurDTO {
    public UUID uuid;
    public String lastname;
    public String firstname;
    public NiveauMoniteur niveau;
    public int nbevents = 0;

    public static MoniteurDTO fromEntity(Moniteur moniteur) {
        if (moniteur == null) {
            return null;
        }

        MoniteurDTO dto = new MoniteurDTO();
        dto.uuid = moniteur.getUUID();
        dto.lastname = moniteur.getLastName();
        dto.firstname = moniteur.getFirstName();
        dto.niveau = moniteur.getNiveau();
        return dto;
    }

    public Moniteur toEntity() {
        Moniteur moniteur = Moniteur.findById(this.uuid);
        if (moniteur == null) {
            moniteur = new Moniteur();
            moniteur.setUUID(this.uuid != null ? this.uuid : UUID.randomUUID());
        }
        moniteur.setLastName(this.lastname);
        moniteur.setFirstName(this.firstname);
        moniteur.setNiveau(this.niveau);
        return moniteur;
    }

    public void setNbevents(int nbevents) {
        this.nbevents = nbevents;
    }
}
