package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    int createGame(GameData g) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> getGameListUser(String username) throws DataAccessException;

    Collection<GameData> getGameList() throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clearGame() throws DataAccessException;
}
