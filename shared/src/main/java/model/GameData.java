package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    /// The game ID will get set later on a new GameData object.
    public GameData setGameID(int gameID) {
        return new GameData(gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public static GameData withName(String gameName) {
        return new GameData(0, null, null, gameName, new ChessGame());
    }
}
