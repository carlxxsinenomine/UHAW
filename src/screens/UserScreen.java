package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import main.AppConstants;
import main.MainActivity;
import models.InventoryItem;
import models.InventoryManager;
import models.InvoiceItem;
import models.ItemRowData;
import models.RoundedBorder;

// The customer purchase creation and checkout interface
// Primary screen for end-users to browse inventory, add items to cart, and generate purchase invoices
public class UserScreen extends JPanel {

    // InventoryManager handles all data operations
    private InventoryManager inventoryManager;
    private final Set<String> selectedCategories;
    private JPanel itemsPanel;
    private JLabel overallTotalLabel;
    private JLabel totalItemsLabel;
    private final java.util.List<ItemRowData> itemRows;
    
    private JTextField nameInput;
    private JTextField contactInput;
    private JTextField addressInput;

    // Stores current search text
    private String currentSearchText = "";
    private NavBarPanel navBarPanel;
    
    // Category constants
    private static final String[] CATEGORY_IDS = {"1", "2", "3"};
    private static final String[] CATEGORY_NAMES = {"Tools", "Building Materials", "Paint & Supplies"};

    // Constructor for UserScreen - initializes all UI components and loads inventory data
    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize InventoryManager and data structures
        inventoryManager = new InventoryManager();
        selectedCategories = new HashSet<>();
        itemRows = new ArrayList<>();

        // Load inventory data and select all categories by default
        inventoryManager.loadInventory();
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

    // Refreshes the screen data when user returns to this screen
    // Reloads inventory, clears search, and refreshes table display
    public void refreshData() {
        inventoryManager.loadInventory();
        resetSearch();
        refreshTableRows();
    }

    // Resets search to its initial state
    // Clears search text and internal search state variable
    public void resetSearch() {
        currentSearchText = "";
        if (navBarPanel != null) {
            navBarPanel.resetSearch();
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
            savedQuantities.put(row.getItemName(), (Integer) row.getSpinner().getValue());
        }
        
        // Clear old components and references
        itemsPanel.removeAll();
        itemRows.clear();
        
        // Get all inventory items from manager
        Map<String, InventoryItem> inventory = inventoryManager.getItemsAsMap();
        java.util.List<String> sorted = new ArrayList<>(inventory.keySet());
        Collections.sort(sorted);

        int visibleItems = 0;
        
        for (String itemName : sorted) {
            InventoryItem item = inventory.get(itemName);
            
            // Check Category
            boolean matchCat = selectedCategories.contains(item.getCategory());

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

    private JPanel createItemRow(InventoryItem item, int initialQuantity) {
        JPanel row = new JPanel(new GridLayout(1, 4, 5, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLbl = getStyledLabel(item.getName()); 
        nameLbl.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel totalLbl = getStyledLabel("0.00");
        JLabel valLbl = getStyledLabel(String.format("%.2f", item.getPrice()));

        JComponent qtyComp;
        if (item.getQuantity() > 0) {
            // Ensure initial quantity doesn't exceed available stock
            int qty = Math.min(initialQuantity, item.getQuantity());
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(qty, 0, item.getQuantity(), 1));
            spinner.setFont(AppConstants.FONT_BODY_SMALL);
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor)editor).getTextField().setForeground(Color.BLACK);
            }

            // Store row data efficiently
            itemRows.add(new ItemRowData(item.getName(), spinner, totalLbl));
            
            // Update total label with initial quantity
            updateTotalFromLabel(spinner, item.getPrice(), totalLbl);
            
            spinner.addChangeListener(e -> {
                updateTotalFromLabel(spinner, item.getPrice(), totalLbl);
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
                row.getSpinner().setValue(0);
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
            int qty = (Integer) row.getSpinner().getValue();
            if (qty > 0) {
                InventoryItem item = inventoryManager.getItemByName(row.getItemName());
                if (item != null) {
                    total += qty * item.getPrice();
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
            int qty = (Integer) row.getSpinner().getValue();
            if (qty > 0) {
                InventoryItem item = inventoryManager.getItemByName(row.getItemName());
                if (item != null) {
                    // Check if we have enough stock
                    if (qty > item.getQuantity()) {
                        JOptionPane.showMessageDialog(this,
                            "Insufficient stock for: " + item.getName() + 
                            "\nAvailable: " + item.getQuantity() + 
                            "\nRequested: " + qty,
                            "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    double itemTotal = qty * item.getPrice();
                    items.add(new InvoiceItem(item.getName(), qty, item.getPrice(), itemTotal));
                    decrements.put(item.getName(), qty);
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
                    row.getSpinner().setValue(0);
                }

                // Clear search and refresh
                resetSearch();
                refreshTableRows();

                // Reload inventory data to reflect changes
                refreshData();

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
                String desc = i.getDescription().length() > 38 ? 
                             i.getDescription().substring(0, 35) + "..." : i.getDescription();
                content.append(String.format("  %-40s %6d %18s %18s%n", 
                    desc, 
                    i.getQty(), 
                    String.format("PHP %,.2f", i.getUnitPrice()),
                    String.format("PHP %,.2f", i.getAmount())));
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
}