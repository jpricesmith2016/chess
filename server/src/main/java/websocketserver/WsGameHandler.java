package websocketserver;

import chess.ChessGame;
import chess.ChessMove;
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
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Objects;

public class WsGameHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static final Gson gson = new Gson();
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
    public void handleClose (@NotNull WsCloseContext ctx) {
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
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, gson.fromJson(wsMessageContext.message(), MakeMoveCommand.class));
                case LEAVE -> leave(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            wsConn.sendMessage(session, new ErrorMessage(ex.getMessage()));
        }
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        try {
            if (gameDAO.getGame(command.getGameID()) == null) {
                wsConn.sendMessage(session, new ErrorMessage("Invalid gameID"));
                return;
            }
            if (!authDAO.containsAuthToken(command.getAuthToken())) {
                wsConn.sendMessage(session, new ErrorMessage("Unauthorized"));
            }
            wsConn.sendMessage(session, new LoadGameMessage(gameDAO.getGame(command.getGameID())));
            String team = Objects.equals(gameDAO.getGame(command.getGameID()).whiteUsername(), username) ? "White"
                    : Objects.equals(gameDAO.getGame(command.getGameID()).blackUsername(), username) ? "Black" : " an Observer";
            wsConn.broadcastMessage(session, new NotificationMessage(username + " has joined the game as " + team)
                    , command.getGameID());
        } catch (Exception e) {
            wsConn.sendMessage(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        GameData oldData = gameDAO.getGame(command.getGameID());
        ChessMove move = command.move;
        chess.ChessGame.TeamColor team = Objects.equals(gameDAO.getGame(command.getGameID()).whiteUsername(), username) ?
                ChessGame.TeamColor.WHITE : Objects.equals(gameDAO.getGame(command.getGameID()).blackUsername(), username) ?
                ChessGame.TeamColor.BLACK : null;
        if (oldData.game().validMoves(move.getStartPosition()).contains(move)){
            if (oldData.game().getBoard().getPiece(move.getStartPosition()).getTeamColor() != team) {
                throw new Exception("You cannot move for your opponent");
            }
            if (!oldData.game().getGameEnd().isEmpty()) {
                throw new Exception("Cannot Move Game has ended");
            }
            if (team == null) {
                throw new Exception("Observers cannot make moves");
            }
            oldData.game().makeMove(move);

            wsConn.broadcastMessage(null, new LoadGameMessage(gameDAO.getGame(command.getGameID()))
                    , command.getGameID());

            wsConn.broadcastMessage(session
                    , new NotificationMessage(username + "has made the move " + move)
                    , command.getGameID());

            if (!Objects.equals(oldData.game().getGameEnd(), "")) {
                wsConn.broadcastMessage(null, new NotificationMessage(oldData.game().getGameEnd())
                        , command.getGameID());
            }
            gameDAO.updateGame(oldData);

        } else {
            throw new Exception ("Invalid move sent to server " + move);
        }
    }

    private void leave(Session session, String username, UserGameCommand command) throws Exception {
        GameData oldData = gameDAO.getGame(command.getGameID());
        GameData data = new GameData(oldData.gameID()
                , Objects.equals(oldData.whiteUsername(), username) ? null : oldData.whiteUsername()
                , Objects.equals(oldData.blackUsername(), username) ? null : oldData.blackUsername()
                , oldData.gameName(), oldData.game());

        gameDAO.updateGame(data);
        wsConn.broadcastMessage(session, new NotificationMessage("User " + username + " has left the game."), data.gameID());
        wsConn.removeSessionFromGame(data.gameID(), session);
    }

    private void resign(Session session, String username, UserGameCommand command) throws Exception {
        GameData oldData = gameDAO.getGame(command.getGameID());

        if (!oldData.game().getGameEnd().isEmpty()) {
            throw new Exception("The game has already ended, you may not resign");
        }

        ChessGame.TeamColor team = Objects.equals(oldData.whiteUsername(), username) ? ChessGame.TeamColor.WHITE
                : Objects.equals(oldData.blackUsername(), username) ? ChessGame.TeamColor.BLACK : null;
        if (team == null) {
            throw new Exception("Observers are not able to resign");
        }

        ChessGame.TeamColor winTeam = Objects.equals(oldData.whiteUsername(), username) ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;

        oldData.game().setGameEnd(team.name() + " has decided to resign, " + winTeam + " has won by default");

        GameData data = new GameData(oldData.gameID(), oldData.whiteUsername(), oldData.blackUsername()
                , oldData.gameName(), oldData.game());

        gameDAO.updateGame(data);
        wsConn.broadcastMessage(null, new NotificationMessage("User " + username + " has decided to resign.")
                , data.gameID());
    }

    private void saveSession(Integer gameId, Session session) {
        wsConn.addSessionToGame(gameId, session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData data = authDAO.getAuth(authToken);
        return data.username();
    }
}
