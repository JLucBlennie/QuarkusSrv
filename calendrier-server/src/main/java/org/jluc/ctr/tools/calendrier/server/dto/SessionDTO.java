package org.jluc.ctr.tools.calendrier.server.dto;
import java.util.Date;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeSession;

import io.quarkus.logging.Log;

public class SessionDTO {
    public UUID uuid;
    public long dateDebut;
    public long dateFin;
    public String typeSession;

    public static SessionDTO fromEntity(Session session) {
        if (session == null) {
            return null;
        }

        SessionDTO dto = new SessionDTO();
        dto.uuid = session.getUuid();
        dto.dateDebut = session.getDateDebut() != null ? session.getDateDebut().getTime() : 0;
        dto.dateFin = session.getDateFin() != null ? session.getDateFin().getTime() : 0;
        dto.typeSession = session.getTypeSession().name();
        return dto;
    }

    public Session toEntity() {
        Log.debug("Conversion d'une session JSON en Session Quarkus");
        Log.debug("SessionDTO : " + this.uuid + ", " + this.dateDebut + ", " + this.dateFin + ", " + this.typeSession);
        Session session = null;
        if (this.uuid != null) {
            session = Session.findById(this.uuid);
            if (session == null) {
                Log.debug("La session n'existe pas en base, création d'une nouvelle session avec l'UUID fourni : "
                        + this.uuid);
                session = new Session();
                session.setUuid(this.uuid);
            }
        } else {
            Log.debug("Aucun UUID fourni pour la session, création d'une nouvelle session avec un UUID généré");
            session = new Session();
            session.setUuid(UUID.randomUUID());
        }
        session.setDateDebut(new Date(this.dateDebut));
        session.setDateFin(new Date(this.dateFin));
        Log.debug("SessionDTO . TypeSession: " + TypeSession.valueOf(this.typeSession));
        session.setTypeSession(TypeSession.valueOf(this.typeSession));
        Log.debug("SessionDTO convertie en Session: " + session.getUuid() + ", " + session.getDateDebut() + ", "
                + session.getDateFin() + ", " + session.getTypeSession());
        return session;
    }
}
