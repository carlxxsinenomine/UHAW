package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import main.MainActivity;
import main.AppConstants;

/**
 * UserScreen class with Search enabled and Top-Bar Layout.
 */
public class UserScreen extends JPanel {

    // Data structures - optimized to reduce memory
    private final Map<String, InventoryItem> inventory; // Single map instead of three
    private final Set<String> selectedCategories;
    private JPanel itemsPanel;
    private JLabel overallTotalLabel;
    private JLabel totalItemsLabel;
    private final java.util.List<ItemRowData> itemRows; // Replace two maps with single list
    
    private JTextField nameInput;
    private JTextField contactInput;
    private JTextField addressInput;

    // Stores current search text
    private String currentSearchText = "";
    private NavBarPanel navBarPanel;
    
    // Category constants
    private static final String[] CATEGORY_IDS = {"1", "2", "3"};
    private static final String[] CATEGORY_NAMES = {"Tools", "Building Materials", "Paint & Supplies"};
    
    // Inner class to hold inventory data efficiently
    private static class InventoryItem {
        final String name;
        double price;
        String category;
        int quantity;
        
        InventoryItem(String name, double price, String category, int quantity) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.quantity = quantity;
        }
    }
    
    // Inner class to track row components efficiently
    private static class ItemRowData {
        final String itemName;
        final JSpinner spinner;
        final JLabel totalLabel;
        
        ItemRowData(String itemName, JSpinner spinner, JLabel totalLabel) {
            this.itemName = itemName;
            this.spinner = spinner;
            this.totalLabel = totalLabel;
        }
    }

    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize optimized data structures
        inventory = new HashMap<>();
        selectedCategories = new HashSet<>();
        itemRows = new ArrayList<>();

        // Load Data and select all categories by default
        loadInventoryData();
        selectedCategories.addAll(Arrays.asList(CATEGORY_IDS));

        // 3. Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 4. Navigation Bar with Search
        navBarPanel = new NavBarPanel("USER");
        navBarPanel.setSearchListener(text -> {
            // Check if text is placeholder or actual search
            String searchText = text.trim();

            // If text is placeholder or empty, clear search
            if (searchText.isEmpty() || 
                searchText.equals("Search") || 
                searchText.equals("Search (YYYY-MM-DD)")) {
                this.currentSearchText = "";
            } else {
                this.currentSearchText = searchText.toLowerCase();
            }

            refreshTableRows();
        });

        // 5. Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JLabel titleLabel = new JLabel("Create Purchase");
        titleLabel.setFont(AppConstants.FONT_TITLE_XL);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 6. Main Content Panels
        JPanel invoiceTablePanel = getInvoiceTablePanel();
        JPanel bottomPanel = getBottomPanel();

        // 7. Content panel assembly
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(invoiceTablePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 8. Assemble main container
        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    public void refreshData() {
        loadInventoryData();
        resetSearch(); // Clear search when refreshing
        refreshTableRows();
    }

    // NEW METHOD: Reset search when screen is shown again
    public void resetSearch() {
        currentSearchText = "";
        if (navBarPanel != null) {
            navBarPanel.resetSearch(); // FIXED: Changed from clearSearchField() to resetSearch()
        }
    }

    private void loadInventoryData() {
        try {
            File file = null;
            String[] possiblePaths = { "src/items/inventory.json", "items/inventory.json", "../items/inventory.json" };
            for (String path : possiblePaths) {
                File testFile = new File(path);
                if (testFile.exists()) { file = testFile; break; }
            }
            if (file != null && file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder content = new StringBuilder();
                    while (scanner.hasNextLine()) content.append(scanner.nextLine());
                    parseInventoryJson(content.toString());
                }
            } else {
                loadSampleData();
            }
        } catch (Exception e) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        inventory.put("Product A", new InventoryItem("Product A", 150.00, "1", 10));
        inventory.put("Product B", new InventoryItem("Product B", 250.00, "2", 5));
        inventory.put("Product C", new InventoryItem("Product C", 75.00, "3", 15));
    }

    private void parseInventoryJson(String jsonContent) {
        try {
            // Clear existing data before loading
            inventory.clear();
            
            String jsonStr = jsonContent.replaceAll("\\s+", " ");
            int arrayStart = jsonStr.indexOf('[');
            int arrayEnd = jsonStr.lastIndexOf(']');
            if (arrayStart != -1 && arrayEnd != -1) {
                jsonStr = jsonStr.substring(arrayStart + 1, arrayEnd);
                String[] items = jsonStr.split("\\},\\s*\\{");
                for (String item : items) {
                    item = item.trim().replace("{", "").replace("}", "");
                    String itemName = extractJsonValue(item, "itemName");
                    String valueStr = extractJsonValue(item, "value");
                    String category = extractJsonValue(item, "category");
                    String qtyStr = extractJsonValue(item, "quantity");

                    if (itemName != null && valueStr != null && category != null) {
                        try {
                            double value = Double.parseDouble(valueStr.replaceAll(",.*", "").trim());
                            int qty = (qtyStr != null) ? Integer.parseInt(qtyStr.trim()) : 0;
                            inventory.put(itemName, new InventoryItem(itemName, value, category, qty));
                        } catch (Exception e) {
                            System.err.println("Error parsing item: " + itemName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
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

    private JPanel getCustomerInfoPanel() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        container.setOpaque(false);

        JPanel p1 = new JPanel(new BorderLayout(5, 0));
        p1.setOpaque(false);
        JLabel nameLbl = new JLabel("Name:");
        nameLbl.setFont(AppConstants.FONT_LABEL_BOLD);
        nameLbl.setForeground(Color.BLACK);
        p1.add(nameLbl, BorderLayout.WEST);
        nameInput = getInputField();
        p1.add(nameInput, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout(5, 0));
        p2.setOpaque(false);
        JLabel contactLbl = new JLabel("Contact No.");
        contactLbl.setFont(AppConstants.FONT_LABEL_BOLD);
        contactLbl.setForeground(Color.BLACK);
        p2.add(contactLbl, BorderLayout.WEST);

        // Create numeric-only contact field
        contactInput = getInputField();
        contactInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Allow only digits, backspace, delete, and navigation keys
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume(); // Ignore this key event
                }
            }
        });
        p2.add(contactInput, BorderLayout.CENTER);

        JPanel p3 = new JPanel(new BorderLayout(5, 0));
        p3.setOpaque(false);
        JLabel addressLbl = new JLabel("Address:");
        addressLbl.setFont(AppConstants.FONT_LABEL_BOLD);
        addressLbl.setForeground(Color.BLACK);
        p3.add(addressLbl, BorderLayout.WEST);
        addressInput = getInputField();
        p3.add(addressInput, BorderLayout.CENTER);

        container.add(p1);
        container.add(p2);
        container.add(p3);
        return container;
    }

    private JPanel getInvoiceTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.setOpaque(false);
        topControlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel categoryPanel = createCategoryTogglePanel();
        JPanel infoPanel = getCustomerInfoPanel();

        topControlPanel.add(categoryPanel, BorderLayout.WEST);
        topControlPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        headerPanel.setBackground(AppConstants.BG_MEDIUM_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] headers = {"Item Name", "Qty", "Value", "Total"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(AppConstants.FONT_LABEL_BOLD);
            headerLabel.setForeground(Color.BLACK);
            headerPanel.add(headerLabel);
        }

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        refreshTableRows();

        JPanel scrollContent = new JPanel(new BorderLayout());
        scrollContent.setOpaque(false);
        scrollContent.add(itemsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_LIGHT_GRAY, 1));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 350));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(headerPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        tablePanel.add(topControlPanel, BorderLayout.NORTH);
        tablePanel.add(content, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createCategoryTogglePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);
        JLabel catLabel = new JLabel("Categories:");
        catLabel.setFont(AppConstants.FONT_LABEL_BOLD);
        catLabel.setForeground(Color.BLACK);
        panel.add(catLabel);

        for (int i = 0; i < CATEGORY_IDS.length; i++) {
            final String catId = CATEGORY_IDS[i];
            final String catName = CATEGORY_NAMES[i];
            
            JToggleButton btn = new JToggleButton(catName);
            btn.setSelected(true);
            btn.setFont(AppConstants.FONT_LABEL_REGULAR);
            btn.setPreferredSize(new Dimension(150, 32));
            btn.setBackground(AppConstants.PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(AppConstants.DARK_PRIMARY_BLUE, 2));
            btn.addActionListener(e -> {
                if (btn.isSelected()) {
                    selectedCategories.add(catId);
                    btn.setBackground(AppConstants.PRIMARY_BLUE);
                    btn.setForeground(Color.WHITE);
                } else {
                    selectedCategories.remove(catId);
                    btn.setBackground(AppConstants.BG_GRAY);
                    btn.setForeground(Color.GRAY);
                }
                refreshTableRows();
            });
            panel.add(btn);
        }
        return panel;
    }

    private void refreshTableRows() {
        // Save current quantities before clearing
        Map<String, Integer> savedQuantities = new HashMap<>();
        for (ItemRowData row : itemRows) {
            savedQuantities.put(row.itemName, (Integer) row.spinner.getValue());
        }
        
        // Clear old components and references
        itemsPanel.removeAll();
        itemRows.clear();
        
        // Get sorted list of items
        java.util.List<String> sorted = new ArrayList<>(inventory.keySet());
        Collections.sort(sorted);

        int visibleItems = 0;
        
        for (String itemName : sorted) {
            InventoryItem item = inventory.get(itemName);
            
            // Check Category
            boolean matchCat = selectedCategories.contains(item.category);

            // Check Search - only apply if currentSearchText is not empty
            boolean matchSearch = currentSearchText.isEmpty() || 
                                 itemName.toLowerCase().contains(currentSearchText);

            if (matchCat && matchSearch) {
                // Get saved quantity for this item (default to 0)
                Integer savedQty = savedQuantities.getOrDefault(itemName, 0);
                itemsPanel.add(createItemRow(item, savedQty));
                visibleItems++;
            }
        }

        // Add a message if no items match the filter
        if (visibleItems == 0) {
            StringBuilder msg = new StringBuilder("No items found");
            if (!currentSearchText.isEmpty()) {
                msg.append(" for search: '").append(currentSearchText).append("'");
            }

            JLabel noItemsLabel = new JLabel(msg.toString(), SwingConstants.CENTER);
            noItemsLabel.setFont(AppConstants.FONT_BODY_ITALIC);
            noItemsLabel.setForeground(Color.GRAY);
            noItemsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            itemsPanel.add(noItemsLabel);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
        updateOverallTotals();
    }

    private JPanel createItemRow(InventoryItem item) {
        return createItemRow(item, 0); // Default quantity is 0
    }

    private JPanel createItemRow(InventoryItem item, int initialQuantity) {
        JPanel row = new JPanel(new GridLayout(1, 4, 5, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLbl = getStyledLabel(item.name); 
        nameLbl.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel totalLbl = getStyledLabel("0.00");
        JLabel valLbl = getStyledLabel(String.format("%.2f", item.price));

        JComponent qtyComp;
        if (item.quantity > 0) {
            // Ensure initial quantity doesn't exceed available stock
            int qty = Math.min(initialQuantity, item.quantity);
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(qty, 0, item.quantity, 1));
            spinner.setFont(AppConstants.FONT_BODY_SMALL);
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor)editor).getTextField().setForeground(Color.BLACK);
            }

            // Store row data efficiently
            itemRows.add(new ItemRowData(item.name, spinner, totalLbl));
            
            // Update total label with initial quantity
            updateTotalFromLabel(spinner, item.price, totalLbl);
            
            spinner.addChangeListener(e -> {
                updateTotalFromLabel(spinner, item.price, totalLbl);
                updateOverallTotals();
            });
            qtyComp = spinner;
        } else {
            JLabel out = new JLabel("Out of Stock");
            out.setForeground(AppConstants.ACCENT_RED);
            out.setFont(AppConstants.FONT_LABEL_BOLD_SMALL);
            out.setHorizontalAlignment(SwingConstants.CENTER);
            qtyComp = out;
            totalLbl.setText("-");
        }

        row.add(nameLbl); 
        row.add(qtyComp); 
        row.add(valLbl); 
        row.add(totalLbl);
        return row;
    }

    private void updateTotalFromLabel(JSpinner spinner, double price, JLabel totalLabel) {
        try {
            int qty = (Integer) spinner.getValue();
            totalLabel.setText(String.format("%.2f", qty * price));
        } catch (Exception e) { 
            totalLabel.setText("0.00"); 
        }
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel totals = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        totals.setOpaque(false);
        JLabel l1 = new JLabel("Overall Total:"); l1.setForeground(Color.BLACK); totals.add(l1);
        overallTotalLabel = getStyledLabel("0.00"); totals.add(overallTotalLabel);

        JLabel l2 = new JLabel("Total Items:"); l2.setForeground(Color.BLACK); totals.add(l2);
        totalItemsLabel = getStyledLabel("0"); totals.add(totalItemsLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.setOpaque(false);

        JButton checkoutButton = createActionButton("Checkout", AppConstants.ACCENT_GREEN);
        checkoutButton.addActionListener(e -> generateInvoice());

        // Clear Cart button - resets all quantities to 0
        JButton clearCartButton = createActionButton("Clear Cart", AppConstants.ACCENT_RED);
        clearCartButton.addActionListener(e -> {
            // Reset all spinner values to 0
            for (ItemRowData row : itemRows) {
                row.spinner.setValue(0);
            }
            updateOverallTotals();
            JOptionPane.showMessageDialog(this, 
                "Cart cleared successfully!", 
                "Cart Cleared", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.add(clearCartButton);
        buttons.add(checkoutButton);

        bottomPanel.add(totals, BorderLayout.WEST);
        bottomPanel.add(buttons, BorderLayout.EAST);
        return bottomPanel;
    }

    private void updateOverallTotals() {
        if (overallTotalLabel == null) return;
        double total = 0; 
        int count = 0;
        
        for (ItemRowData row : itemRows) {
            int qty = (Integer) row.spinner.getValue();
            if (qty > 0) {
                InventoryItem item = inventory.get(row.itemName);
                if (item != null) {
                    total += qty * item.price;
                    count += qty;
                }
            }
        }
        
        overallTotalLabel.setText(String.format("%.2f", total));
        totalItemsLabel.setText(String.valueOf(count));
    }

    private JButton createActionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(AppConstants.FONT_LABEL_BOLD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JTextField getInputField() {
        JTextField f = new JTextField("", 15);
        f.setFont(AppConstants.FONT_BODY_REGULAR);
        f.setForeground(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(15, AppConstants.BORDER_LIGHT_GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private static JLabel getStyledLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(AppConstants.FONT_BODY_SMALL);
        l.setForeground(Color.BLACK);
        l.setBackground(new Color(245, 245, 245));
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12, new Color(180, 180, 180)), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        l.setPreferredSize(new Dimension(100, 30));
        return l;
    }

    private void generateInvoice() {
        String name = nameInput.getText().trim();
        String contact = contactInput.getText().trim();
        String addr = addressInput.getText().trim();

        // Check if name is empty or set to "unknown" (case insensitive)
        if (name.isEmpty() || name.equalsIgnoreCase("unknown")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid customer name.", 
                "Invalid Name", 
                JOptionPane.WARNING_MESSAGE);
            nameInput.requestFocus();
            return;
        }

        // Check if contact contains only digits and is not empty
        if (contact.isEmpty() || !contact.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid contact number (digits only).", 
                "Invalid Contact", 
                JOptionPane.WARNING_MESSAGE);
            contactInput.requestFocus();
            return;
        }

        if (addr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter address.", 
                "Missing Address", 
                JOptionPane.WARNING_MESSAGE);
            addressInput.requestFocus();
            return;
        }

        // Validate that at least one item is selected
        java.util.List<InvoiceItem> items = new ArrayList<>();
        Map<String, Integer> decrements = new HashMap<>();
        double total = 0;
        int totalItems = 0;

        for (ItemRowData row : itemRows) {
            int qty = (Integer) row.spinner.getValue();
            if (qty > 0) {
                InventoryItem item = inventory.get(row.itemName);
                if (item != null) {
                    // Check if we have enough stock
                    if (qty > item.quantity) {
                        JOptionPane.showMessageDialog(this,
                            "Insufficient stock for: " + item.name + 
                            "\nAvailable: " + item.quantity + 
                            "\nRequested: " + qty,
                            "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    double itemTotal = qty * item.price;
                    items.add(new InvoiceItem(item.name, qty, item.price, itemTotal));
                    decrements.put(item.name, qty);
                    total += itemTotal;
                    totalItems += qty;
                }
            }
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one item.", 
                "Empty Cart", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Generate invoice ID - ensure it's not blank
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String invoiceId = "INV" + timestamp;

        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Error generating invoice ID.",
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Generating invoice with ID: " + invoiceId);

        // Update inventory first
        if (updateInventoryFile(decrements)) {
            // Write invoice file
            boolean invoiceWritten = writeInvoiceToFile(invoiceId, name, contact, addr, items, total);

            if (invoiceWritten) {
                // Show success message with invoice details
                String message = String.format(
                    "Invoice Generated Successfully!\n\n" +
                    "Invoice ID: %s\n" +
                    "Customer: %s\n" +
                    "Total Items: %d\n" +
                    "Total Amount: PHP %,.2f\n\n" +
                    "Invoice saved to: invoices/%s.txt",
                    invoiceId, name, totalItems, total, invoiceId);
                
                JOptionPane.showMessageDialog(this, 
                    message, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form
                nameInput.setText(""); 
                contactInput.setText(""); 
                addressInput.setText("");
                
                // Reset all spinners to 0
                for (ItemRowData row : itemRows) {
                    row.spinner.setValue(0);
                }

                // Clear search and refresh
                resetSearch();
                refreshTableRows();

                // Reload inventory data to reflect changes
                loadInventoryData();

                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().refreshAllScreens();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save invoice file.\n" +
                    "Please check if the 'invoices' directory exists and is writable.", 
                    "File Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update inventory.\n" +
                "Invoice generation cancelled.", 
                "Inventory Error", 
                JOptionPane.ERROR_MESSAGE);
            }
    }


    private boolean updateInventoryFile(Map<String, Integer> decrements) {
        try {
            File f = new File("src/items/inventory.json");
            if (!f.exists()) f = new File("items/inventory.json");
            if (!f.exists()) f = new File("../items/inventory.json");
            if (!f.exists()) {
                System.err.println("Inventory file not found!");
                return false;
            }

            java.util.List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String l; while ((l = br.readLine()) != null) lines.add(l);
            }

            for (Map.Entry<String, Integer> e : decrements.entrySet()) {
                String target = e.getKey(); 
                int sub = e.getValue();
                boolean found = false;
                
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("\"itemName\": \"" + target + "\"")) {
                        System.out.println("Found item: " + target + " at line " + (i+1));
                        for (int j = i; j < Math.min(i + 10, lines.size()); j++) {
                            if (lines.get(j).contains("\"quantity\":")) {
                                String qLine = lines.get(j);
                                System.out.println("Original quantity line: " + qLine);
                                
                                // Extract the number more carefully
                                String[] parts = qLine.split(":");
                                if (parts.length >= 2) {
                                    String numPart = parts[1].trim().replaceAll("[^0-9]", "");
                                    if (numPart.isEmpty()) {
                                        System.err.println("Could not extract number from: " + qLine);
                                        return false;
                                    }
                                    int cur = Integer.parseInt(numPart);
                                    System.out.println("Current quantity: " + cur + ", Subtracting: " + sub);
                                    
                                    String comma = qLine.trim().endsWith(",") ? "," : "";
                                    String newLine = "    \"quantity\": " + Math.max(0, cur - sub) + comma;
                                    System.out.println("New quantity line: " + newLine);
                                    
                                    lines.set(j, newLine);
                                    found = true;
                                }
                                break;
                            }
                            if (lines.get(j).contains("}")) break;
                        }
                        break;
                    }
                }
                
                if (!found) {
                    System.err.println("Item not found in inventory: " + target);
                    return false;
                }
            }
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) { 
                for (String l : lines) pw.println(l); 
            }
            System.out.println("Inventory file updated successfully!");
            return true;
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            return false; 
        }
    }

    private boolean writeInvoiceToFile(String invoiceId, String n, String c, String a, java.util.List<InvoiceItem> items, double t) {
        try {
            // Try multiple possible paths in order
            String[] possiblePaths = {
                "src/main/invoices",  // First try the exact path from error
                "src/invoices",
                "invoices",
                "../invoices",
                "./invoices"
            };

            File d = null;
            for (String path : possiblePaths) {
                File testDir = new File(path);
                if (testDir.exists() && testDir.isDirectory()) {
                    d = testDir;
                    System.out.println("Found invoices directory: " + d.getAbsolutePath());
                    break;
                }
            }

            // Create directory if not found
            if (d == null) {
                d = new File("invoices");
                if (!d.mkdirs()) {
                    // Try alternative location
                    d = new File("src/main/invoices");
                    if (!d.mkdirs()) {
                        System.err.println("Failed to create invoices directory at any location");
                        return false;
                    }
                }
                System.out.println("Created invoices directory: " + d.getAbsolutePath());
            }

            // Create the invoice file
            File f = new File(d, invoiceId + ".txt");
            System.out.println("Writing invoice to: " + f.getAbsolutePath());

            // Use StringBuilder for efficient string building
            StringBuilder content = new StringBuilder(2000);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Header
            content.append("=".repeat(80)).append("\n\n");
            content.append("                           HARDWARE STORE INVOICE\n\n");
            content.append("=".repeat(80)).append("\n\n");

            // Invoice details
            content.append("  Invoice Number: ").append(invoiceId).append("\n");
            content.append("  Date: ").append(date).append("\n\n");

            // Customer information
            content.append("  BILL TO:\n");
            content.append("  Name: ").append(n).append("\n");
            content.append("  Contact No.: ").append(c).append("\n");
            content.append("  Address: ").append(a).append("\n\n");

            content.append("  ").append("-".repeat(76)).append("\n\n");

            // Table header
            content.append(String.format("  %-40s %6s %18s %18s%n", 
                "DESCRIPTION", "QTY", "UNIT PRICE", "AMOUNT"));
            content.append("\n");

            // Items
            for (InvoiceItem i : items) {
                String desc = i.description.length() > 38 ? 
                             i.description.substring(0, 35) + "..." : i.description;
                content.append(String.format("  %-40s %6d %18s %18s%n", 
                    desc, 
                    i.qty, 
                    String.format("PHP %,.2f", i.unitPrice),
                    String.format("PHP %,.2f", i.amount)));
            }

            content.append("\n").append("  ").append("-".repeat(76)).append("\n\n");

            // Total
            content.append(String.format("  %-66s %18s%n", 
                "TOTAL AMOUNT DUE:", 
                String.format("PHP %,.2f", t)));

            content.append("\n").append("=".repeat(80)).append("\n");
            content.append("  Thank you for your business!\n");
            content.append("\n").append("=".repeat(80));

            // Write to file
            try (PrintWriter w = new PrintWriter(new FileWriter(f))) {
                w.print(content.toString());
                w.flush();
            }

            System.out.println("Invoice saved successfully!");
            System.out.println("Full path: " + f.getAbsolutePath());
            System.out.println("File exists: " + f.exists());
            System.out.println("File size: " + f.length() + " bytes");

            return true;

        } catch (Exception e) {
            System.err.println("Error writing invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static class InvoiceItem { 
        String description; 
        int qty; 
        double unitPrice, amount; 
        InvoiceItem(String d, int q, double p, double a){
            this.description=d;
            this.qty=q;
            this.unitPrice=p;
            this.amount=a;
        }
    }
    
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        int r; 
        Color c; 
        RoundedBorder(int r, Color c){
            this.r=r;
            this.c=c;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
            Graphics2D g2=(Graphics2D)g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.c); 
            g2.drawRoundRect(x,y,w-1,h-1,r,r); 
            g2.dispose();
        }
        public Insets getBorderInsets(Component c){
            return new Insets(2,2,2,2);
        }
        public Insets getBorderInsets(Component c, Insets i){
            i.left=i.right=i.bottom=i.top=2;
            return i;
        }
    }
}