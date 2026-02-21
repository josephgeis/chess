package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.GameData;
import result.ListGamesResult;
import server.UnauthorizedRequestException;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        try {
            authDAO.retrieveAuth(authToken);
        } catch (AuthDAO.AuthDoesNotExistException ignored) {
            throw new UnauthorizedRequestException();
        }

        Collection<GameData> games = gameDAO.retrieveAllGames();

        return ListGamesResult.from(games);
    }
}
