package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLGameDAOTest {

    MySQLGameDAO gameDAO = new MySQLGameDAO();

    final static String GAME_NAME = "game";
    final static String WHITE_USERNAME = "white";
    final static String BLACK_USERNAME = "black";
    final static String INVALID_USERNAME1 = "user1";
    final static String INVALID_USERNAME2 = "user2";

    static boolean clearTestRun = false;
    static boolean clearTestPassed = false;

    @BeforeAll
    static void init() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("replace into user(username, email, password) values (?, '', ''), (?, '', '')")) {
            stmt.setString(1, WHITE_USERNAME);
            stmt.setString(2, BLACK_USERNAME);
            final int rowsCount = stmt.executeUpdate();
            assumeTrue(rowsCount >= 2 && rowsCount <= 4,
                    "Something wasn't right with inserting users into the database. rowsCount: %d".formatted(rowsCount));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
    void createGameInvalidUsers() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(0, INVALID_USERNAME1, INVALID_USERNAME2, GAME_NAME, chessGame);
        assertThrows(GameDAO.InvalidUserException.class, () -> gameDAO.createGame(gameData));
    }

    @Nested
    class GameManipulationTests {
        static class TestScenario {
            public int gameID;
            public String whiteUsername;
            public String blackUsername;
            public String gameName;
            public ChessGame chessGame;

            TestScenario(String whiteUsername, String blackUsername, String gameName, int row, int col) {
                this.gameID = 0;
                this.whiteUsername = whiteUsername;
                this.blackUsername = blackUsername;
                this.gameName = gameName;
                this.chessGame = new ChessGame();

                ChessBoard chessBoard = new ChessBoard();
                chessBoard.addPiece(
                        new ChessPosition(row, col),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
                );
                chessGame.setBoard(chessBoard);
            }
        }

        final static String GAME_NAME_EMPTY = "game_empty";
        final static String GAME_NAME_NO_WHITE = "game_no_white";
        final static String GAME_NAME_NO_BLACK = "game_no_black";

        final static TestScenario SCENARIO_NORMAL = new TestScenario(WHITE_USERNAME, BLACK_USERNAME, GAME_NAME, 1, 1);
        final static TestScenario SCENARIO_EMPTY = new TestScenario(WHITE_USERNAME, BLACK_USERNAME, GAME_NAME_EMPTY, 1, 2);
        final static TestScenario SCENARIO_NO_WHITE = new TestScenario(null, BLACK_USERNAME, GAME_NAME_NO_WHITE, 1, 3);
        final static TestScenario SCENARIO_NO_BLACK = new TestScenario(WHITE_USERNAME, null, GAME_NAME_NO_BLACK, 1, 4);
        final static TestScenario[] SCENARIOS = {SCENARIO_NORMAL, SCENARIO_EMPTY, SCENARIO_NO_WHITE, SCENARIO_NO_BLACK};

        @BeforeEach
        void setUp() {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("insert into game(whiteUsername, blackUsername, gameName, game)" +
                         "values (?, ?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {

                for (TestScenario scenario : SCENARIOS) {
                    stmt.setString(1, scenario.whiteUsername);
                    stmt.setString(2, scenario.blackUsername);
                    stmt.setString(3, scenario.gameName);
                    stmt.setString(4, scenario.chessGame.toJson());

                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();

                    assumeTrue(rs.next(), "Couldn't get ID for normal game");
                    scenario.gameID = rs.getInt(1);
                    assumeFalse(rs.next(), "More games inserted than expected.");
                }

            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void retrieveGame() {
            for (TestScenario scenario : SCENARIOS) {
                GameData gameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(scenario.gameID));

                assertEquals(scenario.gameID, gameData.gameID());
                assertEquals(scenario.whiteUsername, gameData.whiteUsername());
                assertEquals(scenario.blackUsername, gameData.blackUsername());
                assertEquals(scenario.gameName, gameData.gameName());
                assertEquals(scenario.chessGame, gameData.game());
            }
        }

        @Test
        void updateGame() {
            fail();
        }

        @Test
        void retrieveAllGames() {
            fail();
        }
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