package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    HashMap<String, AuthData> authCollection;

    public MemoryAuthDAO() {
        clear();
    }

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        authCollection.put(a.authToken(), a);
    }

    @Override
    public AuthData retrieveAuth(String authToken) throws DataAccessException {
        AuthData authData = authCollection.get(authToken);

        if (authData == null) {
            throw new AuthDoesNotExistException();
        } else {
            return authData;
        }
    }

    @Override
    public void destroyAuth(String authToken) throws DataAccessException {
        if (authCollection.remove(authToken) == null) {
            throw new AuthDoesNotExistException();
        }
    }

    @Override
    public void clear() {
        authCollection = new HashMap<>();
    }
}
