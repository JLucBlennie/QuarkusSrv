package org.jluc.ctr.tools.calendrier.server.evenements;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EvenementService {

    public String getEvenementByUUID(String uuid) {
        List<Evenement> evenements = Evenement.listAll();
        String name = "Evènement Not Found ==> " + uuid;
        for (Evenement event : evenements) {
            if (event.getUUID().equalsIgnoreCase(uuid)) {
                name = event.getName();
                break;
            }
        }
        return "Evènement " + name;
    }

    public String getEvenementByName(String name) {
        List<Evenement> evenements = Evenement.listAll();
        String uuid = "Evènement non trouvéFound ==> " + name;
        for (Evenement event : evenements) {
            if (event.getName().equalsIgnoreCase(name)) {
                uuid = event.getUUID();
                break;
            }
        }
        return "Evènement " + name + " --> UUID : " + uuid;
    }

}