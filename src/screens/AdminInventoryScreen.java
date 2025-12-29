package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import main.AppConstants;
import main.MainActivity;
import models.InventoryItem;
import models.InventoryManager;

// The inventory management interface for administrators
// Allows admins to view, add, edit, and delete inventory items with search functionality
public class AdminInventoryScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private InventoryManager inventoryManager;
    private String currentSearchText = "";
    private AdminNavBarPanel navBarPanel;
    
    // Category mapping
    private static final String[] CATEGORY_IDS = {"1", "2", "3"};
    private static final String[] CATEGORY_NAMES = {"Tools", "Building Materials", "Paint & Supplies"};

    public AdminInventoryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        inventoryManager = new InventoryManager();
        inventoryManager.loadInventory();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- SETUP NAV BAR ---
        navBarPanel = new AdminNavBarPanel("Inventory");

        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();

        // Set search listener AFTER table is created
        navBarPanel.setSearchListener(text -> {
            this.currentSearchText = text.toLowerCase().trim();
            populateTable();
        });

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        // Add component listener to detect when screen becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                resetSearch();
            }
        });
    }
    
    /**
     * Resets the search when screen becomes visible
     */
    private void resetSearch() {
        if (navBarPanel != null) {
            navBarPanel.resetSearch();
        }
        currentSearchText = "";
        populateTable();
    }

    public void refreshData() {
        inventoryManager.loadInventory();
        populateTable();
    }



    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(AppConstants.FONT_TITLE_XL);

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
        button.setFont(AppConstants.FONT_LABEL_BOLD);
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
        inventoryTable.setFont(AppConstants.FONT_BODY_REGULAR);
        inventoryTable.setRowHeight(40);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(AppConstants.FONT_LABEL_BOLD);
        inventoryTable.getTableHeader().setBackground(AppConstants.BG_MEDIUM_GRAY);
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
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_LIGHT_GRAY, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void populateTable() {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);

        for (InventoryItem item : inventoryManager.getAllItems()) {
            if (currentSearchText.isEmpty() || item.getName().toLowerCase().contains(currentSearchText)) {
                tableModel.addRow(new Object[]{
                        item.getName(),
                        item.description,
                        String.format("PHP %.2f", item.getPrice()),
                        getCategoryName(item.getCategory()),
                        item.getQuantity()
                });
            }
        }
    }
    
    // Converts category ID to human-readable name
    private String getCategoryName(String categoryId) {
        if (categoryId == null) return "Unknown";
        for (int i = 0; i < CATEGORY_IDS.length; i++) {
            if (CATEGORY_IDS[i].equals(categoryId)) {
                return CATEGORY_NAMES[i];
            }
        }
        return categoryId; // Return ID if not found
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
        String[] categories = {"1 - Tools", "2 - Building Materials", "3 - Paint & Supplies"};
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
                String categorySelection = (String) categoryCombo.getSelectedItem();
                String category = categorySelection.substring(0, 1);
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                InventoryItem newItem = new InventoryItem(name, value, category, qty);
                newItem.description = desc;
                inventoryManager.addItem(newItem);
                inventoryManager.saveInventory();
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
        InventoryItem item = inventoryManager.getItemByName(selectedName);
        if (item == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(item.getName());
        JTextField descField = new JTextField(item.description);
        JTextField valueField = new JTextField(String.valueOf(item.getPrice()));
        String[] categories = {"1 - Tools", "2 - Building Materials", "3 - Paint & Supplies"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        // Select the correct category based on item's category ID
        for (int i = 0; i < CATEGORY_IDS.length; i++) {
            if (CATEGORY_IDS[i].equals(item.getCategory())) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
        JTextField qtyField = new JTextField(String.valueOf(item.getQuantity()));

        formPanel.add(new JLabel("Item Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Description:")); formPanel.add(descField);
        formPanel.add(new JLabel("Value:")); formPanel.add(valueField);
        formPanel.add(new JLabel("Category:")); formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Quantity:")); formPanel.add(qtyField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String originalName = selectedName;
                String newName = nameField.getText().trim();
                String desc = descField.getText().trim();
                double value = Double.parseDouble(valueField.getText().trim());
                String categorySelection = (String) categoryCombo.getSelectedItem();
                String category = categorySelection.substring(0, 1);
                int qty = Integer.parseInt(qtyField.getText().trim());

                InventoryItem updatedItem = new InventoryItem(newName, value, category, qty);
                updatedItem.description = desc;
                inventoryManager.updateItem(originalName, updatedItem);
                inventoryManager.saveInventory();
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
                inventoryManager.deleteItem(selectedName);
                inventoryManager.saveInventory();
                populateTable();
                
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().refreshAllScreens();
                }
            }
        }
    }
}