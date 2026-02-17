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

    public ClubStructure toEntity() {
        ClubStructure entity = null;
        if (this.uuid != null) {
            entity = ClubStructure.findById(this.uuid);
            if (entity != null) {
                return entity;
            } else {
                entity = new ClubStructure();
                entity.setUuid(this.uuid);
            }
        } else {
            entity = new ClubStructure();
            entity.setUuid(UUID.randomUUID());
        }
        entity.setName(this.name);
        return entity;
    }
}
