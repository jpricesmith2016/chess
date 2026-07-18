package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    @Override
    public void createUser(UserData u) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUser() throws DataAccessException {

    }

    @Override
    public boolean containsUser(String username) throws DataAccessException {
        return false;
    }

    @Override
    public boolean containsPass(String username, String password) throws DataAccessException {
        return false;
    }
}
