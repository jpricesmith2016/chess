package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final HashMap<String, AuthData> authInfo = new HashMap<>();


    @Override
    public void createAuth(AuthData a) {
        authInfo.put(a.authToken(), a);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authInfo.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authInfo.remove(authToken);
    }

    @Override
    public void clearAuth() {
        authInfo.clear();
    }

    @Override
    public Boolean containsAuthToken(String authToken) {
        return authInfo.containsKey(authToken);
    }

//    @Override
//    public Boolean containsUser(String username) {
//        Optional<AuthData> matchingUser = authInfo.values().stream()
//                .filter(auth -> username.equals(auth.username()))
//                .findFirst();
//        return matchingUser.isPresent();
//    }
}
