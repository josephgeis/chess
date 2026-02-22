package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
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

    private void validateAuthToken(String authToken) throws DataAccessException, UnauthorizedRequestException {
        try {
            authDAO.retrieveAuth(authToken);
        } catch (AuthDAO.AuthDoesNotExistException ignored) {
            throw new UnauthorizedRequestException();
        }
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        validateAuthToken(authToken);

        Collection<GameData> games = gameDAO.retrieveAllGames();

        return ListGamesResult.from(games);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws Exception {
        validateAuthToken(authToken);

        final GameData newGame = GameData.withName(request.gameName());
        final int gameID = gameDAO.createGame(newGame);

        return new CreateGameResult(gameID);
    }
}
