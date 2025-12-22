package components;

import java.awt.*;
import javax.swing.*;
import main.MainActivity;

/**
 * AdminNavBarPanel represents the admin navigation bar component.
 * Contains navigation buttons for different admin screens.
 *
 * @author Your Name
 * @version 1.0
 */
public class AdminNavBarPanel extends JPanel {
    private String activeScreen;

    /**
     * Constructor that creates the admin navigation bar with specified active screen.
     *
     * @param activeScreen the name of the currently active screen
     */
    public AdminNavBarPanel(String activeScreen) {
        this.activeScreen = activeScreen;
        setLayout(new BorderLayout());
        setBackground(new Color(130, 170, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left section with Admin label and navigation items
        JPanel leftPanel = getJPanel();

        // Right section with logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 220, 220));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_LOGIN_SCREEN);
            }
        });

        rightPanel.add(logoutButton);

        // Add panels to nav panel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Creates and returns the left panel containing the title and navigation buttons.
     *
     * @return JPanel containing navigation elements
     */
    private JPanel getJPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        leftPanel.setOpaque(false);

        // Admin label (bold and larger)
        JLabel adminLabel = new JLabel(activeScreen);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 28));
        adminLabel.setForeground(Color.WHITE);

        // Navigation buttons
        JButton dashboardButton = new AdminNavButton("Dashboard");
        dashboardButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_DASHBOARD_SCREEN);
            }
        });

        JButton inventoryButton = new AdminNavButton("Inventory");
        inventoryButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_INVENTORY_SCREEN);
            }
        });

        JButton invoicesButton = new AdminNavButton("Invoices");
        invoicesButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_INVOICES_SCREEN);
            }
        });

        leftPanel.add(adminLabel);
        leftPanel.add(dashboardButton);
        leftPanel.add(inventoryButton);
        leftPanel.add(invoicesButton);

        return leftPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
    }

    /**
     * AdminNavButton is a custom button for the admin navigation bar.
     */
    private static class AdminNavButton extends JButton {
        public AdminNavButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setForeground(Color.WHITE);
            setBackground(new Color(70, 130, 180));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setForeground(new Color(200, 230, 255));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setForeground(Color.WHITE);
                }
            });
        }
    }
}
