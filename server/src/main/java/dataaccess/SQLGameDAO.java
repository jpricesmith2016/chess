package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{
    @Override
    public int createGame(GameData g) throws DataAccessException {
        var statement = "INSERT INTO game (gameName, gameState) VALUES (?, ?)";
        String gameName = g.gameName();
        String game = new Gson().toJson(g.game());
        return executeUpdate(statement, gameName, game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT id, userBlack, userWhite, gameName, gameState FROM game WHERE id=?";

        return null;
    }

    @Override
    public Collection<GameData> getGameListUser(String username) throws DataAccessException {
        return List.of();
    }

    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        var statement = "SELECT id, userBlack, userWhite, gameName, gameState FROM game";
        return gameListPuller(statement);
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {

    }

    @Override
    public void clearGame() throws DataAccessException {

    }

    private Collection<GameData> gameListPuller (String statement,  Object... params) throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                try (ResultSet rs = ps.executeQuery()) {
                    for (int i = 0; i < params.length; i++) {
                        Object param = params[i];
                        switch (param) {
                            case Integer p -> ps.setInt(i + 1, p);
                            case String p -> ps.setString(i + 1, p);
                            case null -> ps.setNull(i + 1, NULL);
                            default -> {
                            }
                        }
                    }
                    while (rs.next()) {
                        result.add(new GameData(rs.getInt(1), rs.getString(2)
                                , rs.getString(3), rs.getString(4)
                                , new Gson().fromJson(rs.getString(5), ChessGame.class)));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), e);
        }
        return result;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case Integer p -> ps.setInt(i + 1, p);
                        case String p -> ps.setString(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), e);
        }
        return 0;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              'id' INT NOT NULL AUTO_INCREMENT,
              'userBlack' VARCHAR(256) DEFAULT NULL,
              'userWhite' VARCHAR(256) DEFAULT NULL,
              'gameName' VARCHAR(256) NOT NULL,
              'gameState' longtext NOT NULL,
              PRIMARY KEY ('id'),
              FOREIGN KEY ('userBlack'),
              FOREIGN KEY ('userWhite')
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
