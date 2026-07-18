package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.List;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{
    @Override
    public void createGame(GameData g) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> getGameListUser(String username) throws DataAccessException {
        return List.of();
    }

    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clearGame() throws DataAccessException {

    }

    @Override
    public int length() throws DataAccessException {
        return 0;
    }
}
