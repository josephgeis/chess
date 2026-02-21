package dataaccess;

import model.AuthData;

public interface AuthDAO extends BaseDAO {
    void createAuth(AuthData a) throws DataAccessException;
    AuthData retrieveAuth(String authToken) throws DataAccessException;
    void destroyAuth(String authToken) throws DataAccessException;

    class AuthDoesNotExistException extends DataAccessException {
        public AuthDoesNotExistException() {
            super("The given authentication token could not be found in the database.");
        }
    }
}
