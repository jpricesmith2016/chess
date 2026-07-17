package service;

import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;

public class UserService {

    final UserDAO userDAO;

    public UserService (UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clearUser();
    }
}
