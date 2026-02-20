package service;

import dataaccess.GameDAO;

public class GameService {
    private GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }
}
