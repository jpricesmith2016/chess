package service;

import Request_Result.*;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.security.SecureRandom;
import java.util.Base64;


public class AuthService {
    final AuthDAO authDAO;
    final UserDAO userDAO;

    private static String generateToken() {
        SecureRandom secRand = new SecureRandom();

        byte[] randomBytes = new byte[32];
        secRand.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthResult auth(AuthRequest req) {
        if (req.authToken() == null || authDAO.getAuth(req.authToken()) == null) {
            return new AuthResult(401, "Error: unauthorized");
        }
        return new AuthResult(200, "");
    }

    public void clear() {
        authDAO.clearAuth();
    }

    public RegisterResult register(RegisterRequest regReq) {
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
        if (userDAO.getUser(regReq.username()) != null) {
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
