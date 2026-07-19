package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.*;
import model.AuthData;

import java.sql.*;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws Exception {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `authData` longtext NOT NULL,
              PRIMARY KEY (`authToken`)
            );
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, authData) VALUES (?, ?)";
        String authToken = a.authToken();
        String jsonAuthData = new Gson().toJson(a);
        if (getAuth(a.authToken()) != null) {
            throw new AlreadyTakenException();
        }
        DatabaseManager.executeUpdate(statement, authToken, jsonAuthData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, authData FROM auth WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException(String.format("unable to get Auth: %s, %s", statement, e.getMessage()), e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        DatabaseManager.executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public Boolean containsAuthToken(String authToken) throws DataAccessException {
        return getAuth(authToken) != null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("authData");
        return new Gson().fromJson(json, AuthData.class);
    }

}
