import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class UserManager implements UserAuthentication {
    private Map<String, String> users;

    public UserManager() {
        this.users = new HashMap<>();
        // Add the admin user with the specified password
        addUser("admin", "admin123");
    }

    public void addUser(String username, String password) {
        users.put(username, password);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        // Add regular expression patterns for username and password
        String usernamePattern = "^[a-zA-Z0-9_]{3,20}$";
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

        // Check if the provided username is "admin" and the password matches the pattern
        return username.equals("admin") && Pattern.matches(passwordPattern, password)
                && Pattern.matches(usernamePattern, username);
    }
}
