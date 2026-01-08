package org.jluc.ctr.tools.calendrier.server.service;

import java.util.ArrayList;
import java.util.List;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.service.google.forms.FormsAccessService;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;

import com.opencsv.exceptions.CsvException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EvenementService {

    public String getEvenementByUUID(String uuid) {
        List<Evenement> evenements = Evenement.listAll();
        String name = "Evènement Not Found ==> " + uuid;
        for (Evenement event : evenements) {
            if (event.getUUID().toString().equalsIgnoreCase(uuid)) {
                name = event.getUUID().toString();
                break;
            }
        }
        return "Evènement " + name;
    }

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
            events = FormsAccessService.getEventsFromGoogleForms(wsResource);
        } catch (CsvException e) {
            wsResource.broadcast(new InfoMessage("[ERREUR]",
                    "Erreur durant la récupération des évènements depuis Google Forms : " + e.getMessage()));
        }
        int nbSaved = 0;
        for (Evenement event : events) {
            if (!evenementExists(event.getEvtidforms())) {
                event.persist();
                nbSaved++;
            }
        }
        return nbSaved;
    }
}