package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData a) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

//    void updateAuth(AuthData auth);

    void deleteAuth(String authToken) throws DataAccessException;

//    void deleteAuth(AuthData auth);

    void clearAuth() throws DataAccessException;

    Boolean containsAuthToken(String authToken) throws DataAccessException;

//    Boolean containsUser(String username);
}
