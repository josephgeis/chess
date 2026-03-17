package client;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.*;
import server.Server;
import service.ServiceManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


public class ServerFacadeTests {

    private static Server server;

    private static ServiceManager serviceManager;

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    private static final UserData TEST_USER = new UserData("user", "password", "john@example.com");
    private static final AuthData TEST_AUTH = AuthData.createFor("user");
    private static final GameData TEST_GAME = GameData.withName("test_game");
    private static int testGameID;

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
            testGameID = gameDAO.createGame(TEST_GAME);
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
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class,
                () -> serverFacade.logoutUser("fake_token"));
        assertEquals("Error: unauthorized", error.getMessage());
    }

    @Test
    void listGames() {
        ListGamesResponse response = assertDoesNotThrow(() -> serverFacade.listGames(TEST_AUTH.authToken()));
        assertEquals(1, response.games().size(), "The number of games is not the same");
        GameListing gameListing = response.games().iterator().next();
        assertEquals(TEST_GAME.gameName(), gameListing.gameName(), "Does not contain the test game");
    }

    @Test
    void listGamesUnauthenticated() {
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class,
                () -> serverFacade.listGames("fake_token"));
        assertEquals("Error: unauthorized", error.getMessage());
    }

    @Test
    void createGame() {
        CreateGameRequest request = new CreateGameRequest("new_game");
        CreateGameResponse response = assertDoesNotThrow(() -> serverFacade.createGame(request, TEST_AUTH.authToken()));

        GameData game;
        try {
            game = gameDAO.retrieveGame(response.gameID());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertEquals("new_game", game.gameName());
    }

    @Test
    void createGameUnauthorized() {
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class,
                () -> serverFacade.listGames("fake_token"));
        assertEquals("Error: unauthorized", error.getMessage());
    }

    @Test
    void joinGame() {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, testGameID);
        assertDoesNotThrow(() -> serverFacade.joinGame(request, TEST_AUTH.authToken()));

        GameData gameDataAfter = null;
        try {
            gameDataAfter = gameDAO.retrieveGame(testGameID);
        } catch (DataAccessException e) {
            abort("Couldn't access the game");
        }
        assertEquals(TEST_USER.username(), gameDataAfter.blackUsername());
        assertNull(gameDataAfter.whiteUsername());

        JoinGameRequest requestWhite = new JoinGameRequest(ChessGame.TeamColor.WHITE, testGameID);
        assertDoesNotThrow(() -> serverFacade.joinGame(requestWhite, TEST_AUTH.authToken()));

        GameData gameDataAfterWhite = null;
        try {
            gameDataAfterWhite = gameDAO.retrieveGame(testGameID);
        } catch (DataAccessException e) {
            abort("Couldn't access the game");
        }
        assertEquals(TEST_USER.username(), gameDataAfterWhite.blackUsername());
        assertEquals(TEST_USER.username(), gameDataAfterWhite.whiteUsername());
    }

    @Test
    void joinGameAlreadyTaken() {
        JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, testGameID);
        assertDoesNotThrow(() -> serverFacade.joinGame(request, TEST_AUTH.authToken()));
        ServerFacade.ErrorResponseException error = assertThrows(ServerFacade.ErrorResponseException.class,
                () -> serverFacade.joinGame(request, TEST_AUTH.authToken()));
        assertEquals("Error: Already Taken", error.getMessage());
    }

    @Test
    void clearDb() {
        try {
            assumeTrue(authDAO.retrieveAuth(TEST_AUTH.authToken()).equals(TEST_AUTH), "Failed to get test auth");
            assumeTrue(gameDAO.retrieveAllGames().size() == 1, "There isn't exactly one game");
            assumeTrue(userDAO.getUser(TEST_USER.username()).equals(TEST_USER), "Failed to get test user");
        } catch (Exception e) {
            abort("Failed checking test data");
        }

        assertDoesNotThrow(() -> serverFacade.clearDb());

        try {
            assertThrows(AuthDAO.AuthDoesNotExistException.class, () -> authDAO.retrieveAuth(TEST_AUTH.authToken()), "Test auth wasn't cleared");
            assertEquals(0, gameDAO.retrieveAllGames().size(), "Games weren't all cleared");
            assertNull(userDAO.getUser(TEST_USER.username()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
