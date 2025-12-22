package screens;

import components.AdminNavBarPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * AdminInventoryScreen allows admins to manage inventory items.
 * Includes Quantity tracking and Out of Stock indicators.
 *
 * @author Your Name
 * @version 2.0
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
        int quantity; // New field

        InventoryItem(String itemName, String description, double value, String category, int quantity) {
            this.itemName = itemName;
            this.description = description;
            this.value = value;
            this.category = category;
            this.quantity = quantity;
        }
    }

    public AdminInventoryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        inventoryItems = new ArrayList<>();
        loadInventoryData();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel navBarPanel = new AdminNavBarPanel("Inventory");
        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    private void loadInventoryData() {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) file = new File("items/inventory.json");
            if (!file.exists()) file = new File("../items/inventory.json");

            if (!file.exists()) {
                System.err.println("Could not find inventory.json");
                return;
            }

            try (Scanner scanner = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) content.append(scanner.nextLine());
                parseInventoryJson(content.toString());
            }
        } catch (Exception e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    private void parseInventoryJson(String jsonContent) {
        try {
            String jsonStr = jsonContent.replaceAll("\\s+", " ");
            int arrayStart = jsonStr.indexOf('[');
            int arrayEnd = jsonStr.lastIndexOf(']');
            if (arrayStart == -1 || arrayEnd == -1) return;
            jsonStr = jsonStr.substring(arrayStart + 1, arrayEnd);
            String[] items = jsonStr.split("\\},\\s*\\{");

            inventoryItems.clear(); // Clear existing list before repopulating

            for (String item : items) {
                item = item.trim();
                if (item.startsWith("{")) item = item.substring(1);
                if (item.endsWith("}")) item = item.substring(0, item.length() - 1);
                if (item.isEmpty()) continue;

                try {
                    String itemName = extractJsonValue(item, "itemName");
                    String description = extractJsonValue(item, "description");
                    String valueStr = extractJsonValue(item, "value");
                    String category = extractJsonValue(item, "category");
                    String quantityStr = extractJsonValue(item, "quantity");

                    if (itemName != null && description != null && valueStr != null && category != null) {
                        double value = Double.parseDouble(valueStr.replaceAll(",", "").trim());
                        // Default to 0 if quantity is missing in old JSON
                        int quantity = (quantityStr != null) ? Integer.parseInt(quantityStr.trim()) : 0;

                        inventoryItems.add(new InventoryItem(itemName, description, value, category, quantity));
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
    }

    private String extractJsonValue(String jsonStr, String key) {
        String searchStr = "\"" + key + "\"";
        int keyIndex = jsonStr.indexOf(searchStr);
        if (keyIndex == -1) return null;
        int colonIndex = jsonStr.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        int valueStart = colonIndex + 1;
        while (valueStart < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(valueStart))) valueStart++;
        if (valueStart >= jsonStr.length()) return null;

        if (jsonStr.charAt(valueStart) == '"') {
            int valueEnd = jsonStr.indexOf('"', valueStart + 1);
            return (valueEnd != -1) ? jsonStr.substring(valueStart + 1, valueEnd) : null;
        } else {
            int valueEnd = valueStart;
            while (valueEnd < jsonStr.length() && jsonStr.charAt(valueEnd) != ',' && jsonStr.charAt(valueEnd) != '}') valueEnd++;
            return jsonStr.substring(valueStart, valueEnd).trim();
        }
    }

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

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Added "Quantity" column
        String[] columnNames = {"Item Name", "Description", "Value", "Category", "Qty"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (InventoryItem item : inventoryItems) {
            // Display "Out of Stock" text if qty is 0, handled by renderer below
            tableModel.addRow(new Object[]{
                    item.itemName,
                    item.description,
                    String.format("PHP %.2f", item.value),
                    item.category,
                    item.quantity
            });
        }

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(40);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.getTableHeader().setBackground(new Color(200, 200, 200));
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        // Custom Renderer to highlight Out of Stock
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Check the Quantity column (Index 4)
                Object qtyObj = table.getValueAt(row, 4);
                int qty = 0;
                if (qtyObj instanceof Integer) qty = (Integer) qtyObj;
                else if (qtyObj instanceof String) try { qty = Integer.parseInt((String)qtyObj); } catch(Exception e){}

                if (qty == 0) {
                    c.setForeground(Color.RED);
                    if (column == 4) setText("Out of Stock");
                } else {
                    c.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    c.setBackground(inventoryTable.getSelectionBackground());
                    c.setForeground(inventoryTable.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450); // Increased height for new field
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Item Name:"); JTextField nameField = new JTextField();
        JLabel descLabel = new JLabel("Description:"); JTextField descField = new JTextField();
        JLabel valueLabel = new JLabel("Value:"); JTextField valueField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"1", "2", "3"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);

        // New Quantity Field
        JLabel qtyLabel = new JLabel("Quantity:"); JTextField qtyField = new JTextField();

        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(descLabel); formPanel.add(descField);
        formPanel.add(valueLabel); formPanel.add(valueField);
        formPanel.add(categoryLabel); formPanel.add(categoryCombo);
        formPanel.add(qtyLabel); formPanel.add(qtyField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (name.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                InventoryItem newItem = new InventoryItem(name, desc, value, category, qty);
                inventoryItems.add(newItem);
                tableModel.addRow(new Object[]{name, desc, String.format("PHP %.2f", value), category, qty});
                saveInventoryData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid value/quantity format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditItemDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InventoryItem item = inventoryItems.get(selectedRow);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Item Name:"); JTextField nameField = new JTextField(item.itemName);
        JLabel descLabel = new JLabel("Description:"); JTextField descField = new JTextField(item.description);
        JLabel valueLabel = new JLabel("Value:"); JTextField valueField = new JTextField(String.valueOf(item.value));
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"1", "2", "3"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(item.category);

        JLabel qtyLabel = new JLabel("Quantity:"); JTextField qtyField = new JTextField(String.valueOf(item.quantity));

        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(descLabel); formPanel.add(descField);
        formPanel.add(valueLabel); formPanel.add(valueField);
        formPanel.add(categoryLabel); formPanel.add(categoryCombo);
        formPanel.add(qtyLabel); formPanel.add(qtyField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (name.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                item.itemName = name;
                item.description = desc;
                item.value = value;
                item.category = category;
                item.quantity = qty;

                tableModel.setValueAt(name, selectedRow, 0);
                tableModel.setValueAt(desc, selectedRow, 1);
                tableModel.setValueAt(String.format("PHP %.2f", value), selectedRow, 2);
                tableModel.setValueAt(category, selectedRow, 3);
                tableModel.setValueAt(qty, selectedRow, 4);

                saveInventoryData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid value/quantity format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?","Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            inventoryItems.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            saveInventoryData();
        }
    }

    private void saveInventoryData() {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) file = new File("items/inventory.json");
            if (!file.exists()) {
                file = new File("../items/inventory.json");
                file.getParentFile().mkdirs();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("[");
            for (int i = 0; i < inventoryItems.size(); i++) {
                InventoryItem item = inventoryItems.get(i);
                writer.println("  {");
                writer.println("    \"itemName\": \"" + item.itemName + "\",");
                writer.println("    \"description\": \"" + item.description + "\",");
                writer.println("    \"value\": " + item.value + ",");
                writer.println("    \"category\": \"" + item.category + "\",");
                writer.println("    \"quantity\": " + item.quantity); // Save Quantity
                writer.print("  }");
                if (i < inventoryItems.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("]");
            writer.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}