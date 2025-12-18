package org.jluc.ctr.tools.calendrier.server.dto;

import java.util.UUID;
import org.jluc.ctr.tools.calendrier.server.model.club.ClubStructure;

public class ClubStructureDTO {
    public UUID uuid;
    public String name;

    public static ClubStructureDTO fromEntity(ClubStructure entity) {
        if (entity == null) {
            return null;
        }
        
        ClubStructureDTO dto = new ClubStructureDTO();
        dto.uuid = entity.getUuid();
        dto.name = entity.getName();
        return dto;
    }
}
