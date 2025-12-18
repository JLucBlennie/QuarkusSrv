package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.UUID;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.NiveauMoniteur;

public class MoniteurDTO {
    public UUID uuid;
    public String lastname;
    public String firstname;
    public NiveauMoniteur niveau;

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
}
