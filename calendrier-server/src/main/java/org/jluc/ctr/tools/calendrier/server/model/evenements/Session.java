package org.jluc.ctr.tools.calendrier.server.model.evenements;

import java.util.Date;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Session extends PanacheEntityBase {
    @Id
    private UUID uuid;
    @ManyToOne
    private Evenement evenement;
    private Date dateDebut;
    private Date dateFin;
    private TypeSession typeSession;
    
    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public Session() {
    }

    public Session(Date debut, Date fin, TypeSession typeSession) {
        this.dateDebut = debut;
        this.dateFin = fin;
        this.typeSession = typeSession;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public UUID getUuid() {
        return uuid;
    }   

    public TypeSession getTypeSession() {
        return typeSession;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public void setTypeSession(TypeSession typeSession) {
        this.typeSession = typeSession;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }
}
