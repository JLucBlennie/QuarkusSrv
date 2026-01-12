package org.jluc.ctr.tools.calendrier.server.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.EvenementRepository;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;
import org.jluc.ctr.tools.calendrier.server.service.google.forms.FormsAccessService;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;

import com.opencsv.exceptions.CsvException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EvenementService {
    @Inject
    EvenementRepository evenementRepository;

    @Inject
    FormsAccessService formsAccessService;

    private boolean evenementExists(String evtIdForms) {
        List<Evenement> evenements = Evenement.listAll();
        for (Evenement event : evenements) {
            if (event.getEvtidforms().equalsIgnoreCase(evtIdForms)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public int updateEvenementsFromGoogleForms(WebSocketResource wsResource) {
        List<Evenement> events = new ArrayList<Evenement>();
        try {
            events = formsAccessService.getEventsFromGoogleForms(wsResource);
            if (formsAccessService.getErrors().size() > 0) {
                for (String error : formsAccessService.getErrors()) {
                    wsResource.broadcast(new InfoMessage("[ERREUR] Récupération depuis Google Forms", error));
                }
            }
        } catch (CsvException e) {
            wsResource.broadcast(new InfoMessage("[ERREUR]",
                    "Erreur durant la récupération des évènements depuis Google Forms : " + e.getMessage()));
        }
        int nbSaved = 0;
        for (Evenement event : events) {
            if (!evenementExists(event.getEvtidforms())) {
                event.persist();
                for (int iSession = 0; iSession < event.getSessions().size(); iSession++) {
                    event.getSessions().get(iSession).setEvenement(event);
                    event.getSessions().get(iSession).persist();
                }
                nbSaved++;
            }
        }
        return nbSaved;
    }

    public List<Evenement> getExamensFor(Moniteur moniteur) {
        List<Evenement> evenements = evenementRepository.findAllWithPresidentDeleguer();
        List<Evenement> evenementsPresidentJury = evenements.stream()
                .filter(e -> e.getPresidentjury() != null && e.getPresidentjury().equals(moniteur))
                .toList();
        List<Evenement> evenementsDeleguerCTR = evenements.stream()
                .filter(e -> e.getDeleguectr() != null && e.getDeleguectr().equals(moniteur))
                .toList();
        List<Evenement> allEvenements = new ArrayList<Evenement>();
        allEvenements.addAll(evenementsPresidentJury);
        allEvenements.addAll(evenementsDeleguerCTR);
        return allEvenements;
    }

    public int getAnnee(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}