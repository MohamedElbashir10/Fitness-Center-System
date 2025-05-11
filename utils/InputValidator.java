package utils;

public class InputValidator {

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isPositiveInteger(String str) {
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
