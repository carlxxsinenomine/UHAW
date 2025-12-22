package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * AdminDashboardScreen displays admin dashboard with statistics and overview.
 * Shows key metrics like total invoices, inventory items, and recent activities.
 *
 * @author Your Name
 * @version 1.0
 */
public class AdminDashboardScreen extends JPanel {
    private JLabel invoicesValueLabel;
    private JLabel revenueValueLabel;
    private Timer refreshTimer;

    /**
     * Constructor that initializes and displays the AdminDashboardScreen.
     */
    public AdminDashboardScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar
        JPanel navBarPanel = new AdminNavBarPanel("Dashboard");

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Statistics cards panel
        JPanel statsPanel = createStatsPanel();

        // Recent activity panel
        JPanel activityPanel = createActivityPanel();

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(activityPanel, BorderLayout.SOUTH);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        // Start auto-refresh timer (updates every 2 seconds)
        startAutoRefresh();
    }
    
    /**
     * Starts an auto-refresh timer that updates dashboard stats periodically.
     */
    private void startAutoRefresh() {
        refreshTimer = new Timer(2000, e -> refreshDashboardStats());
        refreshTimer.start();
    }
    
    /**
     * Stops the auto-refresh timer.
     */
    public void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
    
    /**
     * Refreshes all dashboard statistics.
     */
    public void refreshDashboardStats() {
        if (invoicesValueLabel != null && revenueValueLabel != null) {
            int totalInvoices = getTotalInvoicesCount();
            double totalRevenue = calculateTotalRevenue();
            
            invoicesValueLabel.setText(String.valueOf(totalInvoices));
            revenueValueLabel.setText(String.format("PHP %,.2f", totalRevenue));
        }
    }

    /**
     * Creates the statistics panel with metric cards.
     *
     * @return JPanel containing statistics cards
     */
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Calculate metrics dynamically
        int totalInvoices = getTotalInvoicesCount();
        double totalRevenue = calculateTotalRevenue();
        int inventoryItems = 7; // This is static based on the inventory.json

        // Total Invoices Card - store reference for updates
        invoicesValueLabel = new JLabel(String.valueOf(totalInvoices));
        JPanel invoicesCard = createStatCardWithLabel("Total Invoices", invoicesValueLabel, new Color(100, 200, 150));

        // Inventory Items Card
        JPanel inventoryCard = createStatCard("Inventory Items", String.valueOf(inventoryItems), new Color(130, 170, 255));

        // Total Revenue Card - store reference for updates
        revenueValueLabel = new JLabel(String.format("PHP %,.2f", totalRevenue));
        JPanel revenueCard = createStatCardWithLabel("Total Revenue", revenueValueLabel, new Color(255, 180, 100));

        statsPanel.add(invoicesCard);
        statsPanel.add(inventoryCard);
        statsPanel.add(revenueCard);

        return statsPanel;
    }

    /**
     * Creates a single statistics card.
     *
     * @param title the title of the stat
     * @param value the value to display
     * @param color the background color of the card
     * @return JPanel representing a stat card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    /**
     * Creates a single statistics card with a reference label for updates.
     *
     * @param title the title of the stat
     * @param valueLabel the label to display (reference stored for updates)
     * @param color the background color of the card
     * @return JPanel representing a stat card
     */
    private JPanel createStatCardWithLabel(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    /**
     * Creates the recent activity panel.
     *
     * @return JPanel containing recent activities
     */
    private JPanel createActivityPanel() {
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel activityTitle = new JLabel("Recent Activities");
        activityTitle.setFont(new Font("Arial", Font.BOLD, 20));
        activityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel activityContent = new JPanel();
        activityContent.setLayout(new BoxLayout(activityContent, BoxLayout.Y_AXIS));
        activityContent.setBackground(Color.WHITE);
        activityContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel placeholderLabel = new JLabel("No recent activities");
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        activityContent.add(placeholderLabel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(activityTitle, BorderLayout.NORTH);
        wrapper.add(activityContent, BorderLayout.CENTER);

        activityPanel.add(wrapper);

        return activityPanel;
    }

    /**
     * Gets the total count of invoices from the invoices directory.
     *
     * @return the total number of invoices
     */
    private int getTotalInvoicesCount() {
        File invoicesDir = new File("invoices");
        
        if (!invoicesDir.exists()) {
            invoicesDir = new File("src/main/invoices");
        }
        
        if (!invoicesDir.exists() || !invoicesDir.isDirectory()) {
            return 0;
        }
        
        File[] invoiceFiles = invoicesDir.listFiles((dir, name) -> name.endsWith(".txt"));
        return invoiceFiles != null ? invoiceFiles.length : 0;
    }

    /**
     * Calculates the total revenue from all invoices.
     *
     * @return the total revenue from all invoices
     */
    private double calculateTotalRevenue() {
        File invoicesDir = new File("invoices");
        
        if (!invoicesDir.exists()) {
            invoicesDir = new File("src/main/invoices");
        }
        
        if (!invoicesDir.exists() || !invoicesDir.isDirectory()) {
            return 0.0;
        }
        
        File[] invoiceFiles = invoicesDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (invoiceFiles == null || invoiceFiles.length == 0) {
            return 0.0;
        }
        
        double totalRevenue = 0.0;
        
        for (File file : invoiceFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Amount due")) {
                        String amountStr = line.substring(line.indexOf("Amount due") + 10).trim();
                        amountStr = amountStr.replace("PHP", "").replace(",", "").trim();
                        try {
                            totalRevenue += Double.parseDouble(amountStr);
                        } catch (NumberFormatException e) {
                            // Skip invalid amounts
                        }
                        break; // Found the amount due, move to next file
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading invoice: " + file.getName());
            }
        }
        
        return totalRevenue;
    }
}
