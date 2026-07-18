package dataaccess;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    @Override
    public void createUser(UserData u) throws DataAccessException {

    }

    @Override
    public void clearUser() throws DataAccessException {

    }

    @Override
    public boolean containsUser(String username) throws DataAccessException {
        return false;
    }

    @Override
    public boolean containsPass(String username, String password) throws DataAccessException {
        var statement = "SELECT authToken, authData FROM auth WHERE authToken=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var hashedPass = rs.getString("passwordEnc");
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException(String.format("unable to get Auth: %s, %s", statement, e.getMessage()), e);
        }
        return false;
    }


    private void executeUpdate(String statement, Object... params) throws DataAccessException {
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
                    rs.getInt(1);
                }

            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()), e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              'id' INT NOT NULL AUTO_INCREMENT,
              'username' varchar(256) NOT NULL,
              'passwordEnc' varchar(256) NOT NULL,
              'email; varchar(256) NOT NULL,
              PRIMARY KEY ('id'),
              INDEX ('passwordEnc')
              INDEX ('username')
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
