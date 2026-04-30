package org.jluc.ctr.tools.calendrier.server.service;

import java.time.Duration;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(String username, Set<String> roles) {
        return Jwt.issuer(issuer)
                .subject(username)
                .groups(roles) // les rôles
                .expiresIn(Duration.ofHours(8)) // durée de validité
                .sign(); // signe avec privateKey.pem
    }
}
