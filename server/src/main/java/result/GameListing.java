package result;

import model.GameData;

/// This container is to represent the data in GameData without the ChessGame included, since the HTTP API does not
/// return ChessGame in it.
public record GameListing(int gameID, String whiteUsername, String blackUsername, String gameName) {
    public static GameListing from(GameData gameData) {
        return new GameListing(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
    }
}
