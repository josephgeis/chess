package service;

import dataaccess.*;

public class ServiceManager {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    ServiceManager(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;

        authService = new AuthService(authDAO, userDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
    }

    public static ServiceManager createPersistent() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return new ServiceManager(
                new MySQLAuthDAO(),
                new MySQLGameDAO(),
                new MySQLUserDAO());
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
