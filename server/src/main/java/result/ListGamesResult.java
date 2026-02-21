package result;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public record ListGamesResult(Collection<GameListing> games) {
    public static ListGamesResult from(Collection<GameData> gameDataCollection) {
        ArrayList<GameListing> gameListings = new ArrayList<>();

        for (GameData gameData : gameDataCollection) {
            gameListings.add(GameListing.from(gameData));
        }

        return new ListGamesResult(gameListings);
    }
}
