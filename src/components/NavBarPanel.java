package components;

import java.awt.*;

import javax.swing.*;
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

public class NavBarPanel extends JPanel{
    public NavBarPanel() {
        setLayout(new BorderLayout()); //https://www.geeksforgeeks.org/java/java-awt-borderlayout-class/
        setBackground(new Color(130, 170, 255)); // Light blue color
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left section with User and navigation items
        JPanel leftPanel = getJPanel();

        // Right section with search bar and close button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Search field
        JTextField searchField = getSearchField();


        rightPanel.add(searchField);

        // Add panels to nav panel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private static JTextField getSearchField() {
        JTextField searchField = new JTextField("search", 20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBackground(new Color(200, 220, 255));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 255), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

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

    private static JPanel getJPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        leftPanel.setOpaque(false);

        // User label (bold and larger)
        JLabel userLabel = new JLabel("User");
        userLabel.setFont(new Font("Arial", Font.BOLD, 28));
        userLabel.setForeground(Color.BLACK);

        // Navigation buttons
        JButton homeButton = new NavButton("Home");
        JButton invoicesButton = new NavButton("Invoices");
        JButton summaryButton = new NavButton("Summary");

        leftPanel.add(userLabel);
        leftPanel.add(homeButton);
        leftPanel.add(invoicesButton);
        leftPanel.add(summaryButton);
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