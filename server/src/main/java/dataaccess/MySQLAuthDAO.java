package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into session (username, token) values (?, ?);")) {
            stmt.setString(1, a.username());
            stmt.setString(2, a.authToken());

            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
          throw new DataAccessException("Cannot create session for non-existent user");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create session.");
        }
    }

    @Override
    public AuthData retrieveAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void destroyAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("delete from session where 1;")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear table");
        }
    }
}
