package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLGameDAOTest {

    MySQLGameDAO gameDAO = new MySQLGameDAO();

    final static String GAME_NAME = "game";
    final static String WHITE_USERNAME = "white";
    final static String BLACK_USERNAME = "black";

    static boolean clearTestRun = false;
    static boolean clearTestPassed = false;

    @BeforeEach
    void setUp() {
        if (!clearTestRun) {
            return;
        } else if (!clearTestPassed) {
            abort("All other classes require the clear test to pass.");
        }

        try {
            gameDAO.clear();
        } catch (DataAccessException e) {
            abort("Failed to clear table. Did the clear test pass?");
        }
    }

    @Test
    void createGame() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(0, WHITE_USERNAME, BLACK_USERNAME, GAME_NAME, chessGame);
        int gameID = assertDoesNotThrow(() -> gameDAO.createGame(gameData));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select id, gameName, whiteUsername, blackUsername, game from game")) {
            ResultSet rs = stmt.executeQuery();

            assertTrue(rs.next());

            assertEquals(gameID, rs.getInt(1));
            assertEquals(GAME_NAME, rs.getString(2));
            assertEquals(WHITE_USERNAME, rs.getString(3));
            assertEquals(BLACK_USERNAME, rs.getString(4));

            ChessGame deserializedChessGame = ChessGame.fromJson(rs.getString(5));
            assertEquals(chessGame, deserializedChessGame);

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void retrieveGame() {
        fail();
    }

    @Test
    void updateGame() {
        fail();
    }

    @Test
    void retrieveAllGames() {
        fail();
    }

    @Order(1)
    @Test
    void clear() {
        clearTestRun = true;
        assertDoesNotThrow(() -> gameDAO.clear());
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select count(*) from game;")) {
            var rs = stmt.executeQuery();

            rs.next();
            assertEquals(0, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        clearTestPassed = true;
    }
}