package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> userCollection;

    public MemoryUserDAO() {
        clear();
    }

    @Override
    public boolean validatePassword(UserData user, String password) {
        return user.password().equals(password);
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (userCollection.get(u.username()) != null) {
            throw new UsernameAlreadyExistsException(u.username());
        }

        userCollection.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) {
        return userCollection.get(username);
    }

    @Override
    public void clear() {
        userCollection = new HashMap<>();
    }
}
