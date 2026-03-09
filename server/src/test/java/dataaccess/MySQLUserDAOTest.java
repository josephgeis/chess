package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MySQLUserDAOTest {

    static MySQLUserDAO userDAO;
    static String USERNAME = "username";
    static String PASSWORD = "password";
    static String EMAIL = "email";

    static String PASSWORD_HASHED = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());

    @BeforeAll
    static void setUp() {
        userDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUpTest() {
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void hashPassword() {
        final var hashedPassword = MySQLUserDAO.hashPassword(PASSWORD);
        assertTrue(BCrypt.checkpw(PASSWORD, hashedPassword));
    }

    @Test
    void validatePassword() {
        assertTrue(MySQLUserDAO.validatePassword(PASSWORD_HASHED, PASSWORD));
    }

    @Test
    void createUser() {
        try {
            userDAO.createUser(new UserData(USERNAME, PASSWORD, EMAIL));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select password from user where username=? and email=?")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, EMAIL);
            var rs = stmt.executeQuery();

            assertTrue(rs.next(), "No rows were inserted.");
            var passwordHash = rs.getString("password");
            assertTrue(() -> MySQLUserDAO.validatePassword(passwordHash, PASSWORD));
            assertFalse(rs.next(), "There was more than one thing inserted in!");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUser() {
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("insert into user(username, email, password) values (?, ?, ?)")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, EMAIL);
            stmt.setString(3, PASSWORD_HASHED);
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        var user = assertDoesNotThrow(() -> userDAO.getUser(USERNAME));
        assertEquals(USERNAME, user.username());
        assertEquals(EMAIL, user.email());
        assertTrue(BCrypt.checkpw(PASSWORD, user.password()));
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> userDAO.clear());
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select count(*) from user;")) {
            var rs = stmt.executeQuery();

            rs.next();
            assertEquals(0, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}