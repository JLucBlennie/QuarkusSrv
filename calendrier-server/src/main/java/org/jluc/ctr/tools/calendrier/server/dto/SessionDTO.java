package org.jluc.ctr.tools.calendrier.server.dto;
import java.util.Date;
import java.util.UUID;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeSession;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;

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
}
