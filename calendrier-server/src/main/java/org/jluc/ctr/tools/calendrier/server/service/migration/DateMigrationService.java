package org.jluc.ctr.tools.calendrier.server.service.migration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DateMigrationService {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");

    @Transactional
    public MigrationReport fixAllDates() {
        MigrationReport report = new MigrationReport();

        List<Session> sessions = Session.listAll();
        for (Session s : sessions) {
            Date oldDebut = s.getDateDebut();
            Date oldFin = s.getDateFin();
            Date newDebut = fixDateToUtcMidnight(oldDebut);
            Date newFin = fixDateToUtcMidnight(oldFin);

            boolean changed = !oldDebut.equals(newDebut) || !oldFin.equals(newFin);
            Log.info(String.format("[Session #%s] debut: %s -> %s | fin: %s -> %s | %s",
                    s.getUuid(), oldDebut, newDebut, oldFin, newFin,
                    changed ? "MODIFIÉ" : "inchangé"));

            s.setDateDebut(newDebut);
            s.setDateFin(newFin);

            report.sessionsTotal++;
            if (changed)
                report.sessionsModified++;
        }

        List<Evenement> evenements = Evenement.listAll();
        for (Evenement e : evenements) {
            Date oldDebut = e.getDatedebut();
            Date oldFin = e.getDatefin();
            Date newDebut = fixDateToUtcMidnight(oldDebut);
            Date newFin = fixDateToUtcMidnight(oldFin);

            boolean changed = !oldDebut.equals(newDebut) || !oldFin.equals(newFin);
            Log.info(String.format("[Evenement #%s] debut: %s -> %s | fin: %s -> %s | %s",
                    e.getUUID(), oldDebut, newDebut, oldFin, newFin,
                    changed ? "MODIFIÉ" : "inchangé"));

            e.setDatedebut(newDebut);
            e.setDatefin(newFin);

            report.evenementsTotal++;
            if (changed)
                report.evenementsModified++;
        }

        Log.info("Migration des dates terminée : " + report);
        return report;
    }

    private Date fixDateToUtcMidnight(Date original) {
        LocalDate correctLocalDay = original.toInstant()
                .atZone(PARIS)
                .toLocalDate();
        return Date.from(correctLocalDay.atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    public static class MigrationReport {
        public int sessionsTotal;
        public int sessionsModified;
        public int evenementsTotal;
        public int evenementsModified;

        @Override
        public String toString() {
            return String.format(
                    "Sessions: %d/%d modifiées | Evenements: %d/%d modifiés",
                    sessionsModified, sessionsTotal, evenementsModified, evenementsTotal);
        }
    }
}