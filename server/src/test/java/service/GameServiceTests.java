package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.GameListing;
import result.ListGamesResult;
import server.AlreadyTakenException;
import server.MalformedRequestException;
import server.UnauthorizedRequestException;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    static GameDAO gameDAO;
    static AuthDAO authDAO;

    static GameService gameService;

    static final String USERNAME = "username";
    static final String USERNAME_ALT = "alternate_username";
    static final String AUTH_TOKEN = "auth_token";
    static final String AUTH_TOKEN_ALT = "alternate_auth_token";
    static final String AUTH_TOKEN_INVALID = "invalid_auth_token";
    static final int gameID = 9001;
    static final String GAME_NAME = "my_chess_game";
    static final String GAME_NAME_TWO = "my_other_chess_game";

    @BeforeAll
    static void setUp() {
        gameDAO = new MemoryGameDAO(gameID);
        authDAO = new MemoryAuthDAO();

        gameService = new GameService(gameDAO, authDAO);

        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData(AUTH_TOKEN, USERNAME)));
        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData(AUTH_TOKEN_ALT, USERNAME_ALT)));
    }

    @BeforeEach
    void beforeEach() {
        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listGamesEmpty() {
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));
        assertTrue(result.games().isEmpty(), "Expected there to be no games.");
    }

    @Test
    void listGamesOne() {
        GameData gameData = GameData.withName(GAME_NAME);
        int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));

        assertEquals(1, result.games().size());
        GameListing firstGame = result.games().iterator().next();

        assertEquals(gameID, firstGame.gameID());
        assertEquals(GAME_NAME,  firstGame.gameName());
        assertNull(firstGame.whiteUsername());
        assertNull(firstGame.blackUsername());
    }

    @Test
    void listGamesTwo() {
        GameData gameData = GameData.withName(GAME_NAME);
        int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        GameData gameData2 = GameData.withName(GAME_NAME_TWO);
        int gameID2 = assertDoesNotThrow(() -> gameDAO.createGame(gameData2));
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));

        assertEquals(2, result.games().size());
        Iterator<GameListing> gameListingIterator = result.games().iterator();
        GameListing firstGame = gameListingIterator.next();

        assertEquals(gameID, firstGame.gameID());
        assertEquals(GAME_NAME,  firstGame.gameName());
        assertNull(firstGame.whiteUsername());
        assertNull(firstGame.blackUsername());

        GameListing secondGame = gameListingIterator.next();
        assertEquals(gameID2, secondGame.gameID());
        assertEquals(GAME_NAME_TWO,  secondGame.gameName());
        assertNull(secondGame.whiteUsername());
        assertNull(secondGame.blackUsername());
    }

    @Test
    void invalidAuth() {
        assertThrows(UnauthorizedRequestException.class,
                () -> gameService.listGames(AUTH_TOKEN_INVALID));
    }

    @Test
    void createGame() {
        CreateGameRequest request = new CreateGameRequest(GAME_NAME);
        CreateGameResult result = assertDoesNotThrow(() -> gameService.createGame(AUTH_TOKEN, request));
        GameData newGame = assertDoesNotThrow(() -> gameDAO.retrieveGame(result.gameID()));

        assertNotNull(newGame);
        assertEquals(GAME_NAME, newGame.gameName());
    }

    @Test
    void createGameUnauthorized() {
        CreateGameRequest request = new CreateGameRequest(GAME_NAME);
        assertThrows(UnauthorizedRequestException.class, () -> gameService.createGame(AUTH_TOKEN_INVALID, request));
    }

    @Test
    void createTwoGames() {
        CreateGameRequest request = new CreateGameRequest(GAME_NAME);
        CreateGameResult result = assertDoesNotThrow(() -> gameService.createGame(AUTH_TOKEN, request));
        GameData newGame = assertDoesNotThrow(() -> gameDAO.retrieveGame(result.gameID()));

        CreateGameRequest request2 = new CreateGameRequest(GAME_NAME_TWO);
        CreateGameResult result2 = assertDoesNotThrow(() -> gameService.createGame(AUTH_TOKEN, request2));
        GameData newGame2 = assertDoesNotThrow(() -> gameDAO.retrieveGame(result2.gameID()));

        assertNotNull(newGame);
        assertEquals(GAME_NAME, newGame.gameName());

        assertNotNull(newGame2);
        assertEquals(GAME_NAME_TWO, newGame2.gameName());
        assertNotEquals(newGame.gameID(), newGame2.gameID());
    }

    @Test
    void joinGameBlack() {
        final GameData gameData = GameData.withName(GAME_NAME);
        final int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(AUTH_TOKEN, request));
        final GameData updatedGameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(gameID));

        assertEquals(USERNAME, updatedGameData.blackUsername());
        assertNull(gameData.whiteUsername());
    }

    @Test
    void joinGameWhite() {
        final GameData gameData = GameData.withName(GAME_NAME);
        final int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(AUTH_TOKEN, request));
        final GameData updatedGameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(gameID));

        assertEquals(USERNAME, updatedGameData.whiteUsername());
        assertNull(gameData.blackUsername());
    }

    @Test
    void joinNonExistentGame() {
        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        assertThrows(MalformedRequestException.class, () -> gameService.joinGame(AUTH_TOKEN, request));
    }

    @Test
    void joinGameUnauthorized() {
        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        assertThrows(UnauthorizedRequestException.class, () -> gameService.joinGame(AUTH_TOKEN_INVALID, request));
    }

    @Test
    void joinGameBlackTaken() {
        final GameData gameData = GameData.withName(GAME_NAME);
        final int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(AUTH_TOKEN, request));
        final GameData updatedGameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(gameID));

        assertEquals(USERNAME, updatedGameData.blackUsername());
        assertNull(gameData.blackUsername());

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(AUTH_TOKEN_ALT, request));
    }

    @Test
    void joinGameWhiteTaken() {
        final GameData gameData = GameData.withName(GAME_NAME);
        final int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        final JoinGameRequest request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        assertDoesNotThrow(() -> gameService.joinGame(AUTH_TOKEN, request));
        final GameData updatedGameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(gameID));

        assertEquals(USERNAME, updatedGameData.whiteUsername());
        assertNull(gameData.blackUsername());

        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(AUTH_TOKEN_ALT, request));
    }
}