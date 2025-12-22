package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import main.MainActivity;

/**
 * UserScreen class represents the main invoice generation interface.
 * This screen allows users to create new invoices for Peter Loves Carl Co.
 * It includes customer information input fields, an invoice items table,
 * and action buttons to add items to invoice or summary.
 *
 * @author Your Name
 * @version 1.0
 */
public class UserScreen extends JPanel {

    private Map<String, Double> itemValueMap;
    private Map<String, String> itemCategoryMap;
    private Set<String> selectedCategories;
    private JPanel itemsPanel;

    /**
     * Constructor that initializes and displays the UserScreen.
     * Sets up the navigation bar, customer information fields,
     * invoice items table, and action buttons.
     */
    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        itemValueMap = new HashMap<>();
        itemCategoryMap = new HashMap<>();
        selectedCategories = new HashSet<>();

        loadInventoryData();

        // Initialize with all categories selected
        selectedCategories.addAll(Arrays.asList("1", "2", "3"));

        JPanel navBarPanel = new NavBarPanel("Home");

        JPanel navWrapper = new JPanel(new BorderLayout());
        navWrapper.setOpaque(false);
        navWrapper.add(navBarPanel, BorderLayout.CENTER);

        JLabel companyName = new JLabel("Generate New Invoice for Peter Loves Carl Co.");
        companyName.setFont(new Font("Arial", Font.BOLD, 32));
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        companyName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 20, 10, 20),
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(130, 170, 255))
        ));
        navWrapper.add(companyName, BorderLayout.SOUTH);

        // Main contents
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel informationInputContainer = getCustomerInfoPanel();
        JPanel invoiceTablePanel = getInvoiceTablePanel();
        JPanel bottomPanel = getBottomPanel();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(informationInputContainer, BorderLayout.NORTH);
        contentPanel.add(invoiceTablePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.WHITE);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.add(navWrapper, BorderLayout.NORTH);
        containerPanel.add(contentPanel, BorderLayout.CENTER);

        add(containerPanel);
    }

    /**
     * Loads inventory data from inventory.json file.
     * Tries multiple path locations to find the file.
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

            // Try each possible path
            for (String path : possiblePaths) {
                File testFile = new File(path);
                if (testFile.exists()) {
                    file = testFile;
                    System.out.println("Found inventory.json at: " + path);
                    break;
                }
            }

            if (file == null || !file.exists()) {
                System.err.println("Could not find inventory.json. Tried paths:");
                for (String path : possiblePaths) {
                    System.err.println("  - " + path);
                }
                // Load sample data for testing
                loadSampleData();
                return;
            }

            try (Scanner scanner = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine());
                }
                parseInventoryJson(content.toString());
                System.out.println("Successfully loaded " + itemValueMap.size() + " items from inventory.json");

                // Debug: Print all loaded items
                System.out.println("Items by category:");
                for (String cat : Arrays.asList("1", "2", "3")) {
                    System.out.println("  Category " + cat + ":");
                    for (String item : itemValueMap.keySet()) {
                        if (cat.equals(itemCategoryMap.get(item))) {
                            System.out.println("    - " + item + ": $" + itemValueMap.get(item));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading inventory data: " + e.getMessage());
            e.printStackTrace();
            // Load sample data as fallback
            loadSampleData();
        }
    }

    /**
     * Loads sample inventory data if the JSON file cannot be found.
     * This is useful for testing and development.
     */
    private void loadSampleData() {
        System.out.println("Loading sample inventory data...");

        // Add sample data matching your JSON structure
        itemValueMap.put("Product A", 150.00);
        itemCategoryMap.put("Product A", "1");

        itemValueMap.put("Product B", 75.50);
        itemCategoryMap.put("Product B", "1");

        itemValueMap.put("Service X", 250.00);
        itemCategoryMap.put("Service X", "2");

        itemValueMap.put("Product C", 45.99);
        itemCategoryMap.put("Product C", "2");

        itemValueMap.put("Service Y", 500.00);
        itemCategoryMap.put("Service Y", "3");

        itemValueMap.put("Product D", 199.99);
        itemCategoryMap.put("Product D", "3");

        itemValueMap.put("Product E", 29.50);
        itemCategoryMap.put("Product E", "1");

        System.out.println("Loaded " + itemValueMap.size() + " sample items");

        // Debug: Print all loaded items
        System.out.println("Items by category:");
        for (String cat : Arrays.asList("1", "2", "3")) {
            System.out.println("  Category " + cat + ":");
            for (String item : itemValueMap.keySet()) {
                if (cat.equals(itemCategoryMap.get(item))) {
                    System.out.println("    - " + item + ": $" + itemValueMap.get(item));
                }
            }
        }
    }


    /**
     * Parses the JSON content from inventory.json file.
     * Extracts itemName, value, and category for each item.
     *
     * @param jsonContent the JSON string to parse
     */
    private void parseInventoryJson(String jsonContent) {
        try {
            // Remove whitespace and newlines
            String jsonStr = jsonContent.replaceAll("\\s+", " ");

            // Extract the array content
            int arrayStart = jsonStr.indexOf('[');
            int arrayEnd = jsonStr.lastIndexOf(']');
            if (arrayStart == -1 || arrayEnd == -1) {
                System.err.println("Invalid JSON format: no array found");
                return;
            }

            jsonStr = jsonStr.substring(arrayStart + 1, arrayEnd);

            // Split by object boundaries - look for "},{"
            String[] items = jsonStr.split("\\},\\s*\\{");

            for (String item : items) {
                // Clean up the item string
                item = item.trim();
                if (item.startsWith("{")) {
                    item = item.substring(1);
                }
                if (item.endsWith("}")) {
                    item = item.substring(0, item.length() - 1);
                }

                if (item.isEmpty()) continue;

                try {
                    // Extract itemName
                    String itemName = extractJsonValue(item, "itemName");
                    if (itemName == null) continue;

                    // Extract value
                    String valueStr = extractJsonValue(item, "value");
                    if (valueStr == null) continue;
                    // Remove any comma at the end
                    valueStr = valueStr.replaceAll(",.*", "").trim();
                    double value = Double.parseDouble(valueStr);

                    // Extract category
                    String category = extractJsonValue(item, "category");
                    if (category == null) continue;

                    // Store in maps
                    itemValueMap.put(itemName, value);
                    itemCategoryMap.put(itemName, category);

                    System.out.println("Parsed item: " + itemName + " = $" + value + " (Category " + category + ")");

                } catch (Exception e) {
                    System.err.println("Error parsing item: " + item);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in parseInventoryJson: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extracts a value from a JSON string for a given key.
     * Handles both string and numeric values.
     *
     * @param jsonStr the JSON object string
     * @param key the key to extract
     * @return the value as a string, or null if not found
     */
    private String extractJsonValue(String jsonStr, String key) {
        try {
            // Find the key
            String searchStr = "\"" + key + "\"";
            int keyIndex = jsonStr.indexOf(searchStr);
            if (keyIndex == -1) {
                return null;
            }

            // Find the colon after the key
            int colonIndex = jsonStr.indexOf(":", keyIndex);
            if (colonIndex == -1) {
                return null;
            }

            // Skip whitespace after colon
            int valueStart = colonIndex + 1;
            while (valueStart < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(valueStart))) {
                valueStart++;
            }

            if (valueStart >= jsonStr.length()) {
                return null;
            }

            // Check if value is a string (starts with quote)
            if (jsonStr.charAt(valueStart) == '"') {
                // String value
                int valueEnd = jsonStr.indexOf('"', valueStart + 1);
                if (valueEnd == -1) {
                    return null;
                }
                return jsonStr.substring(valueStart + 1, valueEnd);
            } else {
                // Numeric or other value
                int valueEnd = valueStart;
                while (valueEnd < jsonStr.length() &&
                        jsonStr.charAt(valueEnd) != ',' &&
                        jsonStr.charAt(valueEnd) != '}') {
                    valueEnd++;
                }
                return jsonStr.substring(valueStart, valueEnd).trim();
            }
        } catch (Exception e) {
            System.err.println("Error extracting key '" + key + "': " + e.getMessage());
            return null;
        }
    }


    /**
     * Creates and returns the customer information input panel.
     * Contains three input fields: Name, Contact No., and Address.
     *
     * @return JPanel containing customer information input fields
     */
    private JPanel getCustomerInfoPanel() {
        JPanel informationInputContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        informationInputContainer.setOpaque(false);

        JPanel nameContainer = new JPanel(new BorderLayout(5, 0));
        nameContainer.setOpaque(false);
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField nameInput = getInputField();
        nameContainer.add(nameLabel, BorderLayout.WEST);
        nameContainer.add(nameInput, BorderLayout.CENTER);

        JPanel contactContainer = new JPanel(new BorderLayout(5, 0));
        contactContainer.setOpaque(false);
        JLabel contactLabel = new JLabel("Contact No.:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField contactInput = getInputField();
        contactContainer.add(contactLabel, BorderLayout.WEST);
        contactContainer.add(contactInput, BorderLayout.CENTER);

        JPanel addressContainer = new JPanel(new BorderLayout(5, 0));
        addressContainer.setOpaque(false);
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField addressInput = getInputField();
        addressContainer.add(addressLabel, BorderLayout.WEST);
        addressContainer.add(addressInput, BorderLayout.CENTER);

        informationInputContainer.add(nameContainer);
        informationInputContainer.add(contactContainer);
        informationInputContainer.add(addressContainer);
        return informationInputContainer;
    }

    /**
     * Creates and returns the invoice items table panel.
     * Contains a table with columns: Item Name, Qty, Value, and Total.
     * Includes category toggle buttons and dynamically generates rows based on available items.
     * The table is scrollable when there are many items.
     *
     * @return JPanel containing the invoice items table
     */
    private JPanel getInvoiceTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Category toggle panel
        JPanel categoryPanel = createCategoryTogglePanel();

        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        headerPanel.setBackground(new Color(200, 200, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] headers = {"Item Name", "Qty", "Value", "Total"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerPanel.add(headerLabel);
        }

        // Items panel with rows - dynamically created based on filtered items
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        // Count items in selected categories to determine number of rows
        int itemCount = getFilteredItemCount();

        for (int i = 1; i <= itemCount; i++) {
            itemsPanel.add(createItemRow(i));
        }

        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.setOpaque(false);
        scrollableContent.add(itemsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Set preferred height to show approximately 7 rows, then scroll
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 350));

        // Combine header and scroll pane
        JPanel tableContentPanel = new JPanel(new BorderLayout());
        tableContentPanel.setOpaque(false);
        tableContentPanel.add(headerPanel, BorderLayout.NORTH);
        tableContentPanel.add(scrollPane, BorderLayout.CENTER);

        tablePanel.add(categoryPanel, BorderLayout.NORTH);
        tablePanel.add(tableContentPanel, BorderLayout.CENTER);

        return tablePanel;
    }

    /**
     * Counts the number of items in the currently selected categories.
     *
     * @return the count of filtered items
     */
    private int getFilteredItemCount() {
        int count = 0;
        for (String itemName : itemValueMap.keySet()) {
            String category = itemCategoryMap.get(itemName);
            if (selectedCategories.contains(category)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Creates the category toggle panel with buttons for each category.
     *
     * @return JPanel containing category toggle buttons
     */
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

            String catId = category;
            categoryBtn.addActionListener(e -> {
                if (categoryBtn.isSelected()) {
                    selectedCategories.add(catId);
                    categoryBtn.setBackground(new Color(130, 170, 255));
                    categoryBtn.setForeground(Color.WHITE);
                } else {
                    selectedCategories.remove(catId);
                    categoryBtn.setBackground(new Color(200, 200, 200));
                    categoryBtn.setForeground(Color.BLACK);
                }
                refreshTableRows();
            });

            categoryPanel.add(categoryBtn);
        }

        return categoryPanel;
    }

    /**
     * Refreshes the table rows to show only items from selected categories.
     * Dynamically creates rows based on the number of items in selected categories.
     */
    private void refreshTableRows() {
        itemsPanel.removeAll();

        // Count items in selected categories
        int itemCount = getFilteredItemCount();

        // Create rows for each item
        for (int i = 1; i <= itemCount; i++) {
            itemsPanel.add(createItemRow(i));
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    /**
     * Creates and returns a single invoice item row.
     * Each row displays an item name label, quantity spinner,
     * value label (non-editable), and auto-calculated total label (non-editable).
     * Items are automatically assigned based on selected categories.
     *
     * @param rowIndex the row number (1-based)
     * @return JPanel representing a single item row
     */
    private JPanel createItemRow(int rowIndex) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Get sorted items from selected categories
        java.util.List<String> sortedItems = new java.util.ArrayList<>();
        for (String itemName : itemValueMap.keySet()) {
            String category = itemCategoryMap.get(itemName);
            if (selectedCategories.contains(category)) {
                sortedItems.add(itemName);
            }
        }
        Collections.sort(sortedItems);

        // Determine which item to display in this row
        String itemName = "";
        double itemValue = 0.0;

        if (rowIndex <= sortedItems.size()) {
            itemName = sortedItems.get(rowIndex - 1);
            itemValue = itemValueMap.get(itemName);
        }

        // Item name label (NON-EDITABLE)
        JLabel itemNameLabel = getStyledLabel(itemName);
        itemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        itemNameLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        // Qty spinner
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        qtySpinner.setFont(new Font("Arial", Font.PLAIN, 13));

        // Value label (NON-EDITABLE) - Styled to look like a field
        JLabel valueLabel = getStyledLabel(String.format("%.2f", itemValue));

        // Total label (NON-EDITABLE) - Styled to look like a field
        JLabel totalLabel = getStyledLabel("0.00");

        // Add listener to quantity spinner to update total
        qtySpinner.addChangeListener(e -> {
            updateTotalFromLabel(qtySpinner, valueLabel, totalLabel);
        });

        rowPanel.add(itemNameLabel);
        rowPanel.add(qtySpinner);
        rowPanel.add(valueLabel);
        rowPanel.add(totalLabel);

        return rowPanel;
    }

    /**
     * Updates the total label based on qty and value from label.
     * Total = Qty * Value
     */
    private void updateTotalFromLabel(JSpinner qtySpinner, JLabel valueLabel, JLabel totalLabel) {
        try {
            int qty = (Integer) qtySpinner.getValue();
            String valueText = valueLabel.getText().trim();

            double value = Double.parseDouble(valueText);
            double total = qty * value;

            totalLabel.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("0.00");
        }
    }

    /**
     * Creates and returns the bottom panel containing overall total,
     * total items count, and action buttons.
     *
     * @return JPanel containing totals and action buttons
     */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Left side - Overall Total and Total Items
        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        totalsPanel.setOpaque(false);

        JPanel overallTotalContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        overallTotalContainer.setOpaque(false);
        JLabel overallTotalLabel = new JLabel("Overall Total:");
        overallTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField overallTotalField = getInputField();
        overallTotalField.setPreferredSize(new Dimension(100, 30));
        overallTotalContainer.add(overallTotalLabel);
        overallTotalContainer.add(overallTotalField);

        JPanel totalItemsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalItemsContainer.setOpaque(false);
        JLabel totalItemsLabel = new JLabel("Total Items:");
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField totalItemsField = getInputField();
        totalItemsField.setPreferredSize(new Dimension(100, 30));
        totalItemsContainer.add(totalItemsLabel);
        totalItemsContainer.add(totalItemsField);

        totalsPanel.add(overallTotalContainer);
        totalsPanel.add(totalItemsContainer);

        // Right side - Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonsPanel.setOpaque(false);

        JButton addToInvoiceButton = createActionButton("Add to Invoice", new Color(130, 170, 255));
        addToInvoiceButton.addActionListener(e -> {
            // Navigate to Invoice Screen
            MainActivity.getInstance().showScreen(MainActivity.INVOICE_SCREEN);
        });

        JButton addToSummaryButton = createActionButton("Add to Summary", new Color(130, 170, 255));
        addToSummaryButton.addActionListener(e -> {
            // Navigate to Summary Screen
            MainActivity.getInstance().showScreen(MainActivity.SUMMARY_SCREEN);
        });

        buttonsPanel.add(addToInvoiceButton);
        buttonsPanel.add(addToSummaryButton);

        // Wrap everything in a panel that can wrap when needed
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(totalsPanel);
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(buttonsPanel);

        bottomPanel.add(wrapperPanel, BorderLayout.CENTER);

        return bottomPanel;
    }

    /**
     * Creates and returns a styled action button.
     *
     * @param text the text to display on the button
     * @param bgColor the background color of the button
     * @return JButton styled action button
     */
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

    /**
     * Creates and returns a styled input field for customer information.
     * The field has rounded corners and custom styling.
     *
     * @return JTextField styled input field
     */
    private static JTextField getInputField() {
        JTextField inputField = new JTextField("", 15);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setForeground(Color.GRAY);
        inputField.setBackground(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        return inputField;
    }

    /**
     * Creates and returns a styled label that looks like an input field.
     * This is used for non-editable value and total columns.
     *
     * @param text the initial text for the label
     * @return JLabel styled to look like an input field
     */
    private static JLabel getStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(Color.BLACK);
        label.setBackground(new Color(245, 245, 245)); // Light gray background
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        // Ensure the label can be resized properly
        label.setMinimumSize(new Dimension(50, 30));
        label.setPreferredSize(new Dimension(100, 30));

        return label;
    }

    /**
     * Custom border class for creating rounded borders on text fields.
     */
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color borderColor;

        /**
         * Constructor for RoundedBorder.
         *
         * @param radius the radius of the rounded corners
         * @param borderColor the color of the border
         */
        RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 2;
            return insets;
        }
    }
}