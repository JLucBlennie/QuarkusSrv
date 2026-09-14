package org.jluc.ctr.tools.calendrier.server.websockets;

import java.util.HashMap;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.WebSocketMessage;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkus.logging.Log;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
@ApplicationScoped
public class WebSocketResource {

    @Inject
    JWTParser jwtParser;

    protected final HashMap<Integer, Session> sessions = new HashMap<>();
    protected final HashMap<Integer, JsonWebToken> identities = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws java.io.IOException {
        String token = extractToken(session);
        if (token == null) {
            session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Token manquant"));
            return;
        }
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            sessions.put(session.hashCode(), session);
            identities.put(session.hashCode(), jwt);
            broadcast(new InfoMessage(jwt.getName() + " a rejoint"));
        } catch (ParseException e) {
            Log.warn("Connexion WS refusée, token invalide : " + e.getMessage());
            session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "Token invalide"));
        }
    }

    private String extractToken(Session session) {
        var params = session.getRequestParameterMap().get("token");
        return (params == null || params.isEmpty()) ? null : params.get(0);
    }

    @OnClose
    public void onClose(Session session) {
        JsonWebToken jwt = identities.remove(session.hashCode());
        sessions.remove(session.hashCode());
        broadcast(new InfoMessage((jwt != null ? jwt.getName() : "Un utilisateur") + " a quitté"));
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        Log.error("Erreur WS pour la session " + session.getId(), exception);
        sessions.remove(session.hashCode());
        identities.remove(session.hashCode());
    }

    @OnMessage
    public void onMessage(String message) {
        // Le front n'a pas besoin d'envoyer de messages pour l'instant :
        // on n'expose plus de rebroadcast public (c'était une porte ouverte au spam).
        Log.debug("Message WS entrant ignoré : " + message);
    }

    public void broadcast(WebSocketMessage wsmessage) {
        sessions.values().forEach(s -> {
            try {
                s.getAsyncRemote().sendObject(wsmessage.getJson(), result -> {
                    if (result.getException() != null) {
                        Log.error("Unable to send message : " + result.getException());
                    }
                });
            } catch (JsonProcessingException e) {
                Log.error("Error in WebSocketEndpoint:broadcast: ", e);
            }
        });
    }
}
