package websocket.messages;

public class MoveMadeMessage extends ServerMessage{
    public String message;
    public MoveMadeMessage(String message){
        super(ServerMessageType.MOVE_MADE);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}