package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import main.MainActivity;

/**
 * UserScreen class represents the main invoice generation interface.
 * Implements Quantity Tracking: Decrements inventory on checkout and shows Out of Stock.
 *
 * @author Your Name
 * @version 2.3
 */
public class UserScreen extends JPanel {

    private Map<String, Double> itemValueMap;
    private Map<String, String> itemCategoryMap;
    // New Map to track available quantities
    private Map<String, Integer> itemQuantityMap;
    private Set<String> selectedCategories;
    private JPanel itemsPanel;
    private JLabel overallTotalLabel;
    private JLabel totalItemsLabel;
    private java.util.List<JSpinner> quantitySpinners;
    // Map to link spinners back to item names for checkout logic
    private Map<JSpinner, String> spinnerToItemMap;

    private JTextField nameInput;
    private JTextField contactInput;
    private JTextField addressInput;

    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Initialize Data Structures
        itemValueMap = new HashMap<>();
        itemCategoryMap = new HashMap<>();
        itemQuantityMap = new HashMap<>();
        selectedCategories = new HashSet<>();
        quantitySpinners = new java.util.ArrayList<>();
        spinnerToItemMap = new HashMap<>();

        // 2. Load Data
        loadInventoryData();
        selectedCategories.addAll(Arrays.asList("1", "2", "3"));

        // 3. Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 4. Navigation Bar
        JPanel navBarPanel = new NavBarPanel("USER");

        // 5. Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JLabel titleLabel = new JLabel("Create Purchase");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 6. Main Content Panels
        JPanel informationInputContainer = getCustomerInfoPanel();
        JPanel invoiceTablePanel = getInvoiceTablePanel();
        JPanel bottomPanel = getBottomPanel();

        // 7. Content panel assembly
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(informationInputContainer, BorderLayout.BEFORE_FIRST_LINE);
        contentPanel.add(invoiceTablePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 8. Assemble main container
        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    /**
     * Loads inventory data from inventory.json file.
     */
    private void loadInventoryData() {
        try {
            File file = null;
            String[] possiblePaths = {
                    "src/items/inventory.json",
                    "items/inventory.json",
                    "../items/inventory.json",
                    "./src/items/inventory.json"
            };
            for (String path : possiblePaths) {
                File testFile = new File(path);
                if (testFile.exists()) {
                    file = testFile;
                    break;
                }
            }
            if (file != null && file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder content = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        content.append(scanner.nextLine());
                    }
                    parseInventoryJson(content.toString());
                }
            } else {
                loadSampleData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadSampleData();
        }
    }

    private void loadSampleData() {
        itemValueMap.put("Product A", 150.00); itemCategoryMap.put("Product A", "1"); itemQuantityMap.put("Product A", 10);
        itemValueMap.put("Product B", 75.50); itemCategoryMap.put("Product B", "1"); itemQuantityMap.put("Product B", 5);
        // ... add other sample data ...
    }

