package passoff.service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import result.GameListing;
import result.ListGamesResult;
import service.GameService;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {
    static GameDAO gameDAO;
    static AuthDAO authDAO;

    static GameService gameService;

    static final String USERNAME = "username";
    static final String AUTH_TOKEN = "auth_token";
    static final int GAME_ID = 9001;
    static final String GAME_NAME = "my_chess_game";
    static final String GAME_NAME_TWO = "my_other_chess_game";

    @BeforeAll
    static void setUp() {
        gameDAO = new MemoryGameDAO(GAME_ID);
        authDAO = new MemoryAuthDAO();

        gameService = new GameService(gameDAO, authDAO);

        assertDoesNotThrow(() -> authDAO.createAuth(new AuthData(AUTH_TOKEN, USERNAME)));
    }

    @Test
    void listGamesEmpty() {
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));
        assertTrue(result.games().isEmpty(), "Expected there to be no games.");
    }

    @Test
    void listGamesOne() {
        GameData gameData = GameData.withName(GAME_NAME);
        int game_id = assertDoesNotThrow(() -> gameDAO.createGame(gameData));
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));

        assertEquals(1, result.games().size());
        GameListing firstGame = result.games().iterator().next();

        assertEquals(game_id, firstGame.gameID());
        assertEquals(GAME_NAME,  firstGame.gameName());
        assertNull(firstGame.whiteUsername());
        assertNull(firstGame.blackUsername());
    }

    @Test
    void listGamesTwo() {
        assertDoesNotThrow(() -> gameDAO.clear());

        GameData gameData = GameData.withName(GAME_NAME);
        int game_id = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        GameData gameData2 = GameData.withName(GAME_NAME_TWO);
        int game_id_two = assertDoesNotThrow(() -> gameDAO.createGame(gameData2));
        ListGamesResult result = assertDoesNotThrow(() -> gameService.listGames(AUTH_TOKEN));

        assertEquals(2, result.games().size());
        Iterator<GameListing> gameListingIterator = result.games().iterator();
        GameListing firstGame = gameListingIterator.next();

        assertEquals(game_id, firstGame.gameID());
        assertEquals(GAME_NAME,  firstGame.gameName());
        assertNull(firstGame.whiteUsername());
        assertNull(firstGame.blackUsername());

        GameListing secondGame = gameListingIterator.next();
        assertEquals(game_id_two, secondGame.gameID());
        assertEquals(GAME_NAME_TWO,  secondGame.gameName());
        assertNull(secondGame.whiteUsername());
        assertNull(secondGame.blackUsername());

    }
}