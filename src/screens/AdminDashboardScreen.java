package screens;

import components.AdminNavBarPanel;
import javax.swing.*;
import java.awt.*;

/**
 * AdminDashboardScreen displays admin dashboard with statistics and overview.
 * Shows key metrics like total invoices, inventory items, and recent activities.
 *
 * @author Your Name
 * @version 1.0
 */
public class AdminDashboardScreen extends JPanel {

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

        // Total Invoices Card
        JPanel invoicesCard = createStatCard("Total Invoices", "0", new Color(100, 200, 150));

        // Inventory Items Card
        JPanel inventoryCard = createStatCard("Inventory Items", "7", new Color(130, 170, 255));

        // Total Revenue Card
        JPanel revenueCard = createStatCard("Total Revenue", "$0.00", new Color(255, 180, 100));

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
}