    private void parseInventoryJson(String jsonContent) {
        try {
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
                    String qtyStr = extractJsonValue(item, "quantity"); // Parse Quantity

                    if (itemName != null && valueStr != null && category != null) {
                        try {
                            double value = Double.parseDouble(valueStr.replaceAll(",.*", "").trim());
                            itemValueMap.put(itemName, value);
                            itemCategoryMap.put(itemName, category);

                            // Parse Quantity (default to 0 if missing)
                            int qty = (qtyStr != null) ? Integer.parseInt(qtyStr.trim()) : 0;
                            itemQuantityMap.put(itemName, qty);
                        } catch (Exception e) {}
                    }
                }
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

    private JPanel getCustomerInfoPanel() {
        JPanel informationInputContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        informationInputContainer.setOpaque(false);

        JPanel nameContainer = new JPanel(new BorderLayout(5, 0));
        nameContainer.setOpaque(false);
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameInput = getInputField();
        nameContainer.add(nameLabel, BorderLayout.WEST);
        nameContainer.add(nameInput, BorderLayout.CENTER);

        JPanel contactContainer = new JPanel(new BorderLayout(5, 0));
        contactContainer.setOpaque(false);
        JLabel contactLabel = new JLabel("Contact No.:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactInput = getInputField();
        contactContainer.add(contactLabel, BorderLayout.WEST);
        contactContainer.add(contactInput, BorderLayout.CENTER);

        JPanel addressContainer = new JPanel(new BorderLayout(5, 0));
        addressContainer.setOpaque(false);
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressInput = getInputField();
        addressContainer.add(addressLabel, BorderLayout.WEST);
        addressContainer.add(addressInput, BorderLayout.CENTER);

        informationInputContainer.add(nameContainer);
        informationInputContainer.add(contactContainer);
        informationInputContainer.add(addressContainer);
        return informationInputContainer;
    }

    private JPanel getInvoiceTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel categoryPanel = createCategoryTogglePanel();

        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        headerPanel.setBackground(new Color(200, 200, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] headers = {"Item Name", "Qty", "Value", "Total"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerPanel.add(headerLabel);
        }

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        refreshTableRows();

        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.setOpaque(false);
        scrollableContent.add(itemsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 350));

        JPanel tableContentPanel = new JPanel(new BorderLayout());
        tableContentPanel.setOpaque(false);
        tableContentPanel.add(headerPanel, BorderLayout.NORTH);
        tableContentPanel.add(scrollPane, BorderLayout.CENTER);

        tablePanel.add(categoryPanel, BorderLayout.NORTH);
        tablePanel.add(tableContentPanel, BorderLayout.CENTER);

        return tablePanel;
    }

    private int getFilteredItemCount() {
        int count = 0;
        for (String itemName : itemValueMap.keySet()) {
            if (selectedCategories.contains(itemCategoryMap.get(itemName))) count++;
        }
        return count;
    }

    private JPanel createCategoryTogglePanel() {
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel categoryLabel = new JLabel("Categories:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        categoryPanel.add(categoryLabel);
        for (String category : Arrays.asList("1", "2", "3")) {
            JToggleButton categoryBtn = new JToggleButton(category);
            categoryBtn.setSelected(true);
            categoryBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            categoryBtn.setPreferredSize(new Dimension(50, 30));
            categoryBtn.setBackground(new Color(130, 170, 255));
            categoryBtn.setForeground(Color.WHITE);
            categoryBtn.setFocusPainted(false);
            categoryBtn.addActionListener(e -> {
                if (categoryBtn.isSelected()) {
                    selectedCategories.add(category);
                    categoryBtn.setBackground(new Color(130, 170, 255));
                    categoryBtn.setForeground(Color.WHITE);
                } else {
                    selectedCategories.remove(category);
                    categoryBtn.setBackground(new Color(200, 200, 200));
                    categoryBtn.setForeground(Color.BLACK);
                }
                refreshTableRows();
            });
            categoryPanel.add(categoryBtn);
        }
        return categoryPanel;
    }

    private void refreshTableRows() {
        itemsPanel.removeAll();
        quantitySpinners.clear();
        spinnerToItemMap.clear();

        // Sort items for consistent display
        java.util.List<String> sortedItems = new java.util.ArrayList<>(itemValueMap.keySet());
        Collections.sort(sortedItems);

        for (String itemName : sortedItems) {
            if (selectedCategories.contains(itemCategoryMap.get(itemName))) {
                itemsPanel.add(createItemRow(itemName));
            }
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
        updateOverallTotals();
    }

    private JPanel createItemRow(String itemName) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        double itemValue = itemValueMap.get(itemName);
        // Get Quantity from map, default to 0
        int availableQty = itemQuantityMap.getOrDefault(itemName, 0);

        JLabel itemNameLabel = getStyledLabel(itemName);
        itemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // --- QUANTITY LOGIC START ---
        JComponent qtyComponent;
        JLabel totalLabel = getStyledLabel("0.00");
        JLabel valueLabel = getStyledLabel(String.format("%.2f", itemValue));

        if (availableQty > 0) {
            // If stock available, show spinner with max value = availableQty
            JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, availableQty, 1));
            qtySpinner.setFont(new Font("Arial", Font.PLAIN, 13));
            quantitySpinners.add(qtySpinner);
            spinnerToItemMap.put(qtySpinner, itemName);

            qtySpinner.addChangeListener(e -> {
                updateTotalFromLabel(qtySpinner, valueLabel, totalLabel);
                updateOverallTotals();
            });
            qtyComponent = qtySpinner;
        } else {
            // If 0 stock, show "Out of Stock" label in red
            JLabel outOfStockLbl = new JLabel("Out of Stock");
            outOfStockLbl.setForeground(Color.RED);
            outOfStockLbl.setFont(new Font("Arial", Font.BOLD, 12));
            outOfStockLbl.setHorizontalAlignment(SwingConstants.CENTER);
            qtyComponent = outOfStockLbl;
            totalLabel.setText("-");
        }
        // --- QUANTITY LOGIC END ---

        rowPanel.add(itemNameLabel);
        rowPanel.add(qtyComponent);
        rowPanel.add(valueLabel);
        rowPanel.add(totalLabel);
        return rowPanel;
    }

