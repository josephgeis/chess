package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    static boolean validatePassword(String expectedHashed, String provided) {
        return BCrypt.checkpw(provided, expectedHashed);
    }

    static boolean validatePassword(UserData user, String password) {
        return BCrypt.checkpw(password, user.password());
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into user(username, email, password) values(?, ?, ?)")) {
            stmt.setString(1, u.username());
            stmt.setString(2, u.email());

            String hashedPassword = hashPassword(u.password());
            stmt.setString(3, hashedPassword);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user.");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "select * from user where username=?;")) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve user.");
        }
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
