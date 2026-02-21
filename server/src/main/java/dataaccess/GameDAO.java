package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends BaseDAO {
    void createGame(GameData g) throws DataAccessException;
    GameData retrieveGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
    Collection<GameData> retrieveAllGames() throws DataAccessException;
}
