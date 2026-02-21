package model;

public record UserData(String username, String password, String email) {
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}
