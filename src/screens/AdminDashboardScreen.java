package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.*;
import main.AppConstants;

/**
 * AdminDashboardScreen - The main administrative overview and analytics screen.
 * 
 * This screen provides admins with a high-level overview of the system's key metrics
 * and recent activity. It serves as the landing page after admin login and displays
 * real-time data about the business operations.
 * 
 * Key Features:
 * - Statistics Cards: Displays total invoices, inventory items, and total revenue
 * - Activity Feed: Shows recent invoice generation and system activities
 * - Auto-Refresh: Updates statistics every 5 seconds for real-time monitoring
 * - Professional Dashboard Layout: Clean, card-based design with visual hierarchy
 * 
 * Displayed Metrics:
 * - Total Invoices: Count of all generated invoices
 * - Inventory Items: Current count of items in stock
 * - Total Revenue: Sum of all invoice amounts in PHP currency
 * - Recent Activity: Latest 5 invoices with timestamps
 * 
 * Data Sources:
 * - Invoices: Loaded from text files in the invoices directory
 * - Activity: Extracted from file modification dates and invoice content
 * - Updates: Automatic refresh every 5 seconds via Timer
 * 
 * Navigation:
 * - Search feature disabled (not applicable for dashboard)
 * - Uses AdminNavBarPanel with showSearchBar = false
 */
public class AdminDashboardScreen extends JPanel {

    // Dynamic Components
    private JLabel invoicesValueLabel;
    private JLabel revenueValueLabel;
    private JPanel activityListPanel; // Panel to hold the list rows
    private Timer refreshTimer;

