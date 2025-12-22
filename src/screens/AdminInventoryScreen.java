package screens;

import components.AdminNavBarPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * AdminInventoryScreen allows admins to manage inventory items.
 * Provides functionality to add, edit, and delete inventory items.
 *
 * @author Your Name
 * @version 1.0
 */
public class AdminInventoryScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private java.util.List<InventoryItem> inventoryItems;

    /**
     * Inner class to represent an inventory item.
     */
    private static class InventoryItem {
        String itemName;
        String description;
        double value;
        String category;

        InventoryItem(String itemName, String description, double value, String category) {
            this.itemName = itemName;
            this.description = description;
            this.value = value;
            this.category = category;
        }
    }

    /**
     * Constructor that initializes and displays the AdminInventoryScreen.
     */
    public AdminInventoryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        inventoryItems = new ArrayList<>();
        loadInventoryData();

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar
        JPanel navBarPanel = new AdminNavBarPanel("Inventory");

        // Title and action buttons panel
        JPanel topPanel = createTopPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    /**
     * Loads inventory data from inventory.json file.
     */
    private void loadInventoryData() {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) {
                file = new File("../items/inventory.json");
            }

            if (!file.exists()) {
                System.err.println("Could not find inventory.json");
                return;
            }

            try (Scanner scanner = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine());
                }
                parseInventoryJson(content.toString());
            }
        } catch (Exception e) {
            System.err.println("Error loading inventory data: " + e.getMessage());
        }
    }

    /**
     * Parses JSON content and populates inventory items list.
     */
    private void parseInventoryJson(String jsonContent) {
        String jsonStr = jsonContent;
        jsonStr = jsonStr.substring(jsonStr.indexOf('[') + 1, jsonStr.lastIndexOf(']'));

        String[] items = jsonStr.split("\\{");
        for (String item : items) {
            if (item.trim().isEmpty()) continue;

            try {
                int nameStart = item.indexOf("\"itemName\"") + 12;
                int nameEnd = item.indexOf("\"", nameStart + 1);
                String itemName = item.substring(nameStart, nameEnd);

                int descStart = item.indexOf("\"description\"") + 15;
                int descEnd = item.indexOf("\"", descStart + 1);
                String description = item.substring(descStart, descEnd);

                int valueStart = item.indexOf("\"value\"") + 9;
                int valueEnd = item.indexOf(",", valueStart);
                if (valueEnd == -1) valueEnd = item.indexOf("}", valueStart);
                String valueStr = item.substring(valueStart, valueEnd).trim();
                double value = Double.parseDouble(valueStr);

                int catStart = item.indexOf("\"category\"") + 12;
                int catEnd = item.indexOf("\"", catStart + 1);
                String category = item.substring(catStart, catEnd);

                inventoryItems.add(new InventoryItem(itemName, description, value, category));
            } catch (Exception e) {
                System.err.println("Error parsing item: " + e.getMessage());
            }
        }
    }

    /**
     * Creates the top panel with title and action buttons.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addButton = createActionButton("Add Item", new Color(100, 200, 150));
        addButton.addActionListener(e -> showAddItemDialog());

        JButton editButton = createActionButton("Edit Item", new Color(130, 170, 255));
        editButton.addActionListener(e -> showEditItemDialog());

        JButton deleteButton = createActionButton("Delete Item", new Color(255, 100, 100));
        deleteButton.addActionListener(e -> deleteSelectedItem());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        return topPanel;
    }

    /**
     * Creates an action button with specified text and color.
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Creates the table panel displaying inventory items.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        String[] columnNames = {"Item Name", "Description", "Value", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate table with inventory data
        for (InventoryItem item : inventoryItems) {
            tableModel.addRow(new Object[]{
                    item.itemName,
                    item.description,
                    String.format("$%.2f", item.value),
                    item.category
            });
        }

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(40);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.getTableHeader().setBackground(new Color(200, 200, 200));
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    /**
     * Shows dialog to add a new inventory item.
     */
    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();

        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();

        JLabel valueLabel = new JLabel("Value:");
        JTextField valueField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"1", "2", "3"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(valueLabel);
        formPanel.add(valueField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                if (name.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                InventoryItem newItem = new InventoryItem(name, desc, value, category);
                inventoryItems.add(newItem);
                tableModel.addRow(new Object[]{name, desc, String.format("$%.2f", value), category});
                saveInventoryData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid value format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Shows dialog to edit selected inventory item.
     */
    private void showEditItemDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InventoryItem item = inventoryItems.get(selectedRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField(item.itemName);

        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(item.description);

        JLabel valueLabel = new JLabel("Value:");
        JTextField valueField = new JTextField(String.valueOf(item.value));

        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"1", "2", "3"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(item.category);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(descLabel);
        formPanel.add(descField);
        formPanel.add(valueLabel);
        formPanel.add(valueField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                if (name.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                item.itemName = name;
                item.description = desc;
                item.value = value;
                item.category = category;

                tableModel.setValueAt(name, selectedRow, 0);
                tableModel.setValueAt(desc, selectedRow, 1);
                tableModel.setValueAt(String.format("$%.2f", value), selectedRow, 2);
                tableModel.setValueAt(category, selectedRow, 3);

                saveInventoryData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid value format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Deletes the selected inventory item.
     */
    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            inventoryItems.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            saveInventoryData();
        }
    }

    /**
     * Saves inventory data back to the JSON file.
     */
    private void saveInventoryData() {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) {
                file = new File("../items/inventory.json");
            }

            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("[");

            for (int i = 0; i < inventoryItems.size(); i++) {
                InventoryItem item = inventoryItems.get(i);
                writer.println("  {");
                writer.println("    \"itemName\": \"" + item.itemName + "\",");
                writer.println("    \"description\": \"" + item.description + "\",");
                writer.println("    \"value\": " + item.value + ",");
                writer.println("    \"category\": \"" + item.category + "\"");
                writer.print("  }");
                if (i < inventoryItems.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            writer.println("]");
            writer.close();

            JOptionPane.showMessageDialog(this, "Inventory saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
