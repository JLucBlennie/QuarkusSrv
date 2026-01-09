package org.jluc.ctr.tools.calendrier.server.model.evenements;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class TypeEvenement extends PanacheEntityBase {

    @Id
    private UUID uuid;
    private String name;
    private TypeActivite activite;
    private String valeurforms;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public TypeEvenement() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public TypeActivite getActivite() {
        return activite;
    }

    public String getValeurforms() {
        return valeurforms;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActivite(TypeActivite activite) {
        this.activite = activite;
    }

    public void setValeurforms(String valeurforms) {
        this.valeurforms = valeurforms;
    }

    public boolean isDoublonOf(TypeEvenement other) {
        if (other == null) {
            return false;
        }
        return this.name != null && this.name.equals(other.name)
                && this.activite == other.activite;
    }
}
