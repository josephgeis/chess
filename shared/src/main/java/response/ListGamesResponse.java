package response;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public record ListGamesResponse(Collection<GameListing> games) {
    public static ListGamesResponse from(Collection<GameData> gameDataCollection) {
        ArrayList<GameListing> gameListings = new ArrayList<>();

        for (GameData gameData : gameDataCollection) {
            gameListings.add(GameListing.from(gameData));
        }

        return new ListGamesResponse(gameListings);
    }
}
