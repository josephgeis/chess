package dataaccess;

import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    HashMap<Integer, GameData> gamesCollection;
    int nextGameID;

    public MemoryGameDAO() {
        clear();
    }

    int generateGameID() {
        return nextGameID++;
    }

    public void createGame(GameData g) {
        int gameID = generateGameID();
        GameData gameData = g.setGameID(gameID);
        gamesCollection.put(gameID, gameData);
    }

    public GameData retrieveGame(int gameID) throws DataAccessException {
        // FIXME: needs to throw exception
        return gamesCollection.get(gameID);
    }

    public void updateGame(int gameID, GameData g) throws DataAccessException {
        // FIXME: throw exception if ID doesn't exist.
        gamesCollection.put(gameID, g);
    }

    @Override
    public void clear() {
        gamesCollection = new HashMap<>();
        nextGameID = 1001;
    }
}
