package websocketserver;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.messages.ServerMessage;

import java.util.Objects;

public class WsGameHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static Gson gson;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private final WsConnectionManager wsConn = new WsConnectionManager();


    public WsGameHandler(AuthDAO auth, GameDAO game) {
        authDAO = auth;
        gameDAO = game;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose (WsCloseContext ctx) {
        System.out.println("Websocket Closed");
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
            throw new Exception(ex);
        }
    }

    private void connect(Session session, String username, ConnectCommand command) {
        int gameID = command.getGameID();
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        GameData oldData = gameDAO.getGame(command.getGameID());
        if (oldData.game().validMoves(command.move.getStartPosition()).contains(command.move)){
            oldData.game().makeMove(command.move);

        } else {
            wsConn.broadcastMessage(null, LOAD_GAME, null);
            throw new Exception ("Invalid move sent to server");
        }
    }

    private void leave(Session session, String username, LeaveGameCommand command) throws DataAccessException {
        GameData oldData = gameDAO.getGame(command.getGameID());
        GameData data = new GameData(oldData.gameID()
                , Objects.equals(oldData.whiteUsername(), username) ? null : oldData.whiteUsername()
                , Objects.equals(oldData.blackUsername(), username) ? null : oldData.blackUsername()
                , oldData.gameName(), oldData.game());
        gameDAO.updateGame(data);
        wsConn.removeSessionFromGame(data.gameID(), session);
        wsConn.broadcastMessage(session, NOTIFICATION, "User " + username + " has left the game.");
    }

    private void resign(Session session, String username, ResignCommand command) throws DataAccessException {
        GameData oldData = gameDAO.getGame(command.getGameID());
        ChessGame.TeamColor team = Objects.equals(oldData.whiteUsername(), username) ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor winTeam = Objects.equals(oldData.whiteUsername(), username) ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
        oldData.game().setGameEnd(team.name() + " has decided to resign, " + winTeam + " has won by default");
        GameData data = new GameData(oldData.gameID(), oldData.whiteUsername(), oldData.blackUsername(), oldData.gameName(), oldData.game());
        gameDAO.updateGame(data);
        wsConn.broadcastMessage(session, NOTIFICATION, "User " + username + " has decided to resign.");
    }

    private void saveSession(Integer gameId, Session session) {

    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData data = authDAO.getAuth(authToken);
        return data.username();
    }
}
