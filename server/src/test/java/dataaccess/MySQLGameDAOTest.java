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
        record TestScenario(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName,
            ChessGame chessGame
        ) {
            static TestScenario build(int gameID, String whiteUsername, String blackUsername, String gameName, int row, int col) {
                ChessGame chessGame = new ChessGame();

                ChessBoard chessBoard = new ChessBoard();
                chessBoard.addPiece(
                        new ChessPosition(row, col),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)
                );
                chessGame.setBoard(chessBoard);

                return new TestScenario(gameID, whiteUsername, blackUsername, gameName, chessGame);
            }
        }

        final static String GAME_NAME_EMPTY = "game_empty";
        final static String GAME_NAME_NO_WHITE = "game_no_white";
        final static String GAME_NAME_NO_BLACK = "game_no_black";
        final static String GAME_NAME_INVALID = "game_invalid";

        final static TestScenario SCENARIO_NORMAL = TestScenario.build(9001, WHITE_USERNAME, BLACK_USERNAME, GAME_NAME, 1, 1);
        final static TestScenario SCENARIO_EMPTY = TestScenario.build(9002, null, null, GAME_NAME_EMPTY, 2, 2);
        final static TestScenario SCENARIO_NO_WHITE = TestScenario.build(9003, null, BLACK_USERNAME, GAME_NAME_NO_WHITE, 3, 3);
        final static TestScenario SCENARIO_NO_BLACK = TestScenario.build(9004, WHITE_USERNAME, null, GAME_NAME_NO_BLACK, 4, 4);
        final static TestScenario SCENARIO_INVALID = TestScenario.build(9999, WHITE_USERNAME, null, GAME_NAME_INVALID, 5, 5);

        final static TestScenario[] SCENARIOS = {SCENARIO_NORMAL, SCENARIO_EMPTY, SCENARIO_NO_WHITE, SCENARIO_NO_BLACK};

        @BeforeEach
        void setUp() {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("insert into game(id, whiteUsername, blackUsername, gameName, game)" +
                         "values (?, ?, ?, ?, ?)")) {

                for (TestScenario scenario : SCENARIOS) {
                    stmt.setInt(1, scenario.gameID());
                    stmt.setString(2, scenario.whiteUsername());
                    stmt.setString(3, scenario.blackUsername());
                    stmt.setString(4, scenario.gameName());
                    stmt.setString(5, scenario.chessGame().toJson());

                    stmt.executeUpdate();
                }

            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void retrieveGame() {
            for (TestScenario scenario : SCENARIOS) {
                GameData gameData = assertDoesNotThrow(() -> gameDAO.retrieveGame(scenario.gameID));

                assertEquals(scenario.gameID(), gameData.gameID());
                assertEquals(scenario.whiteUsername(), gameData.whiteUsername());
                assertEquals(scenario.blackUsername(), gameData.blackUsername());
                assertEquals(scenario.gameName(), gameData.gameName());
                assertEquals(scenario.chessGame(), gameData.game());
            }
        }

        @Test
        void retrieveGameDoesntExist() {
            assertThrows(GameDAO.GameDoesNotExistException.class, () -> gameDAO.retrieveGame(SCENARIO_INVALID.gameID()));
        }

        @Test
        void updateGame() {
            GameData gameData = new GameData(
                    SCENARIO_EMPTY.gameID(),
                    WHITE_USERNAME,
                    BLACK_USERNAME,
                    SCENARIO_EMPTY.gameName(),
                    SCENARIO_EMPTY.chessGame());
            assertDoesNotThrow(() -> gameDAO.updateGame(gameData.gameID(), gameData));

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("select whiteUsername, blackUsername from game where id = ?")) {
                stmt.setInt(1, gameData.gameID());

                ResultSet rs = stmt.executeQuery();
                assertTrue(rs.next());

                String updatedWhiteUsername = rs.getString(1);
                String updatedBlackUsername = rs.getString(2);

                assertEquals(WHITE_USERNAME, updatedWhiteUsername);
                assertEquals(BLACK_USERNAME, updatedBlackUsername);
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void updateGameDoesntExist() {
            GameData gameData = new GameData(
                    SCENARIO_INVALID.gameID(),
                    SCENARIO_INVALID.whiteUsername(),
                    BLACK_USERNAME,
                    SCENARIO_INVALID.gameName(),
                    SCENARIO_INVALID.chessGame());
            assertThrows(GameDAO.GameDoesNotExistException.class, () -> gameDAO.updateGame(gameData.gameID(), gameData));
        }

        @Test
        void updateGameInvalidUser() {
            GameData gameData = new GameData(
                    SCENARIO_NO_BLACK.gameID(),
                    SCENARIO_NO_BLACK.whiteUsername(),
                    INVALID_USERNAME2,
                    SCENARIO_NO_BLACK.gameName(),
                    SCENARIO_NO_BLACK.chessGame());
            assertThrows(GameDAO.InvalidUserException.class, () -> gameDAO.updateGame(gameData.gameID(), gameData));
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