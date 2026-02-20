package passoff.service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;
import server.AlreadyTakenException;
import service.ServiceManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTests {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    ServiceManager serviceManager;

    @BeforeAll
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        serviceManager = new ServiceManager(authDAO, gameDAO, userDAO);
    }

    @Test
    @Order(1)
    void testRegisterUser() {
        final String USERNAME = "username";
        RegisterRequest request = new RegisterRequest(USERNAME, "password", "nobody@example.com");

        RegisterResult result = Assertions.assertDoesNotThrow(() -> serviceManager.getUserService().registerUser(request));
        Assertions.assertEquals(USERNAME, result.username());

        UserData user = Assertions.assertDoesNotThrow(() -> userDAO.getUser(USERNAME));
        Assertions.assertEquals(USERNAME, user.username());

        AuthData auth = Assertions.assertDoesNotThrow(() -> authDAO.retrieveAuth(result.authToken()));
        Assertions.assertEquals(USERNAME, auth.username());
    }

    @Test
    @Order(2)
    void testDoubleRegister() {
        final String USERNAME = "username";
        RegisterRequest request = new RegisterRequest(USERNAME, "password", "nobody@example.com");
        Assertions.assertThrows(AlreadyTakenException.class, () -> serviceManager.getUserService().registerUser(request));
    }
}
