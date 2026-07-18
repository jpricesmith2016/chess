package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;

//    void updateUser(UserData user);

//    void deleteUser(String username);

    void clearUser() throws DataAccessException;

    boolean containsUser(String username) throws DataAccessException;

    boolean containsPass(String username, String password) throws DataAccessException;
}
