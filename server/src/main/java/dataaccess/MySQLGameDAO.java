package dataaccess;

import model.GameData;

import java.sql.*;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData g) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "insert into game(whiteUsername, blackUsername, gameName, game) values (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS
             )) {
            conn.setAutoCommit(false);

            stmt.setString(1, g.whiteUsername());
            stmt.setString(2, g.blackUsername());
            stmt.setString(3, g.gameName());
            stmt.setString(4, g.game().toJson());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                conn.commit();
                return rs.getInt(1);
            } else {
                conn.rollback();
                throw new DataAccessException("Failed to insert into game table");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidUserException();
        } catch (SQLException e) {
            throw new DataAccessException("Failed insert into game table", e);
        }
    }

    @Override
    public GameData retrieveGame(int gameID) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<GameData> retrieveAllGames() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("delete from game where 1;")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear table", e);
        }
    }
}
