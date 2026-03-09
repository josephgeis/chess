package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySQLUserDAO implements UserDAO {

    static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    static boolean validatePassword(String expectedHashed, String provided) {
        return BCrypt.checkpw(provided, expectedHashed);
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into user(username, email, password) values(?, ?, ?)", RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.username());
            stmt.setString(2, u.email());

            String hashedPassword = hashPassword(u.password());
            stmt.setString(3, hashedPassword);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("delete from user where 1;")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear database");
        }
    }
}
