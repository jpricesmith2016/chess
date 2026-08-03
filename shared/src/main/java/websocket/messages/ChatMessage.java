package websocket.messages;

public class ChatMessage extends ServerMessage{
    public String message;
    public ChatMessage(String message){
        super(ServerMessageType.CHAT);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
