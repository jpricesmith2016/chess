package service;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.CreateRequest;
import requestresult.CreateResult;
import requestresult.GameJoinRequest;
import requestresult.GameJoinResult;
import requestresult.ListGamesResult;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    static GameService service;

    @BeforeEach
    public void makeServiceWithAuth() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        try {
            authDAO.createAuth(new AuthData("auth-token", "alice"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        service = new GameService(new MemoryGameDAO(), authDAO);
    }

    @Test
    void listGamesReturnsExistingGames() {
        service.createGame("auth-token", new CreateRequest("Game"));

        ListGamesResult result = service.listGames("auth-token");

        assertEquals(200, result.resultCode());
        assertEquals(1, result.games().size());
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game")));
    }

    @Test
    void listGamesReturnsEmptyNoGamesExist() {

        ListGamesResult result = service.listGames("auth-token");

        assertEquals(200, result.resultCode());
        assertTrue(result.games().isEmpty());
    }

    @Test
    void createGameSucceedsValidName() throws DataAccessException {

        CreateResult result = service.createGame("auth-token", new CreateRequest("Valid Game"));

        assertEquals(200, result.resultCode());
        assertTrue(result.gameID() > 0);
        assertNotNull(service.gameDAO.getGame(result.gameID()));
        assertEquals("Valid Game", service.gameDAO.getGame(result.gameID()).gameName());
    }

    @Test
    void createGameFailsBlankGameName() {

        CreateResult result = service.createGame("auth-token", new CreateRequest(""));

        assertEquals(400, result.resultCode());
        assertEquals(0, service.gameDAO.length());
    }

    @Test
    void joinGameSucceedsAvailableSpot() {
        CreateResult created = service.createGame("auth-token", new CreateRequest("Join"));

        GameJoinResult result = service.joinGame("auth-token", new GameJoinRequest("WHITE", created.gameID()));

        assertEquals(200, result.resultCode());
        assertEquals("alice", service.gameDAO.getGame(created.gameID()).whiteUsername());
    }

    @Test
    void joinGameFailsAlreadyTaken() {
        try {
            service.gameDAO.createGame(new GameData(1, "existing-user", null, "Taken", new ChessGame()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        GameJoinResult result = service.joinGame("auth-token", new GameJoinRequest("WHITE", 1));

        assertEquals(403, result.resultCode());
        assertEquals("Error: already taken", result.message());
        assertEquals("existing-user", service.gameDAO.getGame(1).whiteUsername());
    }

    @Test
    void clearAllGames() {
        service.createGame("auth-token", new CreateRequest("Clear me"));

        service.clear();

        assertEquals(0, service.gameDAO.length());
    }
}