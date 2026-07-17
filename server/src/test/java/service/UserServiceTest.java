package service;

import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void clearAllUsers() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        try {
            userDAO.createUser(new UserData("alice", "password", "alice@gmail.com"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
        UserService service = new UserService(userDAO);

        service.clear();

        try {
            assertNull(userDAO.getUser("alice"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}