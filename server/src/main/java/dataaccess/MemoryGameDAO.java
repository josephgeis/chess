package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    HashMap<Integer, GameData> gamesCollection;
    int nextGameID;
    final int firstGameID;

    public MemoryGameDAO() {
        this(1001);
    }

    public MemoryGameDAO(int firstGameID) {
        this.firstGameID = firstGameID;
        clear();
    }

    int generateGameID() {
        return nextGameID++;
    }

    public int createGame(GameData g) {
        int gameID = generateGameID();

        GameData gameData = g.setGameID(gameID);
        gamesCollection.put(gameID, gameData);

        return gameID;
    }

    public GameData retrieveGame(int gameID) {
        // FIXME: needs to throw exception (or does it?)
        return gamesCollection.get(gameID);
    }

    public void updateGame(int gameID, GameData g) throws DataAccessException {
        if (!gamesCollection.containsKey(gameID)) {
            throw new GameDoesNotExistException();
        }

        gamesCollection.put(gameID, g);
    }

    public Collection<GameData> retrieveAllGames() {
        return gamesCollection.values();
    }

    public void clear() {
        gamesCollection = new HashMap<>();
        nextGameID = firstGameID;
    }
}
