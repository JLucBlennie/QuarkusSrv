package org.jluc.ctr.tools.calendrier.server.resource;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test-mail")
public class MailerRessource {

    @Inject
    Mailer mailer;

    @GET
    @PermitAll
    public String sendTestMail() {
        mailer.send(
                Mail.withText("jean-luc.blondy@cibpl.fr", "Test Quarkus Mailer", "Ceci est un test !"));
        return "Mail envoyé (vérifie les logs) !";
    }

}
