package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;
import server.UnauthorizedRequestException;

public class AuthService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public LoginResult loginUser(LoginRequest request) throws Exception {
        UserData user;
        try {
            user = userDAO.getUser(request.username());
        } catch (UserDAO.UsernameAlreadyExistsException ignored) {
            throw new UnauthorizedRequestException();
        }

        if (user == null || !user.validatePassword(request.password())) {
            throw new UnauthorizedRequestException();
        }

        AuthData authData = AuthData.createFor(user.username());
        authDAO.createAuth(authData);

        return LoginResult.from(authData);
    }

    public void logoutUser(String authToken) throws Exception {
        try {
            authDAO.destroyAuth(authToken);
        } catch (AuthDAO.AuthDoesNotExistException ignored) {
            throw new UnauthorizedRequestException();
        }
    }
}
