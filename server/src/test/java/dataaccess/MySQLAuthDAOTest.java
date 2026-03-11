package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLAuthDAOTest {

    MySQLAuthDAO authDAO = new MySQLAuthDAO();

    final static String USERNAME = "username";
    final static String USERNAME_INVALID = "username_invalid";
    final static String AUTH_TOKEN = "auth_token";
    final static String AUTH_TOKEN_INVALID = "auth_token_invalid";

    static boolean clearTestPassed = false;
    static boolean clearTestRun = false;

    @BeforeEach
    void setUp() {
        if (!clearTestRun) {
            return;
        } else if (!clearTestPassed) {
            abort("All other classes require the clear test to pass.");
        }

        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            abort("Failed to clear table. Did the clear test pass?");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into user(username, email, password) values (?, '', '')")) {
            stmt.setString(1, USERNAME);
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAuth() {
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.createAuth(authData));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "select count(*) from session where username = ? and token = ?")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, AUTH_TOKEN);

            ResultSet rs = stmt.executeQuery();

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAuthInvalid() {
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME_INVALID);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData));
    }

    @Test
    void retrieveAuth() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into session(username, token) values (?, ?)")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, AUTH_TOKEN);

            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        AuthData authData = assertDoesNotThrow(() -> authDAO.retrieveAuth(AUTH_TOKEN));
        assertEquals(USERNAME, authData.username());
        assertEquals(AUTH_TOKEN, authData.authToken());
    }

    @Test
    void retrieveAuthNonExistent() {
        assertThrows(AuthDAO.AuthDoesNotExistException.class, () -> authDAO.retrieveAuth(AUTH_TOKEN_INVALID));
    }

    @Test
    void destroyAuth() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into session(username, token) values (?, ?)")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, AUTH_TOKEN);

            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() -> authDAO.destroyAuth(AUTH_TOKEN));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "select count(*) from session where token = ?")) {
            stmt.setString(1, AUTH_TOKEN);

            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void destroyAuthNonExistent() {
        assertThrows(AuthDAO.AuthDoesNotExistException.class, () -> authDAO.destroyAuth(AUTH_TOKEN_INVALID));
    }

    @Order(1)
    @Test
    void clear() {
        clearTestRun = true;
        assertDoesNotThrow(() -> authDAO.clear());
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select count(*) from session;")) {
            var rs = stmt.executeQuery();

            rs.next();
            assertEquals(0, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        clearTestPassed = true;
    }
}