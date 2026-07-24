package handler;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;

public class wsGameHandler implements WsMessageHandler {

    private static Gson gson;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;


    public wsGameHandler (AuthDAO auth, GameDAO game) {
        authDAO = auth;
        gameDAO = game;
    }


    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = gson.fromJson(wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leave(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void connect(Session session, String username, ConnectCommand command) {

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

    }

    private void leave(Session session, String username, LeaveGameCommand command) {

    }

    private void resign(Session session, String username, ResignCommand command) {

    }

    private void saveSession(Integer gameId, Session session) {

    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData data = authDAO.getAuth(authToken);
        return data.username();
    }
}
