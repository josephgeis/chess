package dataaccess;

import model.GameData;
import model.UserData;

public class InitDefaultData {
    public static void main(String[] args) {
        try {
            DataAccessManager dataAccessManager = DataAccessManager.createPersistent();
            dataAccessManager.clearDatabases();

            UserDAO userDAO = dataAccessManager.getUserDAO();
            UserData joseph = new UserData("joseph", "password", "joseph@example.com");
            UserData anson = new UserData("anson", "password", "anson@example.com");
            userDAO.createUser(joseph);
            userDAO.createUser(anson);

            GameDAO gameDAO = dataAccessManager.getGameDAO();
            GameData testGame = GameData.withName("Test Game");
            gameDAO.createGame(testGame);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
