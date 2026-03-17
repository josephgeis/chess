package client;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import server.Server;
import service.ServiceManager;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;

    private static ServiceManager serviceManager;

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    private static final UserData TEST_USER = new UserData("user", "password", "john@example.com");
    private static final AuthData TEST_AUTH = AuthData.createFor("user");
    private static final GameData TEST_GAME = GameData.withName("test_game");

    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        serviceManager = new ServiceManager(authDAO, gameDAO, userDAO);

        server = new Server(serviceManager);
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("localhost", port);
    }

    @BeforeEach
    public void setUp() {
        try {
            serviceManager.clearDatabases();
            userDAO.createUser(TEST_USER);
            authDAO.createAuth(TEST_AUTH);
            gameDAO.createGame(TEST_GAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerUser() {
        RegisterRequest request = new RegisterRequest("new_user", "new_password", "new@example.com");
        RegisterResponse response = assertDoesNotThrow(() -> serverFacade.registerUser(request));

        UserData userData = assertDoesNotThrow(() -> userDAO.getUser("new_user"));

        assertEquals("new_user", response.username());
        assertDoesNotThrow(() -> authDAO.retrieveAuth(response.authToken()));
        assertEquals("new_user", userData.username());
        assertEquals("new@example.com", userData.email());
    }

    @Test
    void registerUserAlreadyExists() {
        RegisterRequest request = new RegisterRequest(TEST_USER.username(), "new_password", "new@example.com");
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class, () -> serverFacade.registerUser(request));
        assertEquals("Error: Already Taken", error.getMessage());
    }

    @Test
    void loginUser() {
        LoginRequest request = new LoginRequest(TEST_USER.username(), TEST_USER.password());
        LoginResponse response = assertDoesNotThrow(() -> serverFacade.loginUser(request));

        AuthData authData = assertDoesNotThrow(() -> authDAO.retrieveAuth(response.authToken()));
        assertEquals(authData.authToken(), response.authToken());
        assertEquals(authData.username(), response.username());
    }

    @Test
    void loginUserDoesntExist() {
        LoginRequest request = new LoginRequest("fake_user", "fake_password");
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class, () -> serverFacade.loginUser(request));
        assertEquals("Error: unauthorized", error.getMessage());
    }

    @Test
    void logoutUser() {
        assertDoesNotThrow(() -> serverFacade.logoutUser(TEST_AUTH.authToken()));
    }

    @Test
    void logoutUserDoesntExist() {
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class, () -> serverFacade.logoutUser("fake_token"));
        assertEquals("Error: unauthorized", error.getMessage());
    }

    @Test
    void listGames() {
        fail("Not implemented.");
    }

    @Test
    void createGame() {
        fail("Not implemented.");
    }

    @Test
    void joinGame() {
        fail("Not implemented.");
    }

    @Test
    void clearDb() {
        fail("Not implemented.");
    }
}
