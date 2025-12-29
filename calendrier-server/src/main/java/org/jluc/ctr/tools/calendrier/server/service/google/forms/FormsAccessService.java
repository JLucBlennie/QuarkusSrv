package org.jluc.ctr.tools.calendrier.server.service.google.forms;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormsAccessService {
    public static final ResourceBundle DICO_PROPERTIES = ResourceBundle.getBundle("dicoCTR", Locale.getDefault());
    private static String FORMS_URL = DICO_PROPERTIES.getString("app.forms.url");
    private static String OFFLINE_DATA = DICO_PROPERTIES.getString("app.forms.offline.data");
    private static Logger mLogger = Logger.getLogger(FormsAccessService.class);
    private static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static List<String> ERRORS = new ArrayList<String>();

    public static List<Evenement> getEventsFromGoogleForms() {
        String csvTempFilePath = "./forms.csv";
        File csvTempFile = new File(csvTempFilePath);
        Reader reader = null;
        Writer writer = null;
        List<Evenement> events = new ArrayList<Evenement>();
        try {
            URL url = new URI(FORMS_URL).toURL();
            try {
                String csv = IOUtils.toString(url, StandardCharsets.UTF_8);
                if (csvTempFile.exists()) {
                    boolean fileDeleted = csvTempFile.delete();
                    if (fileDeleted)
                        mLogger.debug("Fichier existant ==> suppression de " + csvTempFile.getAbsolutePath());
                }
                writer = new FileWriter(csvTempFile, StandardCharsets.UTF_8);
                writer.append(csv);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                // On est en mode offline
                csvTempFilePath = OFFLINE_DATA;
            }

            reader = new FileReader(csvTempFilePath, StandardCharsets.UTF_8);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setAllowMissingColumnNames(true).setSkipHeaderRecord(true).build();
            CSVParser records = csvFormat.parse(reader);
            // mLogger.debug(" ==> Headers : " +
            // records.getHeaderMap().toString());
            for (CSVRecord record : records) {
                // Liste de Headers : {Horodateur=0, Adresse e-mail=1, Contact
                // (mails secondaire)=2, Autre contact (si besoin)=3,
                // Activités=4, Date de début (inclus)=5, Date de fin
                // (inclus)=6, Date Intermediaire=7, Demandeur=8, Demandeur (si nouveau)=9, En
                // partenariat avec=10, Lieu=11, Structure support=12, Nombre de
                // stagiaires / candidats (prévisionnel)=13, =18}
                try {
                    Date dateDemande = DATE_TIME_FORMAT.parse(record.get(0));
                    Date dateDebut = DATE_FORMAT.parse(record.get(5));
                    Date dateFin = DATE_FORMAT.parse(record.get(6));
                    String comment = record.get(7);
                    String demandeur = record.get(8).isEmpty() ? record.get(9) : record.get(8);
                    String partenaire = record.get(10);
                    String lieu = record.get(11);
                    String activite = record.get(4);
                    String mail = record.get(1);
                    String organisateur = record.get(12).isEmpty() ? demandeur : record.get(12);
                    int nbparticipants = record.get(13).isEmpty() ? 0 : Integer.parseInt(record.get(13));

                    events.add(new Evenement(dateDemande, dateDebut, dateFin, activite, demandeur, partenaire, mail, lieu, organisateur, comment, nbparticipants));
                } catch (ParseException e) {
                    mLogger.error(
                            "Pb durant le parsing des évènements : " + record.get(4) + " demande par " + (record.get(7).isEmpty() ? record.get(8) : record.get(7)) + " ==> On passe au suivant : ", e);
                    ERRORS.add(record.get(4) + " demande par " + (record.get(7).isEmpty() ? record.get(8) : record.get(7)));
                }
            }
        } catch (IOException | URISyntaxException e) {
            mLogger.error("Problème de lecture du CSV...", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    mLogger.error("Problème de lecture du CSV...", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    mLogger.error("Problème de lecture du CSV...", e);
                }
            }

            if (csvTempFile.exists()) {
                boolean fileDeleted = csvTempFile.delete();
                if (fileDeleted)
                    mLogger.debug("Fichier Temporaire supprime : " + csvTempFile.getAbsolutePath());
            }
        }

        return events;
    }

    public static List<String> getErrors() {
        return ERRORS;
    }
}
