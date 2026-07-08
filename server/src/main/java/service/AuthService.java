package service;

public class AuthService {

    record RegisterRequest (String username, String password, String email) {}

    record RegisterResult (String username, String authToken, String message) {}

    RegisterResult register (RegisterRequest regReq) {
        // Verify input
        // Check request username is not taken (return null)
        // Create new User model obj: User u = new User(...)
        // Insert new User into user database by calling UserDAU.createUser(u)
        // Login the user (Create new AuthToken model object and insert into database) could call login function
        // Return result based on spec by making a Register Result obj and returning it
    }


}
