package org.jluc.ctr.tools.calendrier.server.websockets.messages;

public class ProgressMessage extends AbstractWebSocketMessage {

    protected int progressValue;
    protected String processKey;

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int value) {
        this.progressValue = value;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String key) {
        this.processKey = key;
    }

    public ProgressMessage(boolean blocking, String processKey, String title, String message, int progressValue) {
        this.type = blocking ? WebSocketMessageType.PROGRESSBLOCKING : WebSocketMessageType.PROGRESS;
        this.title = title;
        this.message = message;
        this.progressValue = progressValue;
        this.processKey = processKey;
    }

    public ProgressMessage(boolean blocking, String processKey, String message, int progressValue) {
        this.type = blocking ? WebSocketMessageType.PROGRESSBLOCKING : WebSocketMessageType.PROGRESS;
        this.title = "PROGRESS";
        this.message = message;
        this.progressValue = progressValue;
        this.processKey = processKey;
    }

    public ProgressMessage(boolean blocking, String processKey, int progressValue) {
        this.type = blocking ? WebSocketMessageType.PROGRESSBLOCKING : WebSocketMessageType.PROGRESS;
        this.title = "PROGRESS";
        this.message = "";
        this.progressValue = progressValue;
        this.processKey = processKey;
    }
}
