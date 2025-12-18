package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.UUID;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeActivite;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeEvenement;

public class TypeEvenementDTO {
    public UUID uuid;
    public String name;
    public TypeActivite activite;
    public String valeurforms;

    public static TypeEvenementDTO fromEntity(TypeEvenement entity) {
        if (entity == null) {
            return null;
        }
        
        TypeEvenementDTO dto = new TypeEvenementDTO();
        dto.uuid = entity.getUuid();
        dto.name = entity.getName();
        dto.activite = entity.getActivite();
        dto.valeurforms = entity.getValeurforms();
        return dto;
    }
}
