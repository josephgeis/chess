package dataaccess;

import model.UserData;

public interface UserDAO extends BaseDAO {
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    class UsernameAlreadyExistsException extends DataAccessException {
        public UsernameAlreadyExistsException(String username) {
            super("User with username " + username + " already exists.");
        }
    }
}
