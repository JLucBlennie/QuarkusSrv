package org.jluc.ctr.tools.calendrier.server.websockets.messages;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface WebSocketMessage {
    public String getJson() throws JsonProcessingException;
}
