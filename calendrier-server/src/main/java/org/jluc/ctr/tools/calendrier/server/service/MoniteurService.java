package org.jluc.ctr.tools.calendrier.server.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import org.jluc.ctr.tools.calendrier.server.model.moniteurs.Moniteur;

@ApplicationScoped
public class MoniteurService {

    public Moniteur getMoniteurByUUID(String uuid) {
        List<Moniteur> moniteurs = Moniteur.listAll();
        Moniteur result = null;
        for (Moniteur moniteur : moniteurs) {
            if (moniteur.getUUID().toString().equalsIgnoreCase(uuid)) {
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