package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData g) throws DataAccessException {
        throw new RuntimeException("Not implemented");
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
