package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MySQLUserDAOTest {

    static MySQLUserDAO userDAO;
    static String USERNAME = "username";
    static String PASSWORD = "password";
    static String EMAIL = "email";

    static String PASSWORD_HASHED = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());

    static boolean clearTestPassed = false;
    static boolean clearTestRun = false;

    @BeforeAll
    static void setUp() {
        userDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUpTest() {
        if (!clearTestRun) {
            return;
        } else if (!clearTestPassed) {
            abort("All other classes require the clear test to pass.");
        }

        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            abort("Failed to clear table. Did the clear test pass?");
        }
    }

    @Test
    void hashPassword() {
        final var hashedPassword = MySQLUserDAO.hashPassword(PASSWORD);
        assertTrue(BCrypt.checkpw(PASSWORD, hashedPassword));
    }

    @Test
    void createUser() {
        assertDoesNotThrow(() -> userDAO.createUser(new UserData(USERNAME, PASSWORD, EMAIL)));

        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select password from user where username=? and email=?")) {
            stmt.setString(1, USERNAME);
            stmt.setString(2, EMAIL);
            var rs = stmt.executeQuery();

            assertTrue(rs.next(), "No rows were inserted.");
            var passwordHash = rs.getString("password");
            assertTrue(() -> BCrypt.checkpw(PASSWORD, passwordHash));
            assertFalse(rs.next(), "There was more than one thing inserted in!");
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUserDuplicate() {
        final var userData = new UserData(USERNAME, PASSWORD, EMAIL);
        assertDoesNotThrow(() -> userDAO.createUser(userData));
        assertThrows(UserDAO.UsernameAlreadyExistsException.class, () -> userDAO.createUser(userData));
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
    void getUserDoesntExist() {
        var user = assertDoesNotThrow(() -> userDAO.getUser(USERNAME));
        assertNull(user);
    }

    @Order(1)
    @Test
    void clear() {
        clearTestRun = true;

        assertDoesNotThrow(() -> userDAO.clear());
        try(var conn = DatabaseManager.getConnection();
            var stmt = conn.prepareStatement("select count(*) from user;")) {
            var rs = stmt.executeQuery();

            rs.next();
            assertEquals(0, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        clearTestPassed = true;
    }
}