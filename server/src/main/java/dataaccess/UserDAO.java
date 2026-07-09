package dataaccess;

import model.UserData;

public interface UserDAO {
    public void createUser (UserData u);

    public UserData getUser (String username);

    public void updateUser (UserData user);

    public void deleteUser (String username);

    public void clearUser ();
}
