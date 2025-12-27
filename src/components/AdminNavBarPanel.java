package components;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.*;
import main.MainActivity;

/**
 * AdminNavBarPanel - Navigation bar component for the Admin-facing screens.
 * 
 * This component provides a professional navigation header for admin screens (AdminDashboardScreen,
 * AdminInventoryScreen, AdminInvoicesScreen). It supports the admin's workflow with screen
 * identification, optional search functionality, and logout capabilities.
 * 
 * Features:
 * - Screen identifier display: Shows which admin screen the user is currently on
 * - Optional search field: Can be enabled or disabled per screen
 * - Dashboard mode: Hides search when not needed (e.g., on Dashboard)
 * - Logout button: Securely logs the admin out and returns to login screen
 * - Search listener: Notifies the parent screen of search text changes
 * - Placeholder management: Context-aware placeholder text based on screen type
 * 
 * Design:
 * - Color: Primary blue background (consistent with NavBarPanel)
 * - Layout: BorderLayout with left panel (title) and right panel (optional search + logout)
 * - Styling: Consistent with the UHAW design system
 * 
 * Search Behavior:
 * - Inventory Screen: Text-based search by item name
 * - Invoices Screen: Date-based search with "Search (YYYY-MM-DD)" placeholder
 * - Dashboard: No search field displayed (showSearchBar = false)
 */
public class AdminNavBarPanel extends JPanel {
    private String activeScreen;
    private Consumer<String> searchListener;
    private JTextField searchField;

    /**
     * Constructs an AdminNavBarPanel with optional search bar visibility control.
     * 
     * This flexible constructor allows admin screens to determine whether they need
     * search functionality. For example:
     * - Dashboard: showSearchBar = false (no search needed for overview)
     * - Inventory: showSearchBar = true (search by item name)
     * - Invoices: showSearchBar = true (search by date or invoice ID)
     * 
     * Components:
     * - Left panel: Screen identifier and navigation breadcrumb
     * - Right panel: Optional search field (if showSearchBar is true) and Logout button
     * - Search placeholder: Changes based on active screen type
     * 
     * @param activeScreen The name of the current admin screen (e.g., "Dashboard", "Inventory", "Invoices")
     * @param showSearchBar If true, displays the search field; if false, hides it
     */
    // NEW: Constructor with search bar control
    public AdminNavBarPanel(String activeScreen, boolean showSearchBar) {
        this.activeScreen = activeScreen;
        // NEW: flag to control search bar visibility
        setLayout(new BorderLayout());
        setBackground(new Color(130, 170, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left section with Admin label and navigation items
        JPanel leftPanel = getJPanel();

        // Right section with Search and Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Only add search field if showSearchBar is true
        if (showSearchBar) {
            searchField = getSearchField();
            rightPanel.add(searchField);
        }

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

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    // Keep original constructor for backward compatibility (defaults to showing search bar)
    public AdminNavBarPanel(String activeScreen) {
        this(activeScreen, true);
    }

    /**
     * Sets a listener that triggers whenever the search text changes.
     * 
     * This is used for real-time filtering on Inventory and Invoices screens.
     * The listener fires on each keystroke, allowing immediate visual feedback
     * as the admin types a search query.
     * 
     * @param listener A Consumer that accepts the current search text
     */
    public void setSearchListener(Consumer<String> listener) {
        this.searchListener = listener;
    }

    /**
     * Resets the search field to its placeholder state.
     * 
     * Called when:
     * - Admin navigates back to the screen
     * - Screen data is refreshed
     * - Filters need to be cleared
     * 
     * The reset clears search input and fires the listener with empty string
     * to reset the parent screen's filtering state.
     */
    public void resetSearch() {
        if (searchField != null) {
            // Set placeholder based on active screen
            String placeholder = activeScreen.equals("Invoices") ? "Search (YYYY-MM-DD)" : "Search";
            searchField.setText(placeholder);
            searchField.setForeground(Color.GRAY);
            if (searchListener != null) {
                searchListener.accept("");
            }
        }
    }

    private JTextField getSearchField() {
        // Determine placeholder based on active screen
        String placeholder = activeScreen.equals("Invoices") ? "Search (YYYY-MM-DD)" : "Search";
        JTextField searchField = new JTextField(20);
        searchField.setText(placeholder);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(Color.WHITE);
        searchField.setSelectionColor(new Color(184, 207, 229));
        searchField.setSelectedTextColor(Color.BLACK);
        searchField.setCaretColor(Color.BLACK);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 255), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                String currentPlaceholder = activeScreen.equals("Invoices") ? "Search (YYYY-MM-DD)" : "Search";
                if (searchField.getText().equals(currentPlaceholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent evt) {
                // Use invokeLater to allow other UI interactions (like spinner clicks) to complete first
                SwingUtilities.invokeLater(() -> {
                    if (searchField.getText().isEmpty()) {
                        String currentPlaceholder = activeScreen.equals("Invoices") ? "Search (YYYY-MM-DD)" : "Search";
                        searchField.setText(currentPlaceholder);
                        searchField.setForeground(Color.GRAY);
                        // When focus is lost and we show placeholder, trigger empty search
                        if (searchListener != null) {
                            searchListener.accept("");
                        }
                    }
                });
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onChange(); }
            public void removeUpdate(DocumentEvent e) { onChange(); }
            public void changedUpdate(DocumentEvent e) { onChange(); }

            private void onChange() {
                if (searchListener != null) {
                    if (searchField.getForeground() == Color.GRAY) {
                        searchListener.accept("");
                    } else {
                        searchListener.accept(searchField.getText());
                    }
                }
            }
        });

        searchField.setOpaque(true);
        searchField.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, searchField.getWidth(), searchField.getHeight(), 20, 20);
                g2.dispose();
            }
        });
        return searchField;
    }

    private JPanel getJPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        leftPanel.setOpaque(false);

        JLabel adminLabel = new JLabel(activeScreen);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 28));
        adminLabel.setForeground(Color.BLACK);

        JButton dashboardButton = new AdminNavButton("Dashboard");
        dashboardButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) MainActivity.getInstance().showScreen(MainActivity.ADMIN_DASHBOARD_SCREEN);
        });

        JButton inventoryButton = new AdminNavButton("Inventory");
        inventoryButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) MainActivity.getInstance().showScreen(MainActivity.ADMIN_INVENTORY_SCREEN);
        });

        JButton invoicesButton = new AdminNavButton("Invoices");
        invoicesButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) MainActivity.getInstance().showScreen(MainActivity.ADMIN_INVOICES_SCREEN);
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

    private static class AdminNavButton extends JButton {
        public AdminNavButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setForeground(Color.BLACK);
            setBackground(new Color(70, 130, 180));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { setForeground(Color.WHITE); }
                public void mouseExited(java.awt.event.MouseEvent evt) { setForeground(Color.BLACK); }
            });
        }
    }
}