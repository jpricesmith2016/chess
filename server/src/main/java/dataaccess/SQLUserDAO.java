package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class SQLUserDAO implements UserDAO{

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `passwordEnc` varchar(256) DEFAULT NULL,
              `email` varchar(256) DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX (`passwordEnc`)
            );
            """
    };

    public SQLUserDAO() throws Exception {
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO user (username, passwordEnc, email) VALUES (?, ?, ?)";
        String username = u.username();
        String passwordEnc = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        String email = u.email();
        DatabaseManager.executeUpdate(statement, username, passwordEnc, email);
    }

    @Override
    public void clearUser() throws DataAccessException {
        var statement = "TRUNCATE user";
        DatabaseManager.executeUpdate(statement);
        try {
            DatabaseManager.configureDatabase(createStatements);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Override
    public boolean containsUser(String username) throws DataAccessException {
        return hashedPass(username) != null;
    }

    @Override
    public boolean containsPass(String username, String password) throws DataAccessException {
        return BCrypt.checkpw(password, hashedPass(username));
    }

    private String hashedPass(String username) throws DataAccessException {
        var statement = "SELECT username, passwordEnc FROM user WHERE username=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("passwordEnc");
                    }
                }
            }
        }catch (Exception e) {
            throw new DataAccessException(String.format("unable to get Password: %s, %s", statement, e.getMessage()), e);
        }
        return null;
    }

}
