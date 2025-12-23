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
        
        // Set initial placeholder based on screen
        updateSearchPlaceholder();
    }

    /**
     * Updates the search field placeholder based on the active screen.
     */
    private void updateSearchPlaceholder() {
        String placeholder;
        if (activeScreen.equals("Purchase History")) {
            placeholder = "Search (YYYY-MM-DD)";
        } else {
            placeholder = "Search";
        }
        
        if (searchField != null && searchField.getForeground() == Color.GRAY) {
            searchField.setText(placeholder);
        }
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
            String placeholder = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
            searchField.setText(placeholder);
            searchField.setForeground(Color.GRAY);
            // Trigger the listener to clear any filters
            if (searchListener != null) {
                searchListener.accept("");
            }
        }
    }

    private JTextField getSearchField() {
        String placeholder = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
        JTextField searchField = new JTextField(20);
        searchField.setText(placeholder);
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
                String currentPlaceholder = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
                if (searchField.getText().equals(currentPlaceholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    String currentPlaceholder = activeScreen.equals("Purchase History") ? "Search (YYYY-MM-DD)" : "Search";
                    searchField.setText(currentPlaceholder);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Add DocumentListener to detect typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { onChange(); }
            public void removeUpdate(DocumentEvent e) { onChange(); }
            public void changedUpdate(DocumentEvent e) { onChange(); }

            private void onChange() {
                if (searchListener != null) {
                    // Ignore placeholder text
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