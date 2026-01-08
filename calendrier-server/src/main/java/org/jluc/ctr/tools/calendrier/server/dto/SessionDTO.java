package org.jluc.ctr.tools.calendrier.server.dto;
import java.util.Date;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeSession;

public class SessionDTO {
    public UUID uuid;
    public Date dateDebut;
    public Date dateFin;
    public TypeSession typeSession;

    public static SessionDTO fromEntity(Session session) {
        if (session == null) {
            return null;
        }

        SessionDTO dto = new SessionDTO();
        dto.uuid = session.getUuid();
        dto.dateDebut = session.getDateDebut();
        dto.dateFin = session.getDateFin();
        dto.typeSession = session.getTypeSession();
        return dto;
    }

    public Session toEntity() {
        Session session = Session.findById(this.uuid);
        if (session == null) {
            session = new Session();
            session.setUuid(this.uuid != null ? this.uuid : UUID.randomUUID());
        }
        session.setDateDebut(this.dateDebut);
        session.setDateFin(this.dateFin);
        session.setTypeSession(this.typeSession);
        return session;
    }
}
