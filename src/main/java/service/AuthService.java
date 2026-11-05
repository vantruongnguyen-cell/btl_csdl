package service;

import dao.AdminDAO;

public class AuthService {
    private AdminDAO adminDAO = new AdminDAO();

    public boolean login(String username, String password) {
        return adminDAO.authenticate(username, password);
    }
}