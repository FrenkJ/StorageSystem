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
    private Map<String, Double> predefinedProducts;

    private DefaultTableModel tableModel;
    private List<Product> productList;
    private ProductManagement storageManager;
    private final Object productListLock = new Object();


    public AppGUI(ProductManagement storageManager, UserAuthentication userManager) {
        this.storageManager = storageManager;
        this.userManager = userManager;

        productList = new ArrayList<>();
        productList.add(new Vegetable("Vegetable", 4.0,200));
        productList.add(new Detergent("Detergent", 15.0,200));
        productList.add(new Sweets("Sweets", 6.0,200));
        productList.add(new Fruits("Fruits", 8.0,200));

    }

    // Rest of the class remains unchanged
    // ...



    public void createAndShowLoginGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);

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

    public void createAndShowMainGUI() {
        JFrame mainFrame = new JFrame("Supermarket Management");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        String[] columnNames = {"Product", "Price", "Amount"};
        tableModel = new DefaultTableModel(null, columnNames);
        JTable productsTable = new JTable(tableModel);

        // Use productList instead of predefinedProducts to populate the table initially
        for (Product product : productList) {
            Object[] rowData = {product.getName(), product.getPrice(), product.getAmount()};
            tableModel.addRow(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(productsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Product");
        JButton removeButton = new JButton("Remove Amount");  // Updated button label
        JButton sendButton = new JButton("Send to Another Supermarket");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(sendButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);

        addButton.addActionListener(e -> {
            // Show dialog to add a new product
            showAddProductDialog();
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();

                // Check if a row is selected
                if (selectedRow != -1) {
                    // Get the product name from the selected row
                    String productName = (String) tableModel.getValueAt(selectedRow, 0);

                    // Find the product in the list
                    Product product = findProductByName(productName);
                    refreshTable();
                    // Check if the product exists
                    if (product != null) {
                        // Show dialog to remove product amount
                        showRemoveAmountDialog(product);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a product to remove.");
                }
            }
        });



        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show dialog to send products to another supermarket
                showSendProductsDialog();
            }
        });
    }







    private void showAddProductDialog() {
        JFrame addProductFrame = new JFrame("Add Product");
        addProductFrame.setSize(600, 350);

        JPanel addProductPanel = new JPanel();
        addProductPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        JComboBox<String> productComboBox = new JComboBox<>(getProductNamesArray());
        productComboBox.setPreferredSize(new Dimension(150, 25));
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(150, 25));
        JTextField priceField = new JTextField();
        priceField.setPreferredSize(new Dimension(150, 25));

        gbc.gridx = 0;
        gbc.gridy = 0;
        addProductPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        addProductPanel.add(productComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addProductPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        addProductPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addProductPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        addProductPanel.add(priceField, gbc);

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 1;
        gbc.gridy = 3;
        addProductPanel.add(addButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        addProductPanel.add(cancelButton, gbc);

        addProductFrame.getContentPane().add(addProductPanel);

        addProductFrame.setVisible(true);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedProduct = (String) productComboBox.getSelectedItem();
                    int amount = Integer.parseInt(amountField.getText());
                    double price = Double.parseDouble(priceField.getText());

                    // Check if the product already exists
                    Product existingProduct = findProductByName(selectedProduct);
                    if (existingProduct != null) {
                        // If the product already exists, update the amount
                        existingProduct.updateAmount(amount);
                    } else {
                        // If the product doesn't exist, create a new instance and add it to the list
                        Product newProduct = createNewProductInstance(selectedProduct, price, amount);
                        productList.add(newProduct);
                    }

                    // Update the table with the latest data
                    refreshTable();

                    // Close the dialog
                    addProductFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers for amount and price.");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductFrame.dispose();
            }
        });
    }

    private void showRemoveAmountDialog(Product product) {
        JFrame removeAmountFrame = new JFrame("Remove Amount");
        removeAmountFrame.setSize(400, 200);

        JPanel removeAmountPanel = new JPanel();
        removeAmountPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(150, 25));

        JButton removeAmountButton = new JButton("Remove Amount");

        gbc.gridx = 0;
        gbc.gridy = 0;
        removeAmountPanel.add(new JLabel("Enter Amount to Remove:"), gbc);
        gbc.gridx = 1;
        removeAmountPanel.add(amountField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        removeAmountPanel.add(removeAmountButton, gbc);

        removeAmountFrame.getContentPane().add(removeAmountPanel);

        removeAmountFrame.setVisible(true);

        removeAmountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int amountToRemove = Integer.parseInt(amountField.getText());

                    // Check if the amount to remove is valid
                    if (amountToRemove <= 0 || amountToRemove > product.getAmount()) {
                        JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid quantity.");
                    } else {
                        // Update the product amount
                        product.updateAmount(-amountToRemove);

                        // If the amount becomes 0, remove the product from the list and table
                        if (product.getAmount() == 0) {
                            productList.remove(product);
                            removeProductFromTable(product.getName());
                            refreshTable();
                        } else {
                            // Update the table with the latest data
                            updateProductInTable(product.getName(), product.getAmount());  // Use this method
                            refreshTable();
                        }

                        // Close the dialog
                        removeAmountFrame.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number for the amount.");
                }
            }
        });
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
            productList.get(0).updateAmount(newAmount);
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
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear the current rows
        for (Product product : productList) {
            Object[] rowData = {product.getName(), product.getPrice(), product.getAmount()};
            tableModel.addRow(rowData);
        }
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




    public void removeSelectedProduct(JTable productsTable) {
        synchronized (productListLock) {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow != -1) {
                // Get the product name from the selected row
                String productName = (String) tableModel.getValueAt(selectedRow, 0);

                // Remove the product from the table
                tableModel.removeRow(selectedRow);

                // Remove the product from the productList
                removeProductByName(productName);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a product to remove.");
            }
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




