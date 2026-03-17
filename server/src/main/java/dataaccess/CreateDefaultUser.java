package dataaccess;

import model.UserData;

public class CreateDefaultUser {
    public static void main(String[] args) {
        UserDAO userDAO = new MySQLUserDAO();
        try {
            if (args.length < 3) {
                throw new Exception("Not enough arguments.");
            }
            userDAO.createUser(new UserData(args[0], args[1], args[2]));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
