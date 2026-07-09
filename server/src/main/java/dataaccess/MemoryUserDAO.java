package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> userInfo = new HashMap<>();

    @Override
    public void createUser(UserData u) {
        userInfo.putIfAbsent(u.username(), u);
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void updateUser(UserData user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void clearUser() {

    }
}
