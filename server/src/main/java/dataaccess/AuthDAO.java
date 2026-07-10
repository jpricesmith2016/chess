package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData a);

    AuthData getAuth(String authToken);

    void updateAuth(AuthData auth);

    void deleteAuth(String authToken);

    void deleteAuth(AuthData auth);

    void clearAuth();
}
