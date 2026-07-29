package websocketclient;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsCommunicator extends Endpoint{

    private Session session;
    private ServerMessageHandler messageHandler;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServerMessage.class, new ServerMessageAdapter())
            .create();

    public WsCommunicator(String serverURL, ServerMessageHandler messageHandler)
            throws Exception {
        try {
            serverURL = serverURL.replace("http", "ws");
            URI socketURI = new URI(serverURL + "/ws");
            this.messageHandler = messageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    messageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage(), ex);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect() {

    }

    public void makeMove(ChessMove requestedMove) {

    }

    public void leave() {

    }

    public void resign() {

    }
}
