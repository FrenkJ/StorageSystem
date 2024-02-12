import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AppGUI {


    private UserAuthentication userManager;
    private DefaultTableModel tableModel;
    private List<Product> productList;
    private ProductManagement storageManager;
    private final Object productListLock = new Object();
    private JTable productsTable;

    public JFrame mainFrame;


    public AppGUI(ProductManagement storageManager, UserAuthentication userManager) {
        this.storageManager = storageManager;
        this.userManager = userManager;

        productList = new ArrayList<>();
        productList.add(new ConcreteProduct("Vegetable", 4.0, 200));
        productList.add(new ConcreteProduct("Detergent", 15.0, 200));
        productList.add(new ConcreteProduct("Sweets", 6.0, 200));
        productList.add(new ConcreteProduct("Fruits", 8.0, 200));

    }






    public void createAndShowLoginGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.setVisible(true);

        // Attach action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (userManager.authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");

                    // After successful login, close the login GUI and open the main GUI
                    frame.dispose();
                    createAndShowMainGUI();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password!");
                }
            }
        });
    }

    private void createAndShowMainGUI() {
        mainFrame = new JFrame("Supermarket Management");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        JPanel mainPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Product", "Price", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productsTable = new JTable(tableModel);
        refreshTable();  // Populate table with initial product data

        JScrollPane scrollPane = new JScrollPane(productsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Amount");
        JButton removeButton = new JButton("Remove Amount");
        JButton sendButton = new JButton("Send Product");

        addButton.addActionListener(e -> showAddProductDialog());
        removeButton.addActionListener(e -> showRemoveProductDialog());
        sendButton.addActionListener(e ->showSendProductsDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(sendButton);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setVisible(true);
    }

    private void showAddProductDialog() {
        JDialog addProductDialog = new JDialog(mainFrame, "Add Product", true);
        addProductDialog.setLayout(new FlowLayout());
        addProductDialog.setSize(300, 200);

        JComboBox<String> productComboBox = new JComboBox<>(productList.stream().map(Product::getName).toArray(String[]::new));
        JTextField amountField = new JTextField(5);

        JButton addAmountButton = new JButton("Add Amount");
        addAmountButton.addActionListener(e -> {
            String selectedProductName = (String) productComboBox.getSelectedItem();
            int amountToAdd = Integer.parseInt(amountField.getText());
            Product product = findProductByName(selectedProductName);
            if (product != null) {
                product.updateAmount(amountToAdd);
                refreshTable();
            }
            addProductDialog.dispose();
        });

        addProductDialog.add(new JLabel("Product Name:"));
        addProductDialog.add(productComboBox);
        addProductDialog.add(new JLabel("Add Amount:"));
        addProductDialog.add(amountField);
        addProductDialog.add(addAmountButton);

        addProductDialog.setVisible(true);
    }






    private void showRemoveProductDialog() {
        JDialog removeProductDialog = new JDialog(mainFrame, "Remove Amount", true);
        removeProductDialog.setLayout(new FlowLayout());
        removeProductDialog.setSize(300, 200);

        JComboBox<String> productComboBox = new JComboBox<>(productList.stream().map(Product::getName).toArray(String[]::new));
        JTextField amountField = new JTextField(5);

        JButton removeAmountButton = new JButton("Remove Amount");
        removeAmountButton.addActionListener(e -> {
            String selectedProductName = (String) productComboBox.getSelectedItem();
            int amountToRemove;
            try {
                amountToRemove = Integer.parseInt(amountField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(removeProductDialog, "Please enter a valid number.");
                return;
            }

            Product product = findProductByName(selectedProductName);
            if (product != null && amountToRemove > 0) {
                if (product.getAmount() >= amountToRemove) {
                    product.updateAmount(-amountToRemove); // Subtract the amount
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(removeProductDialog, "Cannot remove more than the current amount.");
                }
            } else {
                JOptionPane.showMessageDialog(removeProductDialog, "Product not found or invalid amount.");
            }
            removeProductDialog.dispose();
        });

        removeProductDialog.add(new JLabel("Product Name:"));
        removeProductDialog.add(productComboBox);
        removeProductDialog.add(new JLabel("Remove Amount:"));
        removeProductDialog.add(amountField);
        removeProductDialog.add(removeAmountButton);

        removeProductDialog.setVisible(true);
    }

    private void removeProductFromTable(String productName) {
        int rowIndex = findRowByProductName(productName);
        if (rowIndex != -1) {
            tableModel.removeRow(rowIndex);
        }
    }

    private void updateProductInTable(String productName, int newAmount) {
        int rowIndex = findRowByProductName(productName);
        if (rowIndex != -1) {
            // Directly update the table model with the new amount for the found row
            tableModel.setValueAt(newAmount, rowIndex, 2);
        }
    }


    private void showSendProductsDialog() {
        JFrame sendProductsFrame = new JFrame("Send Products");
        sendProductsFrame.setSize(400, 200);

        JPanel sendProductsPanel = new JPanel();
        sendProductsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Use a JComboBox for selecting the product to send
        JComboBox<String> productComboBox = new JComboBox<>(productList.stream()
                .map(Product::getName)
                .toArray(String[]::new));
        productComboBox.setPreferredSize(new Dimension(150, 25));

        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(150, 25));

        JButton sendButton = new JButton("Send");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 0;
        sendProductsPanel.add(new JLabel("Select Product:"), gbc);
        gbc.gridx = 1;
        sendProductsPanel.add(productComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        sendProductsPanel.add(new JLabel("Enter Amount:"), gbc);
        gbc.gridx = 1;
        sendProductsPanel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        sendProductsPanel.add(sendButton, gbc);

        gbc.gridx = 2;
        sendProductsPanel.add(cancelButton, gbc);

        sendProductsFrame.getContentPane().add(sendProductsPanel);

        sendProductsFrame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected product from the dropdown menu
                String selectedProduct = (String) productComboBox.getSelectedItem();

                // Find the product in the list
                Product product = findProductByName(selectedProduct);

                // Check if the product exists
                if (product != null) {
                    try {
                        int selectedAmount = Integer.parseInt(amountField.getText());

                        // Check if the selected amount is valid
                        if (selectedAmount <= 0 || selectedAmount > product.getAmount()) {
                            JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid quantity.");
                        } else {
                            // Update the product amount
                            product.updateAmount(-selectedAmount);

                            // Calculate profit (price * amount)
                            double profit = product.getPrice() * selectedAmount;

                            // Print the product details and profit to an output .txt file
                            printProductAndProfitToFile(product, selectedAmount, profit);

                            // Update the table with the latest data
                            refreshTable();

                            // Close the dialog
                            sendProductsFrame.dispose();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number for the amount.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selected product not found.");
                }
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the dialog
                sendProductsFrame.dispose();
            }
        });
    }

    private String[] getProductNamesArray() {
        String[] productNames = new String[productList.size()];
        for (int i = 0; i < productList.size(); i++) {
            productNames[i] = productList.get(i).getName();
        }
        return productNames;
    }



    private void printProductAndProfitToFile(Product product, int amount, double profit) {
        try {
            // Create a FileWriter and BufferedWriter to write to the file
            FileWriter fileWriter = new FileWriter("src/text_file/output.txt", true); // Set the file path accordingly
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the product details to the file
            bufferedWriter.write("Product Name: " + product.getName() + "\n");
            bufferedWriter.write("Price: " + product.getPrice() + "\n");
            bufferedWriter.write("Amount Sent: " + amount + "\n");
            bufferedWriter.write("Profit: " + profit + "\n");
            bufferedWriter.write("\n"); // Add a newline for separation

            // Close the BufferedWriter
            bufferedWriter.close();

            // Notify the user
            JOptionPane.showMessageDialog(null, "Product details and profit written to output.txt");
        } catch (IOException ex) {
            // Handle IOException
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error writing to file: " + ex.getMessage());
        }
    }
    private int findRowByProductName(String productName) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (productName.equals(tableModel.getValueAt(i, 0))) {
                return i;
            }
        }
        return -1;  // Not found
    }


    // Add this method to refresh the table
    // Modify the refreshTable method
    private void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear the current rows
            for (Product product : productList) {
                Object[] rowData = {product.getName(), product.getPrice(), product.getAmount()};
                tableModel.addRow(rowData);
            }
        });
    }


    private Product createNewProductInstance(String name, double price, int amount) {
        return new ConcreteProduct(name, price, amount);
    }

    private Product findProductByName(String name) {
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }




    private void removeSelectedProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the product name from the selected row
            String productName = (String) tableModel.getValueAt(selectedRow, 0);

            // Find and remove the product from the storageManager
            Product product = findProductByName(productName);
            if (product != null) {
                storageManager.removeProduct(product);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a product to remove.");
        }
    }


    public void removeProductByName(String name) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(name)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }
    private void modifyProductList(String productName, double price, int amount) {
        synchronized (productListLock) {
            Product existingProduct = findProductByName(productName);

            if (existingProduct != null) {
                // If the product already exists, update the amount and price
                existingProduct.updateAmount(amount);
                existingProduct.updatePrice(price);
            } else {
                // If the product doesn't exist, create a new instance and add it to the list
                Product newProduct = createNewProductInstance(productName, price, amount);
                productList.add(newProduct);
            }

            // Update the table with the latest data
            refreshTable();
        }
    }


}




