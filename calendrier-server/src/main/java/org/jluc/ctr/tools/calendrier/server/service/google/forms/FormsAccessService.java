package org.jluc.ctr.tools.calendrier.server.service.google.forms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jboss.logging.Logger;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Session;
import org.jluc.ctr.tools.calendrier.server.model.evenements.TypeSession;
import org.jluc.ctr.tools.calendrier.server.websockets.WebSocketResource;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.ProgressMessage;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormsAccessService {

    public static final ResourceBundle DICO_PROPERTIES = ResourceBundle.getBundle("dicoCTR", Locale.getDefault());
    private static String FORMS_URL = DICO_PROPERTIES.getString("app.forms.url");
    private static Logger mLogger = Logger.getLogger(FormsAccessService.class);
    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static List<String> ERRORS = new ArrayList<String>();

    public List<Evenement> getEventsFromGoogleForms(WebSocketResource wsResource) throws CsvException {
        List<Evenement> events = new ArrayList<Evenement>();
        wsResource.broadcast(new ProgressMessage(true, "loadevents",
                "Récupération des évènements de Forms...", 0));
        try {
            URL url = new URI(FORMS_URL).toURL();
            InputStream urlStream = url.openStream();
            InputStreamReader urlReader = new InputStreamReader(urlStream, StandardCharsets.UTF_8);
            CSVReader csvReader = new CSVReader(urlReader);

            List<String[]> records = csvReader.readAll();
            mLogger.debug(" ==> Headers : " +
                    records.get(0).toString());
            int nbEvents = 0;
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                // Liste de Headers : {Horodateur=0, Adresse e-mail=1, Contact
                // (mails secondaire)=2, Autre contact (si besoin)=3,
                // Activités=4, Date de début (inclus)=5, Date de fin
                // (inclus)=6, Date Intermediaire=7, Demandeur=8, Demandeur (si nouveau)=9, En
                // partenariat avec=10, Lieu=11, Structure support=12, Nombre de
                // stagiaires / candidats (prévisionnel)=13, =18}
                try {
                    Date dateDemande = DATE_TIME_FORMAT.parse(record[0]);
                    Date dateDebut = DATE_FORMAT.parse(record[5]);
                    Date dateFin = DATE_FORMAT.parse(record[6]);
                    String comment = record[7];
                    String demandeur = (record[8].isEmpty() || record[8].equals("Autre")) ? record[9] : record[8];
                    String partenaire = record[10];
                    String lieu = record[11];
                    String activite = record[4];
                    String mail = record[1];
                    String organisateur = record[12].isEmpty() ? demandeur : record[12];
                    int nbparticipants = record[13].isEmpty() ? 0 : Integer.parseInt(record[13]);
                    Evenement eventToAdd = new Evenement(dateDemande, dateDebut, dateFin, activite, demandeur,
                            partenaire, mail,
                            lieu, organisateur, comment, nbparticipants);
                    // Gestion des sessions - ici une seule session par évènement
                    List<Session> sessions = new ArrayList<Session>();
                    Session session = new Session(dateDebut, dateFin, TypeSession.PRESENTIEL);
                    sessions.add(session);
                    eventToAdd.setSessions(sessions);

                    // Ajout de l'évènement à la liste
                    events.add(eventToAdd);
                    nbEvents++;
                    wsResource.broadcast(new ProgressMessage(true, "loadevents",
                            "Récupération des " + nbEvents + "évènements de Forms...",
                            (int) (nbEvents / records.size() * 100)));
                } catch (ParseException e) {
                    mLogger.error(
                            "Pb durant le parsing des évènements : " + record[4] + " demande par "
                                    + (record[7].isEmpty() ? record[8] : record[7])
                                    + " ==> On passe au suivant : ",
                            e);
                    ERRORS.add(record[4] + " demande par "
                            + (record[7].isEmpty() ? record[8] : record[7]));
                } finally {
                    csvReader.close();
                }
            }
        } catch (IOException | URISyntaxException e) {
            mLogger.error("Problème de lecture du CSV...", e);
        } finally {
            wsResource.broadcast(new ProgressMessage(true, "loadevents",
                    "Récupération des évènements de Forms terminée.", 100));
        }

        return events;
    }

    public List<String> getErrors() {
        return ERRORS;
    }
}
