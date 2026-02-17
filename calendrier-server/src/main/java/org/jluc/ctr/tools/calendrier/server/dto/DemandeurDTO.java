package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.club.Demandeur;

public class DemandeurDTO {
    public UUID uuid;
    public String name;
    public String numerostructure;

    public static DemandeurDTO fromEntity(Demandeur demandeur) {
        if (demandeur == null) {
            return null;
        }

        DemandeurDTO dto = new DemandeurDTO();
        dto.uuid = demandeur.getUUID();
        dto.name = demandeur.getName();
        dto.numerostructure = demandeur.getNumeroStructure();
        return dto;
    }

    public Demandeur toEntity() {
        Demandeur demandeur = null;
        if (this.uuid != null) {
            demandeur = Demandeur.findById(this.uuid);
            if (demandeur == null) {
                demandeur = new Demandeur();
                demandeur.setUUID(this.uuid);
            }
        } else {
            demandeur = new Demandeur();
            demandeur.setUUID(UUID.randomUUID());
        }
        demandeur.setName(this.name);
        demandeur.setNumeroStructure(this.numerostructure);
        return demandeur;
    }
}
