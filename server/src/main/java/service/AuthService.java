package service;

import dataaccess.exceptions.DataAccessException;
import requestresult.*;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class AuthService {
    final AuthDAO authDAO;
    final UserDAO userDAO;

    static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResult login (LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {

            return new LoginResult(400, "Error: bad request", null);

        } else {
            if (!userDAO.containsUser(request.username()) || !userDAO.containsPass(request.username(), request.password())) {

                return new LoginResult(401, "Error: unauthorized", null);

            } else {

                AuthData auth = new AuthData(generateToken(), request.username());
                authDAO.createAuth(auth);
                return new LoginResult(200, "", auth.authToken());

            }
        }

    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        if ((request.authToken() == null) || (!authDAO.containsAuthToken(request.authToken()))) {

            return new LogoutResult(401, "Error: unauthorized");

        } else {

            authDAO.deleteAuth(request.authToken());
            return new LogoutResult (200, "");

        }
    }

    public AuthResult auth(AuthRequest req) throws DataAccessException {
        if (req.authToken() == null || authDAO.getAuth(req.authToken()) == null) {
            return new AuthResult(401, "Error: unauthorized");
        }
        return new AuthResult(200, "");
    }

    public void clear() throws DataAccessException {
        authDAO.clearAuth();
    }

    public RegisterResult register(RegisterRequest regReq) throws DataAccessException {
        String authToken;
        // Verify input
        // Check request username is not taken (return null)
        // Create new User model obj: User u = new User(...)
        // Insert new User into user database by calling UserDAU.createUser(u)
        // Login the user (Create new AuthToken model object and insert into database) could call login function
        // Return result based on spec by making a Register Result obj and returning it
        if (regReq.username() == null || regReq.password() == null) {
            return new RegisterResult(400, new RegAuthReturn(regReq.username(), ""), "Error: bad request");
        }
        if (userDAO.containsUser(regReq.username())) {
            return new RegisterResult(403, new RegAuthReturn(regReq.username(), ""), "Error: already taken");
        }
        do {
            authToken = generateToken();
        } while (authDAO.getAuth(authToken) != null);

        authDAO.createAuth(new AuthData(authToken, regReq.username()));

        userDAO.createUser(new UserData(regReq.username(), regReq.password(), regReq.email()));

        RegAuthReturn returnAuth = new RegAuthReturn(regReq.username(), authToken);

        return new RegisterResult(200, returnAuth, "");
    }


}
