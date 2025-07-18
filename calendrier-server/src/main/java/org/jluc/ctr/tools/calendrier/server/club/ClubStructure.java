package org.jluc.ctr.tools.calendrier.server.club;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class ClubStructure extends PanacheEntityBase {
    @Id
    private UUID uuid;
    private String name;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public ClubStructure() {
    }

    public ClubStructure(String name) {
        this.name = name;
    }

    public String toString() {
        return "ClubStructure [uuid = " + uuid + ", name = " + name + "]";
    }
}
