package websocket.messages;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ServerMessageAdapter implements JsonDeserializer<ServerMessage> {

    @Override
    public ServerMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        ServerMessage.ServerMessageType type =
                ServerMessage.ServerMessageType.valueOf(
                        object.get("serverMessageType").getAsString());

        return switch (type) {

            case LOAD_GAME ->
                    context.deserialize(json, LoadGameMessage.class);

            case ERROR ->
                    context.deserialize(json, ErrorMessage.class);

            case NOTIFICATION ->
                    context.deserialize(json, NotificationMessage.class);
        };
    }
}
