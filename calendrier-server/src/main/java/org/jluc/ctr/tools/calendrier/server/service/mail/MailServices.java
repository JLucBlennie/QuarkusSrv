package org.jluc.ctr.tools.calendrier.server.service.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jluc.ctr.tools.calendrier.server.model.evenements.Evenement;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailServices {
    @Inject
    Mailer mailer;

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    public static final ResourceBundle DICO_PROPERTIES = ResourceBundle.getBundle("dicoCTR", Locale.getDefault());
    public static final String SPLASH_IMAGE_PATH = "/images/logo.png";

    public void sendValidationMessage(Evenement event)
            throws MalformedURLException, FileNotFoundException, URISyntaxException {
        String htmlValidationMsg = DICO_PROPERTIES.getString("app.mail.validation");
        // Récupération du compte
        String subject = MessageFormat.format("Validation de la demande de {0}", event.getType().getName());
        URL url = MailServices.class.getResource(SPLASH_IMAGE_PATH);
        if (url == null) {
            String msg = "Problème de chargement du logo";
            throw new FileNotFoundException(msg);
        }
        // set the html message
        String htmlMsg = MessageFormat.format(htmlValidationMsg,
                "LogoCTR", event.getType().getName(),
                DATE_FORMAT.format(event.getDatedemande()), DATE_FORMAT.format(event.getDatedebut()),
                DATE_FORMAT.format(event.getDatefin()), event.getDemandeur().getName(),
                DATE_FORMAT.format(event.getDatevalidation()));
        mailer.send(
                Mail.withHtml(event.getMailcontact(), subject, htmlMsg)
                        .addInlineAttachment(
                                "Logo CTR", // CID utilisé dans le HTML (ex: "logo")
                                new File(url.toURI()), // Fichier de l'image
                                "image/png", // Type MIME de l'image
                                "LogoCTR")
                        .addCc("presidente-technique@cibpl.fr")
                        .addCc("webmaster-technique@cibpl.fr"));
    }
}
