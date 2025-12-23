package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import main.MainActivity;

public class AdminInventoryScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private java.util.List<InventoryItem> inventoryItems;
    private String currentSearchText = "";

    private static class InventoryItem {
        String itemName;
        String description;
        double value;
        String category;
        int quantity;

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

        // --- SETUP NAV BAR ---
        AdminNavBarPanel navBarPanel = new AdminNavBarPanel("Inventory");

        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();

        // Set search listener AFTER table is created
        navBarPanel.setSearchListener(text -> {
            this.currentSearchText = text.toLowerCase().trim();
            populateTable();
        });

        navBarPanel.resetSearch();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    public void refreshData() {
        loadInventoryData();
        populateTable();
    }

    private void loadInventoryData() {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) file = new File("items/inventory.json");
            if (!file.exists()) file = new File("../items/inventory.json");

            if (!file.exists()) return;

            try (Scanner scanner = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) content.append(scanner.nextLine());
                parseInventoryJson(content.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            inventoryItems.clear();

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

                    if (itemName != null && valueStr != null) {
                        double value = Double.parseDouble(valueStr.replaceAll(",", "").trim());
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

        String[] columnNames = {"Item Name", "Description", "Value", "Category", "Qty"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        populateTable();

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(40);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inventoryTable.getTableHeader().setBackground(new Color(200, 200, 200));
        inventoryTable.getTableHeader().setReorderingAllowed(false);

        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object qtyObj = table.getValueAt(row, 4);
                int qty = 0;
                if (qtyObj instanceof Integer) qty = (Integer) qtyObj;

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

    private void populateTable() {
        if (tableModel == null) return; // Guard against null tableModel
        
        tableModel.setRowCount(0);

        for (InventoryItem item : inventoryItems) {
            if (currentSearchText.isEmpty() || item.itemName.toLowerCase().contains(currentSearchText)) {
                tableModel.addRow(new Object[]{
                        item.itemName,
                        item.description,
                        String.format("PHP %.2f", item.value),
                        item.category,
                        item.quantity
                });
            }
        }
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Item Name:"); JTextField nameField = new JTextField();
        JLabel descLabel = new JLabel("Description:"); JTextField descField = new JTextField();
        JLabel valueLabel = new JLabel("Value:"); JTextField valueField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"1", "2", "3"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
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

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                InventoryItem newItem = new InventoryItem(name, desc, value, category, qty);
                inventoryItems.add(newItem);
                saveInventoryData();
                populateTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Select item to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedName = (String) tableModel.getValueAt(selectedRow, 0);
        InventoryItem item = null;
        for(InventoryItem i : inventoryItems) {
            if(i.itemName.equals(selectedName)) {
                item = i;
                break;
            }
        }
        if (item == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(item.itemName);
        JTextField descField = new JTextField(item.description);
        JTextField valueField = new JTextField(String.valueOf(item.value));
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"1", "2", "3"});
        categoryCombo.setSelectedItem(item.category);
        JTextField qtyField = new JTextField(String.valueOf(item.quantity));

        formPanel.add(new JLabel("Item Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Description:")); formPanel.add(descField);
        formPanel.add(new JLabel("Value:")); formPanel.add(valueField);
        formPanel.add(new JLabel("Category:")); formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Quantity:")); formPanel.add(qtyField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        InventoryItem finalItem = item;
        saveButton.addActionListener(e -> {
            try {
                finalItem.itemName = nameField.getText().trim();
                finalItem.description = descField.getText().trim();
                finalItem.value = Double.parseDouble(valueField.getText().trim());
                finalItem.category = (String) categoryCombo.getSelectedItem();
                finalItem.quantity = Integer.parseInt(qtyField.getText().trim());

                saveInventoryData();
                populateTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
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
        if (selectedRow != -1) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String selectedName = (String) tableModel.getValueAt(selectedRow, 0);
                inventoryItems.removeIf(i -> i.itemName.equals(selectedName));
                saveInventoryData();
                populateTable();
            }
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
                writer.println("    \"quantity\": " + item.quantity);
                writer.print("  }");
                if (i < inventoryItems.size() - 1) writer.println(",");
                else writer.println();
            }
            writer.println("]");
            writer.close();

            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().refreshAllScreens();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}