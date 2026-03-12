package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select whiteUsername, blackUsername, gameName, game.game " +
                     "from game where id = ?")) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String whiteUsername = rs.getString(1);
                String blackUsername = rs.getString(2);
                String gameName = rs.getString(3);
                ChessGame game = ChessGame.fromJson(rs.getString(4));

                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            } else {
                throw new GameDoesNotExistException();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game with id %d.".formatted(gameID), e);
        }
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("update game " +
                     "set whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? " +
                     "where id = ?")) {
            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, updatedGame.gameName());
            stmt.setString(4, updatedGame.game().toJson());
            stmt.setInt(5, gameID);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new GameDoesNotExistException();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new InvalidUserException();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public Collection<GameData> retrieveAllGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select id, whiteUsername, blackUsername, gameName, game " +
                     "from game")) {
            ResultSet rs = stmt.executeQuery();

            ArrayList<GameData> result = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String whiteUsername = rs.getString(2);
                String blackUsername = rs.getString(3);
                String gameName = rs.getString(4);
                ChessGame game = ChessGame.fromJson(rs.getString(5));
                result.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
