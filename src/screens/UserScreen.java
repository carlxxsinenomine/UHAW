package screens;

import components.NavBarPanel;
import java.awt.*;
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

    /**
     * Constructor that initializes and displays the UserScreen.
     * Sets up the navigation bar, customer information fields,
     * invoice items table, and action buttons.
     */
    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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
     * Contains a table with columns: Item Name, Description, Qty, Value, and Total.
     * Includes 7 pre-populated rows and an "Add more" button at the bottom.
     *
     * @return JPanel containing the invoice items table
     */
    private JPanel getInvoiceTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 6, 5, 0));
        headerPanel.setBackground(new Color(200, 200, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] headers = {"", "Item Name", "Description", "Qty", "Value", "Total"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerPanel.add(headerLabel);
        }

        // Items panel with rows
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        for (int i = 1; i <= 7; i++) {
            itemsPanel.add(createItemRow(i));
        }

        // Add more button
        JButton addMoreButton = new JButton("+ Add more");
        addMoreButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addMoreButton.setBackground(new Color(220, 220, 220));
        addMoreButton.setFocusPainted(false);
        addMoreButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        addMoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.setOpaque(false);
        scrollableContent.add(itemsPanel, BorderLayout.CENTER);
        scrollableContent.add(addMoreButton, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    /**
     * Creates and returns a single invoice item row.
     * Each row contains a delete icon, item name field, description field,
     * quantity field, value field, and total field.
     *
     * @param itemNumber the item number for this row
     * @return JPanel representing a single item row
     */
    private JPanel createItemRow(int itemNumber) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 6, 5, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Delete icon
        JLabel deleteIcon = new JLabel("🗑", SwingConstants.CENTER);
        deleteIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField itemNameField = createPlaceholderField("Item " + itemNumber);
        JTextField descField = getTableInputField("");
        JTextField qtyField = getTableInputField("");
        JTextField valueField = getTableInputField("");
        JTextField totalField = getTableInputField("");

        rowPanel.add(deleteIcon);
        rowPanel.add(itemNameField);
        rowPanel.add(descField);
        rowPanel.add(qtyField);
        rowPanel.add(valueField);
        rowPanel.add(totalField);

        return rowPanel;
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
    private static JTextField createPlaceholderField(String placeholder) {
        JTextField inputField = new JTextField(10);
        inputField.setText(placeholder);
        inputField.setFont(new Font("Arial", Font.PLAIN, 13));
        inputField.setForeground(Color.GRAY);
        inputField.setBackground(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        inputField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (inputField.getText().equals(placeholder)) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (inputField.getText().isEmpty()) {
                    inputField.setText(placeholder);
                    inputField.setForeground(Color.GRAY);
                }
            }
        });

        return inputField;
    }

    /**
     * Custom border class for creating rounded borders on text fields.
     */
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color borderColor;

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