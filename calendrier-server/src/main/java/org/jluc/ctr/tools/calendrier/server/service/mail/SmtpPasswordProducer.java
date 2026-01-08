package org.jluc.ctr.tools.calendrier.server.service.mail;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jluc.ctr.tools.calendrier.server.service.mail.pwd.PwdUtils;
import org.jluc.ctr.tools.calendrier.server.service.mail.pwd.StructPwd;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class SmtpPasswordProducer {
    
    @Produces
    @Singleton
    @DefaultBean
    @ConfigProperty(name = "quarkus.mailer.password")
    public String smtpPassword() {
        List<StructPwd> comptes = PwdUtils.getInstance().readPwdFile();

        return comptes.get(0).getPwd(); // Ton code de désérialisation
    }
}