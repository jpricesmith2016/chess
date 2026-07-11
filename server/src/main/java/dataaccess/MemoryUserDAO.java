package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final HashMap<String, UserData> userInfo = new HashMap<>();

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
    public boolean containsUser (String username) {
        return userInfo.containsKey(username);
    }

    @Override
    public boolean containsPass (String username, String password) {
        UserData temp = userInfo.get(username);
        return (temp.password().equals(password));
    }

    @Override
    public void clearUser() {
        userInfo.clear();
    }
}
