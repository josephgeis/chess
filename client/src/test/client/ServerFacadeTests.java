package client;

import dataaccess.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.ServiceManager;


public class ServerFacadeTests {

    private static Server server;

    private static ServiceManager serviceManager;

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
