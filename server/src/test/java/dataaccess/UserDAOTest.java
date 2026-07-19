package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private SQLUserDAO user;

    @BeforeEach
    void setUp() throws Exception {
        user = new SQLUserDAO();
        user.clearUser();
    }

    @Test
    void createUserStoresUser() throws DataAccessException {
        user.createUser(new UserData("alice", "secret", "alice@example.com"));
        assertTrue(user.containsUser("alice"));
    }

    @Test
    void createUserRejectsDuplicateUsername() throws DataAccessException {
        var userData = new UserData("alice", "secret", "alice@example.com");
        user.createUser(userData);
        assertThrows(DataAccessException.class, () -> user.createUser(userData));
    }

    @Test
    void clearUserRemovesAllUsers() throws DataAccessException {
        user.createUser(new UserData("alice", "secret", "alice@example.com"));
        user.clearUser();
        assertFalse(user.containsUser("alice"));
    }

    @Test
    void containsUserFindsExistingUser() throws DataAccessException {
        user.createUser(new UserData("alice", "secret", "alice@example.com"));
        assertTrue(user.containsUser("alice"));
    }

    @Test
    void containsUserReturnsFalseForMissingUser() throws DataAccessException {
        assertFalse(user.containsUser("missing"));
    }

    @Test
    void containsPassAcceptsCorrectPassword() throws DataAccessException {
        user.createUser(new UserData("alice", "secret", "alice@example.com"));
        assertTrue(user.containsPass("alice", "secret"));
    }

    @Test
    void containsPassRejectsIncorrectPassword() throws DataAccessException {
        user.createUser(new UserData("alice", "secret", "alice@example.com"));
        assertFalse(user.containsPass("alice", "wrong-password"));
    }
}
