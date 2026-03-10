package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MySQLAuthDAOTest {

    MySQLAuthDAO authDAO = new MySQLAuthDAO();

    final String USERNAME = "username";
    final String USERNAME_INVALID = "username_invalid";
    final String AUTH_TOKEN = "auth_token";

    @BeforeEach
    void setUp() {
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
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
        fail("Not implemented");
    }

    @Test
    void destroyAuth() {
        fail("Not implemented");
    }

    @Test
    void clear() {
        fail("Not implemented");
    }
}