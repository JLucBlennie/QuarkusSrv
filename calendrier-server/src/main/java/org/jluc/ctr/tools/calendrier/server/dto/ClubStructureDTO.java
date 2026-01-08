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
        if (ClubStructure.findById(this.uuid) != null) {
            return ClubStructure.findById(this.uuid);
        }
        ClubStructure entity = new ClubStructure();
        entity.setUuid(this.uuid != null ? this.uuid : UUID.randomUUID());
        entity.setName(this.name);
        return entity;
    }
}
