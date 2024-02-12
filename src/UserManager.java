import java.util.HashMap;
import java.util.Map;

class UserManager implements UserAuthentication {
    private Map<String, String> users;

    public UserManager() {
        this.users = new HashMap<>();
    }

    public void addUser(String username, String password) {
        users.put(username, password);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
