package org.jluc.ctr.tools.calendrier.server.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;

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
}