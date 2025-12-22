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
     */
    private void loadInventoryData() {
        try {
            // Path to inventory.json relative to screens folder
            File file = new File("src/items/inventory.json");
            
            // If not found, try relative path from screens
            if (!file.exists()) {
                file = new File("../items/inventory.json");
            }
            
            if (!file.exists()) {
                System.err.println("Could not find inventory.json. Tried: src/items/inventory.json and ../items/inventory.json");
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

    private void parseInventoryJson(String jsonContent) {
        // Parse JSON manually
        String jsonStr = jsonContent;
        jsonStr = jsonStr.substring(jsonStr.indexOf('[') + 1, jsonStr.lastIndexOf(']'));
        
        // Split by objects
        String[] items = jsonStr.split("\\{");
        for (String item : items) {
            if (item.trim().isEmpty()) continue;
            
            // Extract itemName
            int nameStart = item.indexOf("\"itemName\"") + 12;
            int nameEnd = item.indexOf("\"", nameStart + 1);
            String itemName = item.substring(nameStart, nameEnd);
            
            // Extract value
            int valueStart = item.indexOf("\"value\"") + 9;
            int valueEnd = item.indexOf(",", valueStart);
            if (valueEnd == -1) {
                valueEnd = item.indexOf("}", valueStart);
            }
            String valueStr = item.substring(valueStart, valueEnd).trim();
            double value = Double.parseDouble(valueStr);
            
            // Extract category
            int catStart = item.indexOf("\"category\"") + 12;
            int catEnd = item.indexOf("\"", catStart + 1);
            String category = item.substring(catStart, catEnd);
            
            itemValueMap.put(itemName, value);
            itemCategoryMap.put(itemName, category);
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
     * Includes category toggle buttons and 7 pre-populated rows.
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

        // Items panel with rows
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        for (int i = 1; i <= 7; i++) {
            itemsPanel.add(createItemRow(i));
        }

        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.setOpaque(false);
        scrollableContent.add(itemsPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

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
     */
    private void refreshTableRows() {
        itemsPanel.removeAll();
        for (int i = 1; i <= 7; i++) {
            itemsPanel.add(createItemRow(i));
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    /**
     * Creates and returns a single invoice item row.
     * Each row contains item name dropdown, quantity spinner,
     * value field (from inventory), and auto-calculated total field.
     *
     * @return JPanel representing a single item row
     */
    private JPanel createItemRow(int itemNumber) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Item name dropdown
        DefaultComboBoxModel<String> itemModel = new DefaultComboBoxModel<>();
        for (String itemName : itemValueMap.keySet()) {
            String category = itemCategoryMap.get(itemName);
            if (selectedCategories.contains(category)) {
                itemModel.addElement(itemName);
            }
        }
        JComboBox<String> itemNameCombo = new JComboBox<>(itemModel);
        itemNameCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        itemNameCombo.setBackground(Color.WHITE);

        // Qty spinner
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        qtySpinner.setFont(new Font("Arial", Font.PLAIN, 13));

        // Value label (constant from inventory) with border
        JLabel valueLabel = new JLabel("");
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setOpaque(true);
        valueLabel.setBackground(Color.WHITE);
        valueLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // Total field (read-only, calculated)
        JTextField totalField = getTableInputField("");
        totalField.setEditable(false);

        // Add listeners to update value and total
        itemNameCombo.addActionListener(e -> {
            String selectedItem = (String) itemNameCombo.getSelectedItem();
            if (selectedItem != null && itemValueMap.containsKey(selectedItem)) {
                double value = itemValueMap.get(selectedItem);
                valueLabel.setText(String.format("%.2f", value));
                updateTotal(qtySpinner, value, totalField);
            }
        });

        qtySpinner.addChangeListener(e -> {
            if (itemNameCombo.getSelectedItem() != null && itemValueMap.containsKey((String) itemNameCombo.getSelectedItem())) {
                double value = itemValueMap.get((String) itemNameCombo.getSelectedItem());
                updateTotal(qtySpinner, value, totalField);
            }
        });

        rowPanel.add(itemNameCombo);
        rowPanel.add(qtySpinner);
        rowPanel.add(valueLabel);
        rowPanel.add(totalField);

        return rowPanel;
    }

    /**
     * Updates the total field based on qty and value.
     * Total = Qty * Value
     */
    private void updateTotal(JSpinner qtySpinner, double value, JTextField totalField) {
        try {
            int qty = (Integer) qtySpinner.getValue();
            double total = qty * value;
            totalField.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            // Handle invalid number format
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
     * Creates and returns a styled input field for table cells.
     *
     * @param placeholder the placeholder text for the field
     * @return JTextField styled table input field
     */
    private static JTextField getTableInputField(String placeholder) {
        JTextField inputField = new JTextField(placeholder, 10);
        inputField.setFont(new Font("Arial", Font.PLAIN, 13));
        inputField.setForeground(Color.GRAY);
        inputField.setBackground(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        return inputField;
    }

    /**
     * Creates a JTextField with placeholder text that disappears when focused.
     *
     * @param placeholder the placeholder text to display
     * @return JTextField with placeholder behavior
     */

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