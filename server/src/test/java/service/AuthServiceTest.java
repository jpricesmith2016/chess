package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requestresult.AuthRequest;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import requestresult.LogoutRequest;
import requestresult.LogoutResult;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;


class AuthServiceTest {

    static AuthService service;

    @BeforeAll
    public static void makeServiceWithNewUser() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        try {
            userDAO.createUser(new UserData("alice", "correct-pass", "test@gmail.com"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
        service = new AuthService(authDAO, userDAO);
    }

    @Test
    void loginSucceedsValidCred() {

        LoginResult result = service.login(new LoginRequest("alice", "correct-pass"));

        assertEquals(200, result.resultCode());
        assertEquals("", result.message());
        assertNotNull(result.authToken());
        try {
            assertNotNull(service.authDAO.getAuth(result.authToken()));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loginFailsBadPass() {

        LoginResult result = service.login(new LoginRequest("alice", "wrong-pass"));

        assertEquals(401, result.resultCode());
        assertNull(result.authToken());
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    void logoutSucceedsKnownToken() {
        String token = AuthService.generateToken();
        try {
            service.authDAO.createAuth(new AuthData(token, "alice"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        LogoutResult result = service.logout(new LogoutRequest(token));

        assertEquals(200, result.resultCode());
        try {
            assertFalse(service.authDAO.containsAuthToken(token));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void logoutFailsUnknownToken() {
        String token = AuthService.generateToken();
        try {
            service.authDAO.createAuth(new AuthData(token, "alice"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        LogoutResult result = service.logout(new LogoutRequest("bad-token"));

        assertEquals(401, result.resultCode());
        try {
            assertTrue(service.authDAO.containsAuthToken(token));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void authSucceedsKnownToken() {
        String token = AuthService.generateToken();
        try {
            service.authDAO.createAuth(new AuthData(token, "alice"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        var result = service.auth(new AuthRequest(token));

        assertEquals(200, result.resultCode());
    }

    @Test
    void authFailsUnknownToken() {

        var result = service.auth(new AuthRequest("missing-token"));

        assertEquals(401, result.resultCode());
    }

    @Test
    void clearAuthEntries() {
        String token = AuthService.generateToken();
        try {
            service.authDAO.createAuth(new AuthData(token, "alice"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }

        service.clear();

        try {
            assertFalse(service.authDAO.containsAuthToken(token));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerSucceedsNewUser() {

        RegisterResult result = service.register(new RegisterRequest("joe", "secret", "joe@gmail.com"));

        assertEquals(200, result.resultCode());
        assertNotNull(result.returnAuth());
        assertEquals("joe", result.returnAuth().username());
        try {
            assertTrue(service.userDAO.containsUser("joe"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(service.userDAO.containsPass("joe", "secret"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerFailsDuplicateUser() {

        RegisterResult result = service.register(new RegisterRequest("alice", "other", "test@gmail.com"));

        assertEquals(403, result.resultCode());
        assertEquals("Error: already taken", result.message());
        try {
            assertTrue(service.userDAO.containsPass("alice", "correct-pass"));
        } catch (dataaccess.exceptions.DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}