package model;

public record UserData(String username, String password, String email) {
    // TODO: Move this into MemoryAuthDAO, make it a method of AuthDAO/UserDAO?
    @Deprecated
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}
