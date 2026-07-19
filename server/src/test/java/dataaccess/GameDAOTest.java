package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    private SQLGameDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        dao = new SQLGameDAO();
        dao.clearGame();
    }

    @Test
    void createStoresInitialBoard() throws DataAccessException {
        var board = new ChessGame();
        int id = dao.createGame(new GameData(0, null, null, "opening", board));
        GameData stored = dao.getGame(id);

        assertTrue(id > 0);
        assertEquals("opening", stored.gameName());
        assertEquals(board, stored.game());
    }

    @Test
    void createRejectsInvalidGameData() {
        assertThrows(RuntimeException.class, () -> dao.createGame(null));
    }

    @Test
    void getReturnsStoredGame() throws DataAccessException {
        int id = createGame("retrievable");
        assertEquals("retrievable", dao.getGame(id).gameName());
    }

    @Test
    void getReturnsNullForMissingGame() throws DataAccessException {
        assertNull(dao.getGame(200));
    }

    @Test
    void getGameListUserReturnsPlayerGames() throws DataAccessException {
        int id = createGame("alice-game");
        GameData game = dao.getGame(id);
        dao.updateGame(new GameData(id, "alice", null, game.gameName(), game.game()));

        assertEquals(1, dao.getGameListUser("alice").size());
    }

    @Test
    void getGameListUserExcludesUnrelatedPlayer() throws DataAccessException {
        int id = createGame("alice-game");
        GameData game = dao.getGame(id);
        dao.updateGame(new GameData(id, "alice", null, game.gameName(), game.game()));

        assertTrue(dao.getGameListUser("bob").isEmpty());
    }

    @Test
    void getGameListReturnsAllGames() throws DataAccessException {
        createGame("first");
        createGame("second");
        assertEquals(2, dao.getGameList().size());
    }

    @Test
    void getGameListReturnsEmptyWhenNoGamesExist() throws DataAccessException {
        assertTrue(dao.getGameList().isEmpty());
    }

    @Test
    void updateGamePersistsPlayersAndMovedBoard() throws Exception {
        int id = createGame("updated");
        ChessGame movedGame = dao.getGame(id).game();
        movedGame.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        dao.updateGame(new GameData(id, "alice", "bob", "updated", movedGame));

        GameData stored = dao.getGame(id);
        assertEquals("alice", stored.whiteUsername());
        assertEquals("bob", stored.blackUsername());
        assertEquals(movedGame, stored.game());
    }

    @Test
    void updateGameDoesNotCreateMissingGame() throws DataAccessException {
        dao.updateGame(new GameData(200, "alice", "bob", "missing", new ChessGame()));
        assertNull(dao.getGame(200));
    }

    @Test
    void clearGameRemovesAllGames() throws DataAccessException {
        createGame("clear-me");
        dao.clearGame();
        assertTrue(dao.getGameList().isEmpty());
    }

    private int createGame(String name) throws DataAccessException {
        return dao.createGame(new GameData(0, null, null, name, new ChessGame()));
    }
}
