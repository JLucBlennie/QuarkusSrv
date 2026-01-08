package org.jluc.ctr.tools.calendrier.server.service;

import java.util.List;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.service.google.forms.FormsAccessService;

import jakarta.enterprise.context.ApplicationScoped;

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

    public int updateEvenementsFromGoogleForms() {
        List<Evenement> events = FormsAccessService.getEventsFromGoogleForms();
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