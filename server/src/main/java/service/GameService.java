package service;

import requestresult.*;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    final GameDAO gameDAO;
    final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames (String authToken) {
        Collection<GameData> games = gameDAO.getGameList();
        return new ListGamesResult(200,"", games);
    }

    public CreateResult createGame(String authToken, CreateRequest request) {
        if (request.gameName() == null || request.gameName().isEmpty()) {
            return new CreateResult(400,0, "Error: bad request");
        }
        GameData game = new GameData(gameDAO.length() + 1, null, null, request.gameName(), new ChessGame());

        gameDAO.createGame(game);

        return new CreateResult(200, game.gameID(), "");
    }

    public GameJoinResult joinGame(String authToken, GameJoinRequest request) {
        boolean whiteBool = request.playerColor() != null && request.playerColor().equalsIgnoreCase("WHITE");
        boolean blackBool = request.playerColor() != null && request.playerColor().equalsIgnoreCase("BLACK");
        if (gameDAO.getGame(request.gameID()) == null || (!whiteBool && !blackBool)) {
            return new GameJoinResult(400, "Error: bad request");
        }
        if (!authDAO.containsAuthToken(authToken)) {
            return new GameJoinResult(401, "Error: unauthorized");
        }
        AuthData auth = authDAO.getAuth(authToken);
        GameData game = gameDAO.getGame(request.gameID());
        if ((whiteBool && game.whiteUsername() != null) ||
                (blackBool && game.blackUsername() != null)) {
            return new GameJoinResult(403, "Error: already taken");
        }
        GameData gameNew = new GameData(game.gameID(), whiteBool ? auth.username() : game.whiteUsername(),
                whiteBool ? game.blackUsername() : auth.username(), game.gameName(), game.game());
        gameDAO.updateGame(gameNew);
        return new GameJoinResult(200, "");
    }

    public void clear() {
        gameDAO.clearGame();
    }
}
