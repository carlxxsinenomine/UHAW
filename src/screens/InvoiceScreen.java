package screens;

import components.NavBarPanel;
import javax.swing.*;
import java.awt.*;

/**
 * InvoiceScreen class represents the invoice summary interface.
 * This screen displays a summary of invoices with company names and item names.
 *
 * @author Your Name
 * @version 1.0
 */
public class InvoiceScreen extends JPanel {

    /**
     * Constructor that initializes and displays the InvoiceScreen.
     * Sets up the navigation bar, title, and invoice summary table.
     */
    public InvoiceScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar
        JPanel navBarPanel = new NavBarPanel("Invoices");

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Summary of invoice");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    /**
     * Creates and returns the invoice summary table panel.
     * Contains a header row with "Company Name" and "Item Name" columns,
     * and a scrollable area for invoice entries.
     *
     * @return JPanel containing the invoice summary table
     */
    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        headerPanel.setBackground(new Color(200, 200, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel companyNameHeader = new JLabel("Company Name");
        companyNameHeader.setFont(new Font("Arial", Font.BOLD, 14));
        companyNameHeader.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel itemNameHeader = new JLabel("Item Name");
        itemNameHeader.setFont(new Font("Arial", Font.BOLD, 14));
        itemNameHeader.setHorizontalAlignment(SwingConstants.LEFT);

        headerPanel.add(companyNameHeader);
        headerPanel.add(itemNameHeader);

        // Content area (empty for now, will contain invoice entries)
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Example: Add a placeholder message
        JLabel placeholderLabel = new JLabel("No invoices to display");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setOpaque(false);
        placeholderPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        placeholderPanel.add(placeholderLabel);

        contentArea.add(placeholderPanel);

        // Scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(200, 200, 200)));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        tableContainer.add(headerPanel, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    /**
     * Creates and returns a single invoice row entry.
     * Each row displays a company name and item name.
     *
     * @param companyName the name of the company
     * @param itemName the name of the item
     * @return JPanel representing a single invoice row
     */
    private JPanel createInvoiceRow(String companyName, String itemName) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 0, 15, 0)
        ));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel companyLabel = new JLabel(companyName);
        companyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        companyLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel itemLabel = new JLabel(itemName);
        itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        itemLabel.setHorizontalAlignment(SwingConstants.LEFT);

        rowPanel.add(companyLabel);
        rowPanel.add(itemLabel);

        return rowPanel;
    }

    /**
     * Adds an invoice entry to the summary table.
     * This method can be called to dynamically add invoice entries.
     *
     * @param companyName the name of the company
     * @param itemName the name of the item
     */
    public void addInvoiceEntry(String companyName, String itemName) {
        // This method would be implemented to add rows dynamically
        // For now, it's a placeholder for future functionality
    }
}