package websocketclient;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageAdapter;
import websocket.commands.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WsCommunicator extends Endpoint{

    private Session session;
    private int gameID;
    private String authToken;
    private ServerMessageHandler messageHandler;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ServerMessage.class, new ServerMessageAdapter())
            .create();

    public WsCommunicator(String serverURL, ServerMessageHandler messageHandler, int gameID)
            throws Exception {
        try {
            serverURL = serverURL.replace("http", "ws");
            URI socketURI = new URI(serverURL + "/ws");
            this.gameID = gameID;
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

    public void connect(String authToken) throws Exception {
        try {
            this.authToken = authToken;
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (Exception e) {
            throw new Exception ("Failed to connect: " + e.getMessage(), e);
        }
    }

    public void makeMove(ChessMove requestedMove) throws Exception {
        try {
            MakeMoveCommand move = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, requestedMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(move));
        } catch (Exception e) {
            throw new Exception ("Failed to make move: " + e.getMessage(), e);
        }
    }

    public void leave() throws Exception {
        try {
            UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leave));
        } catch (Exception e) {
            throw new Exception ("Failed to leave game: " + e.getMessage(), e);
        }
    }

    public void resign() throws Exception {
        try {
            UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resign));
        } catch (Exception e) {
            throw new Exception ("Failed to resign: " + e.getMessage(), e);
        }
    }
}
