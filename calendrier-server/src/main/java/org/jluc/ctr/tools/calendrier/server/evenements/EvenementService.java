package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.ArrayList;
import java.util.List;

import org.jluc.ctr.tools.calendrier.server.evenements.json.EvenementJSON;

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

    public EvenementJSON toJSON(Evenement evenement) {
        EvenementJSON evenementjson = new EvenementJSON(evenement);
        return evenementjson;
    }

    public List<EvenementJSON> toJSON(List<Evenement> evenements) {
        List<EvenementJSON> evenementsJSON = new ArrayList<EvenementJSON>();
        for (Evenement evenement : evenements) {
            evenementsJSON.add(toJSON(evenement));
        }
        return evenementsJSON;
    }
}