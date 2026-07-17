package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void createAuth(AuthData a) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuth() throws DataAccessException {

    }

    @Override
    public Boolean containsAuthToken(String authToken) throws DataAccessException {
        return null;
    }
}
