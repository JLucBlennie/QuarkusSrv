package org.jluc.ctr.tools.calendrier.server.resource;

import java.util.Map;
import java.util.Set;

import org.jluc.ctr.tools.calendrier.server.dto.LoginRequest;
import org.jluc.ctr.tools.calendrier.server.service.JwtService;

import io.quarkus.security.credential.PasswordCredential;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    JwtService jwtService;

    @Inject
    IdentityProviderManager identityProviderManager;

    @POST
    @Path("/login")
    @PermitAll // ← cet endpoint est public, pas besoin d'être connecté !
    public Uni<Response> login(LoginRequest request) {

        // Vérifie les credentials via le fichier users.properties
        var authRequest = new UsernamePasswordAuthenticationRequest(
                request.username,
                new PasswordCredential(
                        request.password.toCharArray()));

        return identityProviderManager.authenticate(authRequest)
                .map(identity -> {
                    // Récupère les rôles de l'utilisateur
                    Set<String> roles = identity.getRoles();

                    // Génère le token JWT
                    String token = jwtService.generateToken(request.username, roles);

                    return Response.ok(Map.of(
                            "token", token,
                            "username", request.username,
                            "roles", roles)).build();
                })
                .onFailure().recoverWithItem(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(Map.of("error", "Identifiants incorrects"))
                                .build());
    }
}