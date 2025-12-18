package org.jluc.ctr.tools.calendrier.server.websockets.messages;

public class InfoMessage extends AbstractWebSocketMessage {
    public InfoMessage(String title, String message) {
        this.type = WebSocketMessageType.INFO;
        this.title = title;
        this.message = message;
    }

    public InfoMessage(String message){
        this.type = WebSocketMessageType.INFO;
        this.title = "INFO";
        this.message = message;
    }
}
