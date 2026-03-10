package dataaccess;

import model.AuthData;

import java.sql.*;

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
        AuthData res;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select token, username from session where token = ?")) {
            stmt.setString(1, authToken);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                throw new AuthDoesNotExistException();
            }
            res = new AuthData(rs.getString(1), rs.getString(2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    @Override
    public void destroyAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("delete from session where token = ?")) {
            stmt.setString(1, authToken);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new AuthDoesNotExistException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
