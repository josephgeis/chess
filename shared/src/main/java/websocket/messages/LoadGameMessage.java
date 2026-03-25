package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    GameData gameData;

    public LoadGameMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = gameData;
    }

    public GameData getGameData() {
        return gameData;
    }
}
