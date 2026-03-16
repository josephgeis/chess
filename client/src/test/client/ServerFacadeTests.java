package client;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.ServiceManager;


public class ServerFacadeTests {

    private static Server server;

    private static ServiceManager serviceManager;

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    private static final UserData TEST_USER = new UserData("user", "password", "john@example.com");
    private static final AuthData TEST_AUTH = AuthData.createFor("user");
    private static final GameData TEST_GAME = GameData.withName("test_game");

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        serviceManager = new ServiceManager(authDAO, gameDAO, userDAO);

        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
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

}
