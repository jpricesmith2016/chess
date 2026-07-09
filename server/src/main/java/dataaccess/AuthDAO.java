package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public void createAuth (AuthData a);

    public AuthData getAuth (String authToken);

    public AuthData getAuthUser (String username);

    public void updateAuth (AuthData auth);

    public void deleteAuth (String authToken);

    public void deleteAuth (AuthData auth);

    public void clearAuth ();
}
