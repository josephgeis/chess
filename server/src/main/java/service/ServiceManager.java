package service;

import dataaccess.*;

public class ServiceManager {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public ServiceManager(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;

        authService = new AuthService(authDAO, userDAO);
        gameService = new GameService(gameDAO);
        userService = new UserService(userDAO, authDAO);
    }

    public ServiceManager() {
        this(new MemoryAuthDAO(),
                new MemoryGameDAO(),
                new MemoryUserDAO());
    }

    public AuthService getAuthService() {
        return authService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void clearDatabases() throws Exception {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
