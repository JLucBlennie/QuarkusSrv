package org.jluc.ctr.tools.calendrier.server.club;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class ClubStructure extends PanacheEntity {

    private String uuid;
    private String name;

    public ClubStructure() {
        uuid = UUID.randomUUID().toString();
    }

    public ClubStructure(String uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public ClubStructure(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
    }

    public String toString() {
        return "ClubStructure [uuid = " + uuid + ", name = " + name + "]";
    }
}
