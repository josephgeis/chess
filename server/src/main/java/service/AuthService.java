package service;

import dataaccess.AuthDAO;

public class AuthService {

    private AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
    }


}
