package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import response.RegisterResponse;
import server.AlreadyTakenException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    static AuthDAO authDAO;
    static UserDAO userDAO;

    static UserService userService;

    static final String USERNAME = "username";

    @BeforeAll
    static void setUp() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        userService = new UserService(userDAO, authDAO);
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest(USERNAME, "password", "nobody@example.com");

        RegisterResponse result = Assertions.assertDoesNotThrow(() -> userService.registerUser(request));
        Assertions.assertEquals(USERNAME, result.username());

        UserData user = Assertions.assertDoesNotThrow(() -> userDAO.getUser(USERNAME));
        Assertions.assertEquals(USERNAME, user.username());

        AuthData auth = Assertions.assertDoesNotThrow(() -> authDAO.retrieveAuth(result.authToken()));
        Assertions.assertEquals(USERNAME, auth.username());
    }

    @Test
    @Order(2)
    void testDoubleRegister() {
        RegisterRequest request = new RegisterRequest(USERNAME, "password", "nobody@example.com");
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.registerUser(request));
    }
}
