package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void createAuth(AuthData a) throws DataAccessException;
    public AuthData retrieveAuth(String authToken) throws DataAccessException;
    public void destroyAuth(String authToken) throws DataAccessException;

    public static class AuthDoesNotExistException extends DataAccessException {
        public AuthDoesNotExistException() {
            super("The given authentication token could not be found in the database.");
        }
    }
}
