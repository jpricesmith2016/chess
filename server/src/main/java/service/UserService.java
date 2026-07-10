package service;

import dataaccess.UserDAO;

public class UserService {

    UserDAO userDAO;

    public UserService (UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void clear() {
        userDAO.clearUser();
    }
}
