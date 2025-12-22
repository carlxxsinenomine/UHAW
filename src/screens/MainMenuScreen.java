package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import main.MainActivity;

/**
 * MainMenuScreen represents the modern entry point of the application.
 * Features a split-pane design to reduce dead space and improve aesthetics.
 *
 * @author Your Name
 * @version 2.0
 */
public class MainMenuScreen extends JPanel {

    // Brand Colors
    private static final Color PRIMARY_BLUE = new Color(130, 170, 255);
    private static final Color DARKER_BLUE = new Color(80, 120, 220);
    private static final Color TEXT_DARK = new Color(50, 50, 50);

    public MainMenuScreen() {
        setLayout(new GridLayout(1, 2)); // Split 50/50

        // --- LEFT PANEL (Branding) ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PRIMARY_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Center vertical alignment for left panel content
        leftPanel.add(Box.createVerticalGlue());

        JLabel logoLabel = new JLabel("PLC"); // Placeholder for logo
        logoLabel.setFont(new Font("Arial", Font.BOLD, 80));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Invoice System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Management Edition 2025");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(230, 240, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>Welcome to the Peter Loves Carl Co.<br>comprehensive invoice and inventory<br>management solution.</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(230, 240, 255));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        leftPanel.add(descLabel);
        leftPanel.add(Box.createVerticalGlue());

        // --- RIGHT PANEL (Navigation) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(Color.WHITE);

        JLabel menuTitle = new JLabel("Main Menu");
        menuTitle.setFont(new Font("Arial", Font.BOLD, 24));
        menuTitle.setForeground(TEXT_DARK);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        menuContainer.add(menuTitle);
        menuContainer.add(createStyledMenuButton("User Panel", "Create & View Invoices", () ->
                MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        menuContainer.add(createStyledMenuButton("Admin Panel", "Inventory & Users", () ->
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_LOGIN_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        menuContainer.add(createStyledMenuButton("Exit System", "Close Application", () ->
                System.exit(0)));

        // Copyright footer on right side
        JLabel copyLabel = new JLabel("© 2025 Peter Loves Carl Co.");
        copyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyLabel.setForeground(Color.LIGHT_GRAY);
        copyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        menuContainer.add(copyLabel);

        rightPanel.add(menuContainer);

        // Add both panels
        add(leftPanel);
        add(rightPanel);
    }

    /**
     * Creates a sophisticated menu button with hover effects.
     */
    private JButton createStyledMenuButton(String title, String subtitle, Runnable action) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(300, 70));
        button.setMaximumSize(new Dimension(300, 70));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(TEXT_DARK);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        subLbl.setForeground(Color.GRAY);
        subLbl.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLbl);
        textPanel.add(subLbl);

        JLabel arrowLbl = new JLabel(">");
        arrowLbl.setFont(new Font("Arial", Font.BOLD, 20));
        arrowLbl.setForeground(Color.LIGHT_GRAY);
        arrowLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        button.add(textPanel, BorderLayout.CENTER);
        button.add(arrowLbl, BorderLayout.EAST);

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(245, 248, 255));
                button.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));
                arrowLbl.setForeground(PRIMARY_BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
                arrowLbl.setForeground(Color.LIGHT_GRAY);
            }
        });

        button.addActionListener(e -> action.run());

        return button;
    }
}