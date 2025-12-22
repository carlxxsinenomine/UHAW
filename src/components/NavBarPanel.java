package components;

import java.awt.*;
import javax.swing.*;
import main.MainActivity;

/*
   // Syempre with the help of my beloved Claude.ai
   // Notice kung pano ko pigseparate into components codes nya
   // Para gawing isang buong compnent nalng tong panel nato
   // https://claude.ai/public/artifacts/c1ebb3c7-4f66-45f3-90af-ae93586c0174
    JFrame
        - containerPanel
            - navPanel
                - leftPanel
                    - User label, mga buttons
                - rightPanel
                    - Search bar
 **/

/**
 * NavBarPanel represents the navigation bar component.
 * Contains navigation buttons for different screens and a search field.
 *
 * @author Your Name
 * @version 1.0
 */
public class NavBarPanel extends JPanel {
    private String activeScreen;

    /**
     * Constructor that creates the navigation bar with specified active screen.
     *
     * @param activeScreen the name of the currently active screen
     */
    public NavBarPanel(String activeScreen) {
        this.activeScreen = activeScreen;
        setLayout(new BorderLayout());
        setBackground(new Color(130, 170, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left section with User and navigation items
        JPanel leftPanel = getJPanel();

        // Right section with search bar
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Search field
        JTextField searchField = getSearchField();
        rightPanel.add(searchField);

        // Add panels to nav panel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Creates and returns the search field with rounded corners and placeholder text.
     *
     * @return JTextField styled search field with placeholder behavior
     */
    private static JTextField getSearchField() {
        String placeholder = "Search";
        JTextField searchField = new JTextField(20);
        searchField.setText(placeholder);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(new Color(200, 220, 255));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 255), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Add placeholder behavior with focus events
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Round the search field corners
        searchField.setOpaque(false);
        searchField.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(searchField.getBackground());
                g2.fillRoundRect(0, 0, searchField.getWidth(), searchField.getHeight(), 20, 20);
                g2.dispose();
            }
        });
        return searchField;
    }

    /**
     * Creates and returns the left panel containing the title and navigation buttons.
     *
     * @return JPanel containing navigation elements
     */
    private JPanel getJPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        leftPanel.setOpaque(false);

        // User label (bold and larger) - changes based on active screen
        JLabel userLabel = new JLabel(activeScreen);
        userLabel.setFont(new Font("Arial", Font.BOLD, 28));
        userLabel.setForeground(Color.BLACK);

        // Navigation buttons with navigation functionality
        JButton homeButton = new NavButton("Home");
        homeButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN);
            }
        });

        JButton invoicesButton = new NavButton("Invoices");
        invoicesButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.INVOICE_SCREEN);
            }
        });

        JButton summaryButton = new NavButton("Summary");
        summaryButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.SUMMARY_SCREEN);
            }
        });
        
        JButton adminButton = new NavButton("Admin");
        adminButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_LOGIN_SCREEN);
            }
        });

        leftPanel.add(userLabel);
        leftPanel.add(homeButton);
        leftPanel.add(invoicesButton);
        leftPanel.add(summaryButton);
        leftPanel.add(adminButton);
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
}