package org.jluc.ctr.tools.calendrier.server.service.export;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.EvenementRepository;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Status;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeActivite;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeEvenement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EvenementExportService {

        private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
        private static final DateTimeFormatter JOUR = DateTimeFormatter
                        .ofPattern("EEE dd/MM/yyyy", Locale.FRANCE).withZone(PARIS);

        @Inject
        EvenementRepository evenementRepository;

        @Transactional
        public String genererHtml(int mois) {
                Date debut = new Date();
                // import java.time.YearMonth;
                LocalDate finPeriode = YearMonth.now(PARIS).plusMonths(mois).atEndOfMonth();
                Date fin = Date.from(finPeriode.atTime(LocalTime.MAX).atZone(PARIS).toInstant());

                List<Evenement> evenements = evenementRepository.findAVenir(Status.VALIDE, debut, fin);

                evenements.sort(Comparator
                                .comparing((Evenement e) -> e.getType() != null ? e.getType().getName() : "",
                                                Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(Evenement::getDatedebut,
                                                Comparator.nullsLast(Comparator.naturalOrder())));

                Map<TypeActivite, List<LigneExport>> groupes = new EnumMap<>(TypeActivite.class);
                List<LigneExport> sansActivite = new ArrayList<>();
                for (Evenement e : evenements) {
                        LigneExport ligne = versLigne(e);
                        TypeEvenement type = e.getType();
                        if (type == null || type.getActivite() == null) {
                                sansActivite.add(ligne);
                        } else {
                                groupes.computeIfAbsent(type.getActivite(), k -> new ArrayList<>()).add(ligne);
                        }
                }

                Map<String, List<LigneExport>> parActivite = new LinkedHashMap<>();
                groupes.forEach((activite, lignes) -> parActivite.put(activite.toString(), lignes));
                if (!sansActivite.isEmpty()) {
                        parActivite.put("Autre", sansActivite);
                }

                String periode = jour(debut) + " → " + jour(fin);
                return ExportTemplates.evenements(parActivite, periode).render();
        }

        private LigneExport versLigne(Evenement e) {
                List<String> sessions;
                if (e.getSessions() == null || e.getSessions().isEmpty()) {
                        sessions = List.of(formatPeriode(e.getDatedebut(), e.getDatefin()));
                } else {
                        sessions = e.getSessions().stream()
                                        .sorted(Comparator.comparing(Session::getDateDebut,
                                                        Comparator.nullsLast(Comparator.naturalOrder())))
                                        .map(s -> formatPeriode(s.getDateDebut(), s.getDateFin()))
                                        .toList();
                }
                String demandeur = e.getDemandeur() != null
                                ? Objects.toString(e.getDemandeur().getName(), "") // à confirmer
                                : "";
                String type = e.getType() != null ? Objects.toString(e.getType().getName(), "") : "";
                return new LigneExport(type, demandeur, Objects.toString(e.getLieu(), ""), sessions);
        }

        private static String formatPeriode(Date debut, Date fin) {
                if (debut == null) {
                        return "";
                }
                String d = jour(debut);
                if (fin == null) {
                        return d;
                }
                String f = jour(fin);
                return d.equals(f) ? d : d + " → " + f;
        }

        private static String jour(Date date) {
                return JOUR.format(Instant.ofEpochMilli(date.getTime()));
        }
}