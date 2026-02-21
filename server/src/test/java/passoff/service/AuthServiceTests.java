package passoff.service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import result.LoginResult;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;


class AuthServiceTests {
    static AuthDAO authDAO;
    static UserDAO userDAO;

    static AuthService authService;

    static String USERNAME = "username";
    static String PASSWORD = "password";
    static String EMAIL = "nobody@example.com";

    @BeforeAll
    static void setUp() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        authService = new AuthService(authDAO, userDAO);

        try {
            userDAO.createUser(new UserData(USERNAME, PASSWORD, EMAIL));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loginUser() {
        LoginRequest request = new LoginRequest(USERNAME, PASSWORD);
        LoginResult result = assertDoesNotThrow(() -> authService.loginUser(request));

        assertEquals(USERNAME, result.username());
        assertDoesNotThrow(() -> authDAO.retrieveAuth(result.authToken()));
    }
}