    private void updateTotalFromLabel(JSpinner qtySpinner, JLabel valueLabel, JLabel totalLabel) {
        try {
            int qty = (Integer) qtySpinner.getValue();
            double value = Double.parseDouble(valueLabel.getText().trim());
            totalLabel.setText(String.format("%.2f", qty * value));
        } catch (Exception e) {
            totalLabel.setText("0.00");
        }
    }

    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        totalsPanel.setOpaque(false);

        JPanel overallTotalContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        overallTotalContainer.setOpaque(false);
        overallTotalContainer.add(new JLabel("Overall Total:"));
        overallTotalLabel = getStyledLabel("0.00");
        overallTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        overallTotalContainer.add(overallTotalLabel);

        JPanel totalItemsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalItemsContainer.setOpaque(false);
        totalItemsContainer.add(new JLabel("Total Items:"));
        totalItemsLabel = getStyledLabel("0");
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalItemsContainer.add(totalItemsLabel);

        totalsPanel.add(overallTotalContainer);
        totalsPanel.add(totalItemsContainer);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonsPanel.setOpaque(false);

        JButton checkoutButton = createActionButton("Checkout", new Color(34, 139, 34));
        checkoutButton.addActionListener(e -> generateInvoice());

        buttonsPanel.add(checkoutButton);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(totalsPanel);
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(buttonsPanel);
        bottomPanel.add(wrapperPanel, BorderLayout.CENTER);
        return bottomPanel;
    }

    private void updateOverallTotals() {
        if (overallTotalLabel == null || totalItemsLabel == null) return;

        double overallTotal = 0.0;
        int totalItems = 0;

        for (JSpinner spinner : quantitySpinners) {
            try {
                int qty = (Integer) spinner.getValue();
                if (qty > 0) {
                    String itemName = spinnerToItemMap.get(spinner);
                    if (itemName != null && itemValueMap.containsKey(itemName)) {
                        overallTotal += qty * itemValueMap.get(itemName);
                        totalItems += qty;
                    }
                }
            } catch (Exception e) {}
        }

        overallTotalLabel.setText(String.format("%.2f", overallTotal));
        totalItemsLabel.setText(String.valueOf(totalItems));
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        return button;
    }

    private static JTextField getInputField() {
        JTextField inputField = new JTextField("", 15);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return inputField;
    }

    private static JLabel getStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBackground(new Color(245, 245, 245));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        label.setPreferredSize(new Dimension(100, 30));
        return label;
    }

    /**
     * Logic to generate invoice and decrement inventory.
     */
    private void generateInvoice() {
        String customerName = nameInput.getText().trim();
        String contactNo = contactInput.getText().trim();
        String address = addressInput.getText().trim();

        if (customerName.isEmpty() || contactNo.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer information.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Calculate Items to Buy & Decrements
        java.util.List<InvoiceItem> itemsToBuy = new ArrayList<>();
        Map<String, Integer> decrements = new HashMap<>();
        double subtotal = 0.0;
        int totalQty = 0;

        for (JSpinner spinner : quantitySpinners) {
            int qty = (Integer) spinner.getValue();
            if (qty > 0) {
                String itemName = spinnerToItemMap.get(spinner);
                double unitPrice = itemValueMap.get(itemName);
                double amount = qty * unitPrice;

                itemsToBuy.add(new InvoiceItem(itemName, qty, unitPrice, amount));
                decrements.put(itemName, qty);
                subtotal += amount;
                totalQty += qty;
            }
        }

        if (itemsToBuy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item.", "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Decrement Inventory in JSON
        if (updateInventoryFile(decrements)) {
            // 3. Generate Invoice File
            writeInvoiceToFile(customerName, contactNo, address, itemsToBuy, subtotal);

            // 4. Success & Refresh
            JOptionPane.showMessageDialog(this, "Invoice generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            nameInput.setText("");
            contactInput.setText("");
            addressInput.setText("");

            // Reload to reflect new quantities (Out of Stock items will now show label)
            loadInventoryData();
            refreshTableRows();

        } else {
            JOptionPane.showMessageDialog(this, "Error updating inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates inventory.json by subtracting purchased quantities.
     */
    private boolean updateInventoryFile(Map<String, Integer> decrements) {
        try {
            File file = new File("src/items/inventory.json");
            if (!file.exists()) file = new File("items/inventory.json");
            if (!file.exists()) return false;

            java.util.List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
            }

            for (Map.Entry<String, Integer> entry : decrements.entrySet()) {
                String targetItem = entry.getKey();
                int reduceBy = entry.getValue();

                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("\"itemName\": \"" + targetItem + "\"")) {
                        for (int j = i; j < lines.size(); j++) {
                            if (lines.get(j).contains("\"quantity\":")) {
                                String qLine = lines.get(j);
                                String[] parts = qLine.split(":");
                                int currentQty = Integer.parseInt(parts[1].trim().replace(",", ""));
                                int newQty = Math.max(0, currentQty - reduceBy);

                                String comma = qLine.trim().endsWith(",") ? "," : "";
                                lines.set(j, "    \"quantity\": " + newQty + comma);
                                break;
                            }
                            if (lines.get(j).trim().startsWith("}")) break;
                        }
                        break;
                    }
                }
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) pw.println(line);
            }
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private void writeInvoiceToFile(String name, String contact, String addr, java.util.List<InvoiceItem> items, double total) {
        try {
            SimpleDateFormat invoiceNumFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String invoiceNumber = "INV" + invoiceNumFormat.format(new Date());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            String dueDate = dateFormat.format(new Date());
            String subject = "Service for " + dateFormat.format(new Date());

            File invoicesDir = new File("invoices");
            if (!invoicesDir.exists()) invoicesDir.mkdirs();

            File invoiceFile = new File(invoicesDir, invoiceNumber + ".txt");
            PrintWriter writer = new PrintWriter(new FileWriter(invoiceFile));

            writer.println("=".repeat(80));
            writer.println();
            writer.println("  " + invoiceNumber);
            writer.println();
            writer.println("  Due date" + " ".repeat(36) + "Subject");
            writer.println("  " + dueDate + " ".repeat(30 - dueDate.length()) + subject);
            writer.println();
            writer.println("  Billed to" + " ".repeat(37) + "Currency");
            writer.println("  " + name + " ".repeat(Math.max(1, 30 - name.length())) + "PHP - Philippine Pesos");
            writer.println("  " + contact);
            writer.println("  " + addr);
            writer.println();
            writer.println("  " + "-".repeat(76));
            writer.println();
            writer.println("  DESCRIPTION" + " ".repeat(30) + "QTY" + " ".repeat(8) + "UNIT PRICE" + " ".repeat(8) + "AMOUNT");
            writer.println();

            for (InvoiceItem item : items) {
                String desc = item.description;
                if (desc.length() > 35) {
                    desc = desc.substring(0, 32) + "...";
                }
                writer.printf("  %-40s %3d %,15.3f PHP %,15.3f PHP%n", desc, item.qty, item.unitPrice, item.amount);
            }

            writer.println();
            writer.println(" ".repeat(55) + "Subtotal" + String.format("%,20.3f PHP", total));
            writer.println(" ".repeat(55) + "Total" + String.format("%,23.3f PHP", total));
            writer.println(" ".repeat(55) + "Amount due" + String.format("%,16.3f PHP", total));
            writer.println();
            writer.println("  " + "-".repeat(76));
            writer.println("=".repeat(80));
            writer.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static class InvoiceItem {
        String description;
        int qty;
        double unitPrice;
        double amount;
        InvoiceItem(String d, int q, double p, double a) { description = d; qty = q; unitPrice = p; amount = a; }
    }

    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color borderColor;
        RoundedBorder(int radius, Color borderColor) { this.radius = radius; this.borderColor = borderColor; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(2, 2, 2, 2); }
        @Override public Insets getBorderInsets(Component c, Insets insets) { insets.left = insets.top = insets.right = insets.bottom = 2; return insets; }
    }
}