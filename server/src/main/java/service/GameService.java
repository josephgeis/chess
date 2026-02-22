package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.AlreadyTakenException;
import server.MalformedRequestException;
import server.UnauthorizedRequestException;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    private AuthData validateAuthToken(String authToken) throws DataAccessException, UnauthorizedRequestException {
        try {
            return authDAO.retrieveAuth(authToken);
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

    public void joinGame(String authToken, JoinGameRequest request) throws Exception {
        final AuthData authenticatedUser = validateAuthToken(authToken);
        final GameData gameData = gameDAO.retrieveGame(request.gameID());

        if (gameData == null) {
            throw new MalformedRequestException();
        }

        final GameData updatedGameData = switch (request.playerColor()) {
            case WHITE -> {
                if (gameData.whiteUsername() != null) {
                    throw new AlreadyTakenException();
                }
                yield gameData.setWhiteUsername(authenticatedUser.username());
            }
            case BLACK -> {
                if (gameData.blackUsername() != null) {
                    throw new AlreadyTakenException();
                }
                yield gameData.setBlackUsername(authenticatedUser.username());
            }
        };

        gameDAO.updateGame(gameData.gameID(), updatedGameData);
    }
}
