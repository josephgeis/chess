package dataaccess;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLGameDAOTest {

    MySQLGameDAO gameDAO = new MySQLGameDAO();

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
        fail();
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