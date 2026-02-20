package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> userCollection;

    public MemoryUserDAO() {
        userCollection = new HashMap<>();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (userCollection.get(u.username()) != null) {
            throw new UsernameAlreadyExistsException(u.username());
        }

        userCollection.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userCollection.get(username);
    }
}
