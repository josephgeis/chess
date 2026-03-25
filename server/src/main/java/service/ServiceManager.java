package service;

import dataaccess.*;

public class ServiceManager {
    private final DataAccessManager dataAccessManager;

    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public ServiceManager(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this(new DataAccessManager(authDAO, gameDAO, userDAO));
    }

    public ServiceManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;

        AuthDAO authDAO = dataAccessManager.getAuthDAO();
        GameDAO gameDAO = dataAccessManager.getGameDAO();
        UserDAO userDAO = dataAccessManager.getUserDAO();

        authService = new AuthService(authDAO, userDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
    }

    public static ServiceManager createPersistent() {
        return new ServiceManager(DataAccessManager.createPersistent());
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

    public void clearDatabases() throws DataAccessException {
        dataAccessManager.clearDatabases();
    }

    public DataAccessManager getDataAccessManager() {
        return dataAccessManager;
    }
}
