package screens;

import components.NavBarPanel; // Using the external component
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import main.MainActivity;

/**
 * UserScreen class represents the main invoice generation interface.
 * Uses the shared NavBarPanel for consistent navigation.
 *
 * @author Your Name
 * @version 2.2
 */
public class UserScreen extends JPanel {

    private Map<String, Double> itemValueMap;
    private Map<String, String> itemCategoryMap;
    private Set<String> selectedCategories;
    private JPanel itemsPanel;
    private JLabel overallTotalLabel;
    private JLabel totalItemsLabel;
    private java.util.List<JSpinner> quantitySpinners;

    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Initialize Data Structures
        itemValueMap = new HashMap<>();
        itemCategoryMap = new HashMap<>();
        selectedCategories = new HashSet<>();
        quantitySpinners = new java.util.ArrayList<>();

        // 2. Load Data
        loadInventoryData();
        selectedCategories.addAll(Arrays.asList("1", "2", "3"));

        // 3. Navigation Bar (Uses the updated component with Main Menu button)
        // Passing "USER" makes the big label say "USER"
        NavBarPanel navBarPanel = new NavBarPanel("USER");

        // Wrapper for the header (Optional: adds title below nav bar if desired)
        JPanel navWrapper = new JPanel(new BorderLayout());
        navWrapper.setOpaque(false);
        navWrapper.add(navBarPanel, BorderLayout.CENTER);

        // Sub-header / Company Title
        JLabel companyName = new JLabel("Generate New Invoice for Peter Loves Carl Co.");
        companyName.setFont(new Font("Arial", Font.BOLD, 32));
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        companyName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 20, 10, 20),
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(130, 170, 255))
        ));
        navWrapper.add(companyName, BorderLayout.SOUTH);

        // 4. Main Content Panels
        JPanel informationInputContainer = getCustomerInfoPanel();
        JPanel invoiceTablePanel = getInvoiceTablePanel(); // logic for rows
        JPanel bottomPanel = getBottomPanel(); // logic for totals

        // Assemble Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(informationInputContainer, BorderLayout.NORTH);
        contentPanel.add(invoiceTablePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Main Container with padding
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
            if (file == null || !file.exists()) {
                loadSampleData();
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
            e.printStackTrace();
            loadSampleData();
        }
    }

    private void loadSampleData() {
        itemValueMap.put("Product A", 150.00); itemCategoryMap.put("Product A", "1");
        itemValueMap.put("Product B", 75.50); itemCategoryMap.put("Product B", "1");
        itemValueMap.put("Service X", 250.00); itemCategoryMap.put("Service X", "2");
        itemValueMap.put("Product C", 45.99); itemCategoryMap.put("Product C", "2");
        itemValueMap.put("Service Y", 500.00); itemCategoryMap.put("Service Y", "3");
        itemValueMap.put("Product D", 199.99); itemCategoryMap.put("Product D", "3");
        itemValueMap.put("Product E", 29.50); itemCategoryMap.put("Product E", "1");
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
                    if (itemName != null && valueStr != null && category != null) {
                        try {
                            double value = Double.parseDouble(valueStr.replaceAll(",.*", "").trim());
                            itemValueMap.put(itemName, value);
                            itemCategoryMap.put(itemName, category);
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
        nameContainer.add(nameLabel, BorderLayout.WEST);
        nameContainer.add(getInputField(), BorderLayout.CENTER);

        JPanel contactContainer = new JPanel(new BorderLayout(5, 0));
        contactContainer.setOpaque(false);
        JLabel contactLabel = new JLabel("Contact No.:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactContainer.add(contactLabel, BorderLayout.WEST);
        contactContainer.add(getInputField(), BorderLayout.CENTER);

        JPanel addressContainer = new JPanel(new BorderLayout(5, 0));
        addressContainer.setOpaque(false);
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressContainer.add(addressLabel, BorderLayout.WEST);
        addressContainer.add(getInputField(), BorderLayout.CENTER);

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

        // This calculates rows. Note: updateOverallTotals is called here,
        // but overallTotalLabel might be null if called before getBottomPanel.
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
        int itemCount = getFilteredItemCount();
        for (int i = 1; i <= itemCount; i++) {
            itemsPanel.add(createItemRow(i));
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
        updateOverallTotals();
    }

    private JPanel createItemRow(int rowIndex) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        java.util.List<String> sortedItems = new java.util.ArrayList<>();
        for (String itemName : itemValueMap.keySet()) {
            if (selectedCategories.contains(itemCategoryMap.get(itemName))) sortedItems.add(itemName);
        }
        Collections.sort(sortedItems);
        String itemName = "";
        double itemValue = 0.0;
        if (rowIndex <= sortedItems.size()) {
            itemName = sortedItems.get(rowIndex - 1);
            itemValue = itemValueMap.get(itemName);
        }
        JLabel itemNameLabel = getStyledLabel(itemName);
        itemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        qtySpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        quantitySpinners.add(qtySpinner);

        JLabel valueLabel = getStyledLabel(String.format("%.2f", itemValue));
        JLabel totalLabel = getStyledLabel("0.00");

        qtySpinner.addChangeListener(e -> {
            updateTotalFromLabel(qtySpinner, valueLabel, totalLabel);
            updateOverallTotals();
        });

        rowPanel.add(itemNameLabel);
        rowPanel.add(qtySpinner);
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

        JButton addToInvoiceButton = createActionButton("Add to Invoice", new Color(130, 170, 255));
        addToInvoiceButton.addActionListener(e -> MainActivity.getInstance().showScreen(MainActivity.INVOICE_SCREEN));

        JButton addToSummaryButton = createActionButton("Add to Summary", new Color(130, 170, 255));
        addToSummaryButton.addActionListener(e -> MainActivity.getInstance().showScreen(MainActivity.SUMMARY_SCREEN));

        buttonsPanel.add(addToInvoiceButton);
        buttonsPanel.add(addToSummaryButton);

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
        // SAFETY CHECK: Labels might be null during initialization phase
        if (overallTotalLabel == null || totalItemsLabel == null) {
            return;
        }

        double overallTotal = 0.0;
        int totalItems = 0;
        Component[] components = itemsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] rowComponents = ((JPanel) comp).getComponents();
                if (rowComponents.length >= 4 && rowComponents[1] instanceof JSpinner && rowComponents[3] instanceof JLabel) {
                    try {
                        int qty = (Integer) ((JSpinner) rowComponents[1]).getValue();
                        overallTotal += Double.parseDouble(((JLabel) rowComponents[3]).getText().trim());
                        if (qty > 0) totalItems += qty;
                    } catch (Exception e) {}
                }
            }
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