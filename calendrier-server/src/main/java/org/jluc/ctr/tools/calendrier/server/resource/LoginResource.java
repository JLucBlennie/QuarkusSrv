package org.jluc.ctr.tools.calendrier.server.resource;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import io.quarkus.logging.Log;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.Response;

public class LoginResource {

    @POST
    @PermitAll // Accessible sans authentification
    public Response login(LoginRequest request) {
        // Vérification basique (en production, utilisez un système plus robuste)
        if (!isValidUser(request.username, request.password)) {
            return Response.status(401).entity("Identifiants invalides").build();
        }

        // Génération du JWT avec les rôles de l'utilisateur
        String token = Jwt.issuer("https://ctr.blondy29.ovh")
                .upn(request.username)
                .groups(getRoles(request.username))
                .expiresIn(Duration.ofHours(24))
                .sign();

        Log.infof("Connexion réussie pour {0} avec rôles : {1}", request.username, getRoles(request.username));
        return Response.ok(new LoginResponse(token)).build();
    }

    private boolean isValidUser(String username, String password) {
        // Logique de validation (exemple simplifié)
        return ("admin".equals(username) && "admin123".equals(password)) ||
                ("user".equals(username) && "user123".equals(password));
    }

    private Set<String> getRoles(String username) {
        // Récupération des rôles depuis roles.properties
        Set<String> roles = new HashSet<>();
        if ("admin".equals(username)) {
            roles.add("admin");
            roles.add("manager"); // Rôle personnalisé
        } else {
            roles.add("user");
        }
        return roles;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class LoginResponse {
        public String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}