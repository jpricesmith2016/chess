package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    private SQLAuthDAO auth;

    @BeforeEach
    void setUp() throws Exception {
        auth = new SQLAuthDAO();
        auth.clearAuth();
    }

    @Test
    void createStoresData() throws DataAccessException {
        var authData = new AuthData("token", "alice");
        auth.createAuth(authData);
        assertEquals(authData, auth.getAuth("token"));
    }

    @Test
    void createRejectsDuplicateToken() throws DataAccessException {
        var authData = new AuthData("token", "alice");
        auth.createAuth(authData);
        assertThrows(DataAccessException.class, () -> auth.createAuth(authData));
    }

    @Test
    void getReturnsStoredData() throws DataAccessException {
        var authData = new AuthData("token", "alice");
        auth.createAuth(authData);
        assertEquals(authData, auth.getAuth("token"));
    }

    @Test
    void getReturnsNullForMissingToken() throws DataAccessException {
        assertNull(auth.getAuth("missing-token"));
    }

    @Test
    void deleteRemovesToken() throws DataAccessException {
        auth.createAuth(new AuthData("token", "alice"));
        auth.deleteAuth("token");
        assertNull(auth.getAuth("token"));
    }

    @Test
    void deleteDoesNotInsertMissingToken() throws DataAccessException {
        auth.deleteAuth("missing-token");
        assertFalse(auth.containsAuthToken("missing-token"));
    }

    @Test
    void clearRemovesAllTokens() throws DataAccessException, SQLException {
        auth.createAuth(new AuthData("token", "alice"));
        auth.clearAuth();
        assertFalse(auth.containsAuthToken("token"));
    }

    @Test
    void containsFindsExistingToken() throws DataAccessException {
        auth.createAuth(new AuthData("token", "alice"));
        assertTrue(auth.containsAuthToken("token"));
    }

    @Test
    void containsDoesNotFindMissingToken() throws DataAccessException {
        assertFalse(auth.containsAuthToken("missing-token"));
    }
}
