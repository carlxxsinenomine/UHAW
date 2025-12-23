package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.swing.*;

public class AdminDashboardScreen extends JPanel {

    // --- Modern Color Palette ---
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(50, 50, 50);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);

    // --- Accent Colors ---
    private static final Color ACCENT_BLUE = new Color(66, 133, 244);
    private static final Color ACCENT_GREEN = new Color(15, 157, 88);
    private static final Color ACCENT_ORANGE = new Color(255, 160, 0);

    // Dynamic Components
    private JLabel invoicesValueLabel;
    private JLabel revenueValueLabel;
    private JPanel activityListPanel; // Panel to hold the list rows
    private Timer refreshTimer;

    public AdminDashboardScreen() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Main container with proper spacing
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Navigation Bar - PASS false to hide search bar
        AdminNavBarPanel navBarPanel = new AdminNavBarPanel("Dashboard", false);

        // 2. Main Content Wrapper
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- Header Section ---
        JLabel titleLabel = new JLabel("Overview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
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
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(BG_COLOR);
        panel.setOpaque(false);

        invoicesValueLabel = new JLabel("0");
        revenueValueLabel = new JLabel("PHP 0.00");

        panel.add(createModernCard("Total Invoices", invoicesValueLabel, "Invoices generated", ACCENT_BLUE));
        panel.add(createModernCard("Inventory Items", new JLabel("15"), "In Stock", ACCENT_GREEN)); // Static or load from JSON
        panel.add(createModernCard("Total Revenue", revenueValueLabel, "Accumulated Earnings", ACCENT_ORANGE));

        return panel;
    }

    private JPanel createModernCard(String title, JLabel valueLabel, String subtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Arial", Font.BOLD, 12));
        titleLbl.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        subLbl.setForeground(Color.GRAY);

        textPanel.add(titleLbl);
        textPanel.add(valueLabel);
        textPanel.add(subLbl);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActivityContainer() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel header = new JLabel("Recent Activity");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Card Container
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
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

    private JPanel createActivityRow(String text, String date, boolean isLast) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        textLbl.setForeground(TEXT_PRIMARY);

        JLabel dateLbl = new JLabel(date);
        dateLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLbl.setForeground(TEXT_SECONDARY);

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
                    if (line.contains("Amount due")) {
                        String amountStr = line.substring(line.indexOf("Amount due") + 10).trim();
                        amountStr = amountStr.replace("PHP", "").replace(",", "").trim();
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
                String activityText = "New Invoice Generated: " + f.getName();
                String dateText = sdf.format(new Date(f.lastModified()));

                // Pass true if it's the last item to remove the border separator
                activityListPanel.add(createActivityRow(activityText, dateText, i == limit - 1));
            }
        }

        activityListPanel.revalidate();
        activityListPanel.repaint();
    }

    private File[] getInvoiceFiles() {
        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) invoicesDir = new File("src/main/invoices");
        if (!invoicesDir.exists() || !invoicesDir.isDirectory()) return null;

        return invoicesDir.listFiles((dir, name) -> name.endsWith(".txt"));
    }
}