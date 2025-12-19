package org.jluc.ctr.tools.calendrier.server.service;

import java.util.List;
import java.util.UUID;

import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MoniteurService {

    public Moniteur getMoniteurByUUID(UUID uuid) {
        List<Moniteur> moniteurs = Moniteur.listAll();
        Moniteur result = null;
        for (Moniteur moniteur : moniteurs) {
            if (moniteur.getUUID().equals(uuid)) {
                result = moniteur;
                break;
            }
        }
        return result;
    }

    public Moniteur getMoniteurByName(String name) {
        List<Moniteur> moniteurs = Moniteur.listAll();
        Moniteur result = null;
        for (Moniteur moniteur : moniteurs) {
            if (moniteur.getLastName().equalsIgnoreCase(name)) {
                result = moniteur;
                break;
            }
        }
        return result;
    }

}