package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.exceptions.DataAccessException;
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
    public static void makeServiceWithNewUser() throws DataAccessException {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser(new UserData("alice", "correct-pass", "test@gmail.com"));

        service = new AuthService(authDAO, userDAO);
    }

    @Test
    void loginSucceedsValidCred() throws DataAccessException{

        LoginResult result = service.login(new LoginRequest("alice", "correct-pass"));

        assertEquals(200, result.resultCode());
        assertEquals("", result.message());
        assertNotNull(result.authToken());
        assertNotNull(service.authDAO.getAuth(result.authToken()));

    }

    @Test
    void loginFailsBadPass() throws DataAccessException {

        LoginResult result = service.login(new LoginRequest("alice", "wrong-pass"));

        assertEquals(401, result.resultCode());
        assertNull(result.authToken());
        assertEquals("Error: unauthorized", result.message());
    }

    @Test
    void logoutSucceedsKnownToken() throws DataAccessException {
        String token = AuthService.generateToken();
        service.authDAO.createAuth(new AuthData(token, "alice"));

        LogoutResult result = service.logout(new LogoutRequest(token));

        assertEquals(200, result.resultCode());
        assertFalse(service.authDAO.containsAuthToken(token));

    }

    @Test
    void logoutFailsUnknownToken() throws DataAccessException {
        String token = AuthService.generateToken();
        service.authDAO.createAuth(new AuthData(token, "alice"));


        LogoutResult result = service.logout(new LogoutRequest("bad-token"));

        assertEquals(401, result.resultCode());
        assertTrue(service.authDAO.containsAuthToken(token));
    }

    @Test
    void authSucceedsKnownToken() throws DataAccessException {
        String token = AuthService.generateToken();

        service.authDAO.createAuth(new AuthData(token, "alice"));

        var result = service.auth(new AuthRequest(token));

        assertEquals(200, result.resultCode());
    }

    @Test
    void authFailsUnknownToken() throws DataAccessException {

        var result = service.auth(new AuthRequest("missing-token"));

        assertEquals(401, result.resultCode());
    }

    @Test
    void clearAuthEntries() throws DataAccessException {
        String token = AuthService.generateToken();
        service.authDAO.createAuth(new AuthData(token, "alice"));

        service.clear();

        assertFalse(service.authDAO.containsAuthToken(token));

    }

    @Test
    void registerSucceedsNewUser() throws DataAccessException {

        RegisterResult result = service.register(new RegisterRequest("joe", "secret", "joe@gmail.com"));

        assertEquals(200, result.resultCode());
        assertNotNull(result.returnAuth());
        assertEquals("joe", result.returnAuth().username());
        assertTrue(service.userDAO.containsUser("joe"));

        assertTrue(service.userDAO.containsPass("joe", "secret"));

    }

    @Test
    void registerFailsDuplicateUser() throws DataAccessException {

        RegisterResult result = service.register(new RegisterRequest("alice", "other", "test@gmail.com"));

        assertEquals(403, result.resultCode());
        assertEquals("Error: already taken", result.message());
        assertTrue(service.userDAO.containsPass("alice", "correct-pass"));

    }
}