package org.jluc.ctr.tools.calendrier.server.moniteurs;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MoniteurService {

    public String getMoniteurByUUID(String uuid) {
        List<Moniteur> moniteurs = Moniteur.listAll();
        String name = "Mono Not Found ==> " + uuid;
        for (Moniteur moniteur : moniteurs) {
            if (moniteur.getUUID().equalsIgnoreCase(uuid)) {
                name = moniteur.getLastname();
                break;
            }
        }
        return "Moniteur " + name;
    }

    public String getMoniteurByName(String name) {
        List<Moniteur> moniteurs = Moniteur.listAll();
        String uuid = "Mono non trouvé ==> " + name;
        for (Moniteur moniteur : moniteurs) {
            if (moniteur.getLastname().equalsIgnoreCase(name)) {
                uuid = moniteur.getUUID();
                break;
            }
        }
        return "Moniteur " + name + " --> UUID : " + uuid;
    }

}