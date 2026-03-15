package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import response.LoginResponse;
import server.UnauthorizedRequestException;

public class AuthService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public LoginResponse loginUser(LoginRequest request) throws Exception {
        UserData user = userDAO.getUser(request.username());

        if (user == null || !userDAO.validatePassword(user, request.password())) {
            throw new UnauthorizedRequestException();
        }

        AuthData authData = AuthData.createFor(user.username());
        authDAO.createAuth(authData);

        return LoginResponse.from(authData);
    }

    public void logoutUser(String authToken) throws Exception {
        try {
            authDAO.destroyAuth(authToken);
        } catch (AuthDAO.AuthDoesNotExistException ignored) {
            throw new UnauthorizedRequestException();
        }
    }
}
