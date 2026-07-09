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
        return userInfo.get(username);
    }

    @Override
    public void updateUser(UserData user) {
        userInfo.putIfAbsent(user.username(), user);
        userInfo.replace(user.username(), user);
    }

    @Override
    public void deleteUser(String username) {
        userInfo.remove(username);
    }

    @Override
    public void clearUser() {
        userInfo.clear();
    }
}
