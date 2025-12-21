package screens;

import components.NavBarPanel;
import javax.swing.*;
import java.awt.*;

/**
 * SummaryScreen class represents the summary interface.
 * This screen displays a summary view for the application.
 *
 * @author Your Name
 * @version 1.0
 */
public class SummaryScreen extends JPanel {

    /**
     * Constructor that initializes and displays the SummaryScreen.
     * Sets up the navigation bar and summary content.
     */
    public SummaryScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar
        JPanel navBarPanel = new NavBarPanel("Summary");

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Placeholder content
        JLabel contentLabel = new JLabel("Summary content will be displayed here");
        contentLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        contentLabel.setForeground(Color.GRAY);
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(contentLabel);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }
}