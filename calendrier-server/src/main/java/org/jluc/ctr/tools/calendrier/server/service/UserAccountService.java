package org.jluc.ctr.tools.calendrier.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserAccountService {

    @ConfigProperty(name = "quarkus.security.users.file.users")
    String usersFile; // récupère "users.properties" depuis application.properties

    public Set<String> getDeclaredUsernames() throws IOException {
        Properties props = new Properties();
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(usersFile)) {
            if (is == null) {
                throw new IOException("Fichier introuvable dans le classpath : " + usersFile);
            }
            props.load(is);
        }
        return props.stringPropertyNames(); // les usernames = les clés du fichier
    }
}