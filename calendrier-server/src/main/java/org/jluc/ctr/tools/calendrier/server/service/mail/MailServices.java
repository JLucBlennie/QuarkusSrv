package org.jluc.ctr.tools.calendrier.server.service.mail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

        public void sendValidationMessage(Evenement event) throws IOException {
                String htmlValidationMsg = DICO_PROPERTIES.getString("app.mail.validation");
                // Récupération du compte
                String subject = MessageFormat.format("Validation de la demande de {0}", event.getType().getName());
                byte[] logoBytes;
                try (InputStream in = MailServices.class.getResourceAsStream(SPLASH_IMAGE_PATH)) {
                        if (in == null) {
                                throw new FileNotFoundException("Problème de chargement du logo");
                        }
                        logoBytes = in.readAllBytes();
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
                                                                logoBytes, // Fichier de l'image
                                                                "image/png", // Type MIME de l'image
                                                                "LogoCTR")
                                                .addCc("presidente-technique@cibpl.fr")
                                                .addCc("webmaster-technique@cibpl.fr"));
        }

        public void sendRefuseMessage(Evenement event) throws IOException {
                String htmlValidationMsg = DICO_PROPERTIES.getString("app.mail.refuse");
                // Récupération du compte
                String subject = MessageFormat.format("Refus de la demande de {0}", event.getType().getName());
                byte[] logoBytes;
                try (InputStream in = MailServices.class.getResourceAsStream(SPLASH_IMAGE_PATH)) {
                        if (in == null) {
                                throw new FileNotFoundException("Problème de chargement du logo");
                        }
                        logoBytes = in.readAllBytes();
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
                                                                logoBytes, // Fichier de l'image
                                                                "image/png", // Type MIME de l'image
                                                                "LogoCTR")
                                                .addCc("presidente-technique@cibpl.fr")
                                                .addCc("webmaster-technique@cibpl.fr"));
        }

        public void sendFormsSyncNotificationMail(List<Evenement> newEvents) throws IOException {
                String subject = newEvents.size() + " nouveau(x) évènement(s) détecté(s) sur le Forms";
                byte[] logoBytes;
                try (InputStream in = MailServices.class.getResourceAsStream(SPLASH_IMAGE_PATH)) {
                        if (in == null) {
                                throw new FileNotFoundException("Problème de chargement du logo");
                        }
                        logoBytes = in.readAllBytes();
                }

                String listHtml = newEvents.stream()
                                .map(e -> "<li>" + e.getType().getName() + " - " + e.getDemandeur().getName()
                                                + " (du " + e.getDatedebut() + " au " + e.getDatefin() + ")</li>")
                                .collect(Collectors.joining());

                String htmlMsg = "<p><img src=\"cid:LogoCTR\" width=\"200px\"/>Les évènements suivants ont été ajoutés automatiquement :</p><ul>"
                                + listHtml + "</ul>";

                mailer.send(
                                Mail.withHtml("webmaster-technique@cibpl.fr", subject, htmlMsg)
                                                .addInlineAttachment(
                                                                "Logo CTR", // CID utilisé dans le HTML (ex: "logo")
                                                                logoBytes, // Fichier de l'image
                                                                "image/png", // Type MIME de l'image
                                                                "LogoCTR")
                                                .addCc("presidente-technique@cibpl.fr"));
        }
}
