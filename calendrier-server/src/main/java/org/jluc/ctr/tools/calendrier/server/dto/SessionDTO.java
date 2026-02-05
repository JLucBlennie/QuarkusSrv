package org.jluc.ctr.tools.calendrier.server.dto;
import java.util.Date;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeSession;

import io.quarkus.logging.Log;

public class SessionDTO {
    public UUID uuid;
    public Date dateDebut;
    public Date dateFin;
    public String typeSession;

    public static SessionDTO fromEntity(Session session) {
        if (session == null) {
            return null;
        }

        SessionDTO dto = new SessionDTO();
        dto.uuid = session.getUuid();
        dto.dateDebut = session.getDateDebut();
        dto.dateFin = session.getDateFin();
        dto.typeSession = session.getTypeSession().name();
        return dto;
    }

    public Session toEntity() {
        Log.debug("Conversion d'une session JSON en Session Quarkus");
        Log.debug("SessionDTO: " + this.uuid + ", " + this.dateDebut + ", " + this.dateFin + ", " + this.typeSession);
        Session session = null;
        if (this.uuid != null) {
            session = Session.findById(this.uuid);
            if (session == null) {
                session = new Session();
                session.setUuid(this.uuid);
            }
        } else {
            session = new Session();
            session.setUuid(UUID.randomUUID());
        }
        session.setDateDebut(this.dateDebut);
        session.setDateFin(this.dateFin);
        Log.debug("SessionDTO . TypeSession: " + TypeSession.valueOf(this.typeSession));
        session.setTypeSession(TypeSession.valueOf(this.typeSession));
        Log.debug("SessionDTO convertie en Session: " + session.getUuid() + ", " + session.getDateDebut() + ", "
                + session.getDateFin() + ", " + session.getTypeSession());
        return session;
    }
}
