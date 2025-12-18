package org.jluc.ctr.tools.calendrier.server.websockets.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractWebSocketMessage implements WebSocketMessage {

    protected WebSocketMessageType type;
    protected String title;
    protected String message;

    public WebSocketMessageType getType() {
        return type;
    }

    public void setType(WebSocketMessageType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    @Override
    public String getJson() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
    
}
