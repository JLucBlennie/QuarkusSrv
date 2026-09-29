package org.jluc.ctr.tools.calendrier.server.scheduler;

import java.io.IOException;
import java.util.List;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.service.EvenementService; // adapte le nom réel
import org.jluc.ctr.tools.calendrier.server.service.mail.MailServices;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FormsSyncScheduler {

    @Inject
    EvenementService service; // adapte selon le vrai nom de la classe contenant
                              // updateEvenementsFromGoogleForms

    @Inject
    WebSocketResource wsResource;

    @Inject
    MailServices mailServices;

    @Inject
    Mailer mailer;

    @Scheduled(every = "48h")
    void syncFormsPeriodically() {
        Log.info("Démarrage de la synchronisation planifiée des évènements Forms...");
        List<Evenement> newEvents = service.updateEvenementsFromGoogleForms(wsResource);
        Log.info(newEvents.size() + " nouveaux évènements ajoutés via la synchronisation planifiée.");

        if (!newEvents.isEmpty()) {
            try {
                mailServices.sendFormsSyncNotificationMail(newEvents);
            } catch (IOException e) {
                Log.error("Erreur lors de l'envoi de la notification de synchronisation des évènements Forms", e);
            }
        }
    }

}