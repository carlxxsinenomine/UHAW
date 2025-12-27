package components;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.*;
import main.MainActivity;

/**
 * NavBarPanel - Navigation bar component for the User-facing screens.
 * 
 * This component provides a consistent navigation header for user screens (UserScreen, InvoiceScreen).
 * It displays the current screen name and provides functionality for searching and navigating back
 * to the main menu.
 * 
 * Features:
 * - Screen identifier display: Shows which screen the user is currently on
 * - Search field: For filtering inventory items or invoices by name or date
 * - Main Menu button: Quick navigation back to the main menu
 * - Search listener: Notifies the parent screen of search text changes
 * - Placeholder management: Shows helpful hint text when search field is empty
 * 
 * Design:
 * - Color: Primary blue background (AppConstants.PRIMARY_BLUE)
 * - Layout: BorderLayout with left panel (title) and right panel (search + menu button)
 * - Styling: Consistent with the UHAW design system
 * 
 * Search Behavior:
 * - Date-based filtering: Purchase History shows "Search (YYYY-MM-DD)" placeholder
 * - Text-based filtering: Other screens show generic "Search" placeholder
 * - Real-time updates: Search listener fires on each keystroke
 */
public class NavBarPanel extends JPanel {
    private String activeScreen;
    private Consumer<String> searchListener; // Listener for search text
    private JTextField searchField;
    private String placeholderText; // Store the placeholder text

    /**
     * Constructs a NavBarPanel for a specific screen.
     * 
     * Initializes the navigation bar with:
     * - The screen name displayed on the left
     * - A search field with context-aware placeholder text
     * - A Main Menu navigation button
     * 
     * The search field placeholder changes based on the screen:
     * - "Search (YYYY-MM-DD)" for Purchase History screens (date-based filtering)
     * - "Search" for other screens (text-based filtering)
     * 
     * @param activeScreen The name of the current screen (used for display and search context)
     */
    public NavBarPanel(String activeScreen) {
        this.activeScreen = activeScreen;
        setLayout(new BorderLayout());
        setBackground(new Color(130, 170, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel leftPanel = getJPanel();

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Arial", Font.BOLD, 14));
        mainMenuButton.setBackground(new Color(220, 220, 220));
        mainMenuButton.setForeground(Color.BLACK);
        mainMenuButton.setFocusPainted(false);
        mainMenuButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        mainMenuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainMenuButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.MAIN_MENU_SCREEN);
            }
        });

        searchField = getSearchField();

        rightPanel.add(searchField);
        rightPanel.add(mainMenuButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Sets a listener that triggers whenever the search text changes.
     * 
     * The listener is called on every keystroke, allowing the parent screen
     * to perform real-time filtering or searching. The listener receives the
     * current search text, excluding the placeholder text.
     * 
     * Common Usage:
     * <pre>
     * navBarPanel.setSearchListener(searchText -> {
     *     // Filter inventory or invoices based on searchText
     *     refreshTableRows();
     * });
     * </pre>
     * 
     * @param listener A Consumer that accepts the current search text
     */
    public void setSearchListener(Consumer<String> listener) {
        this.searchListener = listener;
    }

    /**
     * Resets the search field to its placeholder state.
     * 
     * This method is typically called when:
     * - The user navigates away from and back to the screen
     * - The parent screen resets its filters and display
     * - Data is reloaded or refreshed
     * 
     * The reset action:
     * - Clears the search text
     * - Shows the context-appropriate placeholder text
     * - Fires the search listener with an empty string to clear filters
     */
    public void resetSearch() {
        if (searchField != null) {
            placeholderText = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
            searchField.setText(placeholderText);
            searchField.setForeground(Color.GRAY);
            // Trigger the listener to clear any filters
            if (searchListener != null) {
                searchListener.accept("");
            }
        }
    }

    /**
     * Gets the actual search text (excluding placeholder text).
     * 
     * This method distinguishes between:
     * - Placeholder text: Returns empty string when placeholder is displayed (gray colored text)
     * - Actual search text: Returns the user-entered text when user is actively searching
     * 
     * @return The actual search text, or empty string if no real search text has been entered
     */
    public String getSearchText() {
        if (searchField != null && searchField.getForeground() == Color.GRAY) {
            return ""; // Return empty if showing placeholder
        }
        return searchField != null ? searchField.getText() : "";
    }

    private JTextField getSearchField() {
        placeholderText = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
        JTextField searchField = new JTextField(20);
        searchField.setText(placeholderText);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(Color.WHITE);
        searchField.setSelectionColor(new Color(184, 207, 229)); // Light blue selection
        searchField.setSelectedTextColor(Color.BLACK);
        searchField.setCaretColor(Color.BLACK);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 255), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (searchField.getText().equals(placeholderText)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent evt) {
                // Use invokeLater to allow other UI interactions (like spinner clicks) to complete first
                SwingUtilities.invokeLater(() -> {
                    if (searchField.getText().isEmpty()) {
                        searchField.setText(placeholderText);
                        searchField.setForeground(Color.GRAY);
                        // When focus is lost and we show placeholder, trigger empty search
                        if (searchListener != null) {
                            searchListener.accept("");
                        }
                    }
                });
            }
        });

        // Add DocumentListener to detect typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onChange(); }
            public void removeUpdate(DocumentEvent e) { onChange(); }
            public void changedUpdate(DocumentEvent e) { onChange(); }

            private void onChange() {
                if (searchListener != null) {
                    // Only trigger if not showing placeholder text
                    if (searchField.getForeground() != Color.GRAY) {
                        searchListener.accept(searchField.getText());
                    } else {
                        searchListener.accept(""); // Send empty string when showing placeholder
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

        JLabel userLabel = new JLabel(activeScreen);
        userLabel.setFont(new Font("Arial", Font.BOLD, 28));
        userLabel.setForeground(Color.BLACK);

        JButton homeButton = new NavButton("Home");
        homeButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN);
            }
        });

        JButton purchaseHistoryButton = new NavButton("Purchase History");
        purchaseHistoryButton.addActionListener(e -> {
            if (MainActivity.getInstance() != null) {
                MainActivity.getInstance().showScreen(MainActivity.INVOICE_SCREEN);
            }
        });

        leftPanel.add(userLabel);
        leftPanel.add(homeButton);
        leftPanel.add(purchaseHistoryButton);

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

    // Inner class for styled navigation buttons
    private static class NavButton extends JButton {
        public NavButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setForeground(Color.BLACK); // Set text to BLACK as requested
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setForeground(Color.WHITE); // Hover effect
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setForeground(Color.BLACK); // Back to Black
                }
            });
        }
    }
}