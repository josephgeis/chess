package dataaccess;

import model.GameData;

public interface GameDAO extends BaseDAO {
    void createGame(GameData g) throws DataAccessException;
    GameData retrieveGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
}
