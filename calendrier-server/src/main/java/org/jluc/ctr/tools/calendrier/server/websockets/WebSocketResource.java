package org.jluc.ctr.tools.calendrier.server.websockets;

import java.util.HashMap;

import org.jluc.ctr.tools.calendrier.server.websockets.messages.InfoMessage;
import org.jluc.ctr.tools.calendrier.server.websockets.messages.WebSocketMessage;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
@ApplicationScoped
public class WebSocketResource {

    protected final HashMap<Integer, Session> sessions = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.hashCode(), session);
        broadcast(new InfoMessage("User joined"));
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.hashCode());
        broadcast(new InfoMessage("User left"));
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        broadcast(new InfoMessage("User left on error: " + exception));
    }

    @OnMessage
    public void OnMessage(String message) {
        broadcast(new InfoMessage(">> " + message));
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
