package org.jluc.ctr.tools.calendrier.server.model.club;

import java.text.Normalizer;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Demandeur extends PanacheEntityBase {

    @Id
    private UUID uuid;
    private String name;
    private String numerostructure;
    private String login;

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Demandeur() {
    }

    public Demandeur(String name, String numerostructure) {
        this.name = name;
        this.numerostructure = numerostructure;
        this.login = generateLoginFrom(name);
    }

    public static String generateLoginFrom(String name) {
        if (name == null) {
            return null;
        }
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // retire les accents (é → e, ç → c...)
        return normalized
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "_") // espaces, apostrophes, tirets... → _
                .replaceAll("^_+|_+$", ""); // pas de _ en dé
        // but/fin
    }

    public UUID getUUID() {
        return uuid;
    }
    public String getName() {
        return name;
    }

    public String getNumeroStructure() {
        return numerostructure;
    }

    public String getLogin() {
        return login;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumeroStructure(String numerostructure) {
        this.numerostructure = numerostructure;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isDoublonOf(Demandeur other) {
        return this.name.equals(other.name) && this.numerostructure.equals(other.numerostructure);
    }
}
