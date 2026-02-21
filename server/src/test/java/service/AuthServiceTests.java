package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import result.LoginResult;
import server.UnauthorizedRequestException;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceTests {
    static AuthDAO authDAO;
    static UserDAO userDAO;

    static AuthService authService;

    static String USERNAME = "username";
    static String PASSWORD = "password";
    static String EMAIL = "nobody@example.com";
    static String AUTH_TOKEN = "random_auth_token";

    static final String USERNAME_NON_EXISTENT = "nonexistent_user";
    static final String AUTH_TOKEN_NON_EXISTENT = "non_existent_auth_token";

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
    @Order(1)
    void loginUser() {
        LoginRequest request = new LoginRequest(USERNAME, PASSWORD);
        LoginResult result = assertDoesNotThrow(() -> authService.loginUser(request));

        assertEquals(USERNAME, result.username());
        assertDoesNotThrow(() -> authDAO.retrieveAuth(result.authToken()));
    }

    @Test
    @Order(2)
    void loginNonExistentUser() {
        LoginRequest request = new LoginRequest(USERNAME_NON_EXISTENT, PASSWORD);
        assertThrows(UnauthorizedRequestException.class, () -> authService.loginUser(request));
    }

    @Test
    @Order(3)
    void logoutUser() {
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.createAuth(authData));

        assertDoesNotThrow(() -> authService.logoutUser(AUTH_TOKEN));
    }

    @Test
    @Order(4)
    void logoutNotLoggedInUser() {
        assertThrows(UnauthorizedRequestException.class, () -> authService.logoutUser(AUTH_TOKEN_NON_EXISTENT));
    }

    @Test
    @Order(5)
    void logoutTwice() {
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.createAuth(authData));

        assertDoesNotThrow(() -> authService.logoutUser(AUTH_TOKEN));
        assertThrows(UnauthorizedRequestException.class, () -> authService.logoutUser(AUTH_TOKEN));
    }

    @Test
    @Order(6)
    void loginLogout() {
        LoginRequest request = new LoginRequest(USERNAME, PASSWORD);
        LoginResult result = assertDoesNotThrow(() -> authService.loginUser(request));

        assertEquals(USERNAME, result.username());
        assertDoesNotThrow(() -> authDAO.retrieveAuth(result.authToken()));

        assertDoesNotThrow(() -> authService.logoutUser(result.authToken()));
    }
}