import javax.swing.*;

public class SupermarketManagementApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize storage manager
            ProductManagement storageManager = new StorageManager();

            // Initialize user manager and add a user
            UserManager userManager = new UserManager();
            userManager.addUser("admin", "admin123");


            // Create AppGUI instance and call createAndShowLoginGUI
            AppGUI appGUI = new AppGUI(storageManager, userManager);
            appGUI.createAndShowLoginGUI();
        });
    }
}

