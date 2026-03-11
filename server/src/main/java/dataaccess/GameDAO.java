package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends BaseDAO {
    int createGame(GameData g) throws DataAccessException;
    GameData retrieveGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
    Collection<GameData> retrieveAllGames() throws DataAccessException;

    class GameDoesNotExistException extends DataAccessException {
        GameDoesNotExistException() {
            super("No game exists with the given game ID.");
        }
    }

    public class InvalidUserException extends DataAccessException {
        InvalidUserException() { super("User does not exist"); }
    }
}
