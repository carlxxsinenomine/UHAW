package components;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.*;
import main.MainActivity;

public class NavBarPanel extends JPanel {
    private String activeScreen;
    private Consumer<String> searchListener; // Listener for search text
    private JTextField searchField;
    private String placeholderText; // Store the placeholder text

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
     */
    public void setSearchListener(Consumer<String> listener) {
        this.searchListener = listener;
    }

    /**
     * Resets the search field to its placeholder state.
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
     * Gets the actual search text (excluding placeholder).
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