package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;
import result.RegisterResult;
import server.AlreadyTakenException;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult registerUser(@NotNull RegisterRequest request) throws Exception {
        var newUser = new UserData(request.username(), request.password(), request.email());

        try {
            userDAO.createUser(newUser);
        } catch (UserDAO.UsernameAlreadyExistsException ignored) {
            throw new AlreadyTakenException();
        }

        AuthData newAuth = AuthData.createFor(request.username());
        authDAO.createAuth(newAuth);

        return RegisterResult.from(newAuth);
    }
}
