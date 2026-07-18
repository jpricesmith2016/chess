package service;

import dataaccess.MemoryUserDAO;
import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void clearAllUsers() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser(new UserData("alice", "password", "alice@gmail.com"));
        UserService service = new UserService(userDAO);

        service.clear();

        assertFalse(userDAO.containsUser("alice"));
    }
}