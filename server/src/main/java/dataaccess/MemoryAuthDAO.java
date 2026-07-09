package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> authInfo = new HashMap<>();


    @Override
    public void createAuth(AuthData a) {
        authInfo.put(a.authToken(), a);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authInfo.get(authToken);
    }

    @Override
    public void updateAuth(AuthData auth) {
        authInfo.putIfAbsent(auth.authToken(), auth);
        authInfo.replace(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(String authToken) {
        authInfo.remove(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) {
        authInfo.remove(auth.authToken());
    }

    @Override
    public void clearAuth() {
        authInfo.clear();
    }
}