    /**
     * Constructs the AdminDashboardScreen with statistics and activity display.
     * 
     * Initialization:
     * 1. Creates the main layout with navigation bar
     * 2. Creates statistics cards for key metrics
     * 3. Creates activity feed panel for recent invoices
     * 4. Loads initial data
     * 5. Starts auto-refresh timer (updates every 5 seconds)
     * 
     * Auto-Refresh:
     * - Triggered via a Timer that fires every 5000 milliseconds
     * - Calls refreshDashboardStats() for metric updates
     * - Calls refreshActivityList() for activity feed updates
     * - Ensures real-time data display without user intervention
     */
    public AdminDashboardScreen() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_LIGHT_GRAY);

        // Main container with proper spacing
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(AppConstants.BG_LIGHT_GRAY);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Navigation Bar - PASS false to hide search bar
        AdminNavBarPanel navBarPanel = new AdminNavBarPanel("Dashboard", false);

        // 2. Main Content Wrapper
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppConstants.BG_LIGHT_GRAY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- Header Section ---
        JLabel titleLabel = new JLabel("Overview");
        titleLabel.setFont(AppConstants.FONT_TITLE_LARGE_REGULAR);
        titleLabel.setForeground(AppConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel activityPanel = createActivityContainer();
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble Content
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(activityPanel);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        // Add to Main Frame
        add(mainContainer);

        // Initial Load
        refreshDashboardStats();
        refreshActivityList();

        // Start auto-refresh timer (updates every 5 seconds)
        startAutoRefresh();
    }

    //UI stuffs
    /**
     * Creates the statistics panel with three metric cards.
     * 
     * Displays three key performance indicators in a grid layout:
     * 1. Total Invoices: Number of invoices generated
     * 2. Inventory Items: Count of items in stock
     * 3. Total Revenue: Sum of all invoice amounts
     * 
     * Each metric is displayed in a modern card with:
     * - Title in uppercase
     * - Large numeric value
     * - Descriptive subtitle
     * - Colored accent bar (blue, green, orange)
     * 
     * @return A configured JPanel containing the three metric cards
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(AppConstants.BG_LIGHT_GRAY);
        panel.setOpaque(false);

        invoicesValueLabel = new JLabel("0");
        revenueValueLabel = new JLabel("PHP 0.00");

        panel.add(createModernCard("Total Invoices", invoicesValueLabel, "Invoices generated", AppConstants.ACCENT_BLUE));
        panel.add(createModernCard("Inventory Items", new JLabel("15"), "In Stock", AppConstants.ACCENT_GREEN_BRIGHT)); // Static or load from JSON
        panel.add(createModernCard("Total Revenue", revenueValueLabel, "Accumulated Earnings", AppConstants.ACCENT_ORANGE));

        return panel;
    }

    /**
     * Creates an individual metric card for the statistics panel.
     * 
     * Each card features:
     * - Colored left border (accent color) for visual distinction
     * - Title in uppercase gray text
     * - Large value display (numeric or currency formatted)
     * - Subtitle describing the metric
     * - White background with padding
     * 
     * @param title The title of the metric (e.g., "Total Invoices")
     * @param valueLabel The JLabel displaying the metric value (updated dynamically)
     * @param subtext The descriptive subtitle
     * @param accentColor The color for the left border accent
     * @return A styled JPanel representing one metric card
     */
    private JPanel createModernCard(String title, JLabel valueLabel, String subtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppConstants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Arial", Font.BOLD, 12));
        titleLbl.setForeground(AppConstants.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(AppConstants.TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        subLbl.setForeground(Color.GRAY);

        textPanel.add(titleLbl);
        textPanel.add(valueLabel);
        textPanel.add(subLbl);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates the activity feed panel showing recent invoices and system activity.
     * 
     * This panel displays:
     * - Header: "Recent Activity" title
     * - Card container: White background with border
     * - Scrollable list: Top 5 recent invoices with timestamps
     * - Empty state: Shows "No recent activities logged" if no invoices exist
     * 
     * Layout:
     * - Header at top
     * - Scrollable area below with activity rows
     * - Each row shows invoice ID and timestamp
     * - Rows separated by subtle dividers
     * 
     * @return A configured JPanel containing the activity feed
     */
    private JPanel createActivityContainer() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel header = new JLabel("Recent Activity");
        header.setFont(AppConstants.FONT_TITLE_XL);
        header.setForeground(AppConstants.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Card Container
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(AppConstants.BG_WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        // The list panel that will hold rows
        activityListPanel = new JPanel();
        activityListPanel.setLayout(new BoxLayout(activityListPanel, BoxLayout.Y_AXIS));
        activityListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(activityListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Fix height of activity section
        scrollPane.setPreferredSize(new Dimension(800, 250));

        card.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Creates an individual activity row for the activity feed.
     * 
     * Each row displays:
     * - Activity description on the left (e.g., "Invoice Generated: INV-001")
     * - Timestamp on the right (formatted as "MMM dd, HH:mm")
     * - Optional separator line (unless it's the last item)
     * 
     * @param text The activity description text
     * @param date The timestamp of the activity
     * @param isLast Boolean indicating if this is the last item (affects divider display)
     * @return A styled JPanel representing one activity row
     */
    private JPanel createActivityRow(String text, String date, boolean isLast) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(AppConstants.FONT_BODY_REGULAR);
        textLbl.setForeground(AppConstants.TEXT_PRIMARY);

        JLabel dateLbl = new JLabel(date);
        dateLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLbl.setForeground(AppConstants.TEXT_SECONDARY);

        row.add(textLbl, BorderLayout.WEST);
        row.add(dateLbl, BorderLayout.EAST);

        // Add separator line unless it's the last item
        if (!isLast) {
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
                    BorderFactory.createEmptyBorder(15, 25, 15, 25)
            ));
        }

        return row;
    }

    //Main logic
    private void startAutoRefresh() {
        // Refresh every 5 seconds to avoid heavy IO
        refreshTimer = new Timer(5000, e -> {
            refreshDashboardStats();
            refreshActivityList();
        });
        refreshTimer.start();
    }

    public void stopAutoRefresh() {
        if (refreshTimer != null) refreshTimer.stop();
    }

    // --- Stats Logic ---
    public void refreshDashboardStats() {
        if (invoicesValueLabel != null && revenueValueLabel != null) {
            invoicesValueLabel.setText(String.valueOf(getTotalInvoicesCount()));
            revenueValueLabel.setText(String.format("PHP %,.2f", calculateTotalRevenue()));
        }
    }

    private int getTotalInvoicesCount() {
        File[] files = getInvoiceFiles();
        return files != null ? files.length : 0;
    }

    private double calculateTotalRevenue() {
        File[] files = getInvoiceFiles();
        if (files == null) return 0.0;

        double totalRevenue = 0.0;
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("TOTAL AMOUNT DUE:")) {
                        String amountStr = line.replace("TOTAL AMOUNT DUE:", "")
                                              .replace("PHP", "")
                                              .replace(",", "")
                                              .trim();
                        try {
                            totalRevenue += Double.parseDouble(amountStr);
                        } catch (NumberFormatException e) { /* ignore */ }
                        break;
                    }
                }
            } catch (IOException e) { /* ignore */ }
        }
        return totalRevenue;
    }

    // --- Activity List Logic ---
    public void refreshActivityList() {
        if (activityListPanel == null) return;

        activityListPanel.removeAll();
        File[] files = getInvoiceFiles();

        if (files == null || files.length == 0) {
            JPanel emptyRow = createActivityRow("No recent activities logged.", "", true);
            activityListPanel.add(emptyRow);
        } else {
            // Sort by Last Modified (Descending)
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

            // Take top 5
            int limit = Math.min(files.length, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

            for (int i = 0; i < limit; i++) {
                File f = files[i];
                // Extract invoice ID from filename
                String invoiceId = f.getName().replace(".txt", "");
                String activityText = "Invoice Generated: " + invoiceId;
                String dateText = sdf.format(new Date(f.lastModified()));

                // Pass true if it's the last item to remove the border separator
                activityListPanel.add(createActivityRow(activityText, dateText, i == limit - 1));
            }
        }

        activityListPanel.revalidate();
        activityListPanel.repaint();
    }

    private File[] getInvoiceFiles() {
        // Try multiple invoice directory locations
        File[] possibleDirs = {
            new File("src/main/invoices"),
            new File("src/invoices"),
            new File("invoices"),
            new File("../invoices"),
            new File("./invoices")
        };
        
        File invoicesDir = null;
        for (File dir : possibleDirs) {
            if (dir.exists() && dir.isDirectory()) {
                invoicesDir = dir;
                break;
            }
        }
        
        if (invoicesDir == null || !invoicesDir.isDirectory()) {
            return null;
        }

        return invoicesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
    }
}