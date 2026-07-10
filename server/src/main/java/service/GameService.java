package service;

import Request_Result.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    final GameDAO gameDAO;
    final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult ListGames(String authToken) {
        Collection<GameData> games = new ArrayList<>();
        return new ListGamesResult(200,games);
    }

    public CreateResult createGame(String authToken, CreateRequest request) {
        if (request.gameName() == null || request.gameName().isEmpty()) {
            return new CreateResult(400, "Error: bad request");
        }
        GameData game = new GameData(gameDAO.length(), null, null, request.gameName(), new ChessGame());

        return new CreateResult(200, Integer.toString(game.gameID()));
    }

    public GameJoinResult joinGame(String authToken, GameJoinRequest request) {
        if (request.playerColor() == null || gameDAO.getGame(request.gameID()) == null) {
            return new GameJoinResult(400, "Error: bad request");
        }
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            return new GameJoinResult(401, "Error: unauthorized");
        }
        GameData game = gameDAO.getGame(request.gameID());
        boolean whiteBool = request.playerColor().equalsIgnoreCase("white") ||
                request.playerColor().equalsIgnoreCase("w");
        if ((whiteBool && game.whiteUsername() != null) ||
                (!whiteBool && game.blackUsername() != null)) {
            return new GameJoinResult(403, "Error: already taken");
        }
        GameData gameNew = new GameData(game.gameID(), whiteBool ? auth.username() : game.whiteUsername(),
                whiteBool ? game.blackUsername() : auth.username(), game.gameName(), game.game());
        gameDAO.updateGame(gameNew);
        return new GameJoinResult(200, Integer.toString(game.gameID()));
    }

    public void clear() {
        gameDAO.clearGame();
    }
}
