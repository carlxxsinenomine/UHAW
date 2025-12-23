package screens;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.MainActivity;

public class MainMenuScreen extends JPanel {

    // Brand Colors
    private static final Color PRIMARY_BLUE = new Color(130, 170, 255);
    private static final Color TEXT_DARK = new Color(50, 50, 50);

    public MainMenuScreen() {
        setLayout(new GridLayout(1, 2)); // Split 50/50


        // LEFT PANEL (Info / About Side)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PRIMARY_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        //Add Content Elements
        JTextArea titleLabel = createTextComponent(
                "UHAW: Unified Hardware for Automated Wholesale/Retail",
                new Font("Arial", Font.BOLD, 42)
        );

        // Subtitle: POS System
        JTextArea subTitleLabel = createTextComponent(
                "A point-of-sale (POS) system",
                new Font("Arial", Font.ITALIC, 24)
        );
        subTitleLabel.setForeground(new Color(230, 240, 255)); // Lighter blue/white

        // Description
        String descText = "– A hardware-software solution that processes sales " +
                "transactions, and generates sales invoice – supporting retail operations.";
        JTextArea descLabel = createTextComponent(
                descText,
                new Font("Arial", Font.PLAIN, 18)
        );
        descLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftPanel.add(Box.createVerticalGlue()); // Push content to center
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(subTitleLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(descLabel);
        leftPanel.add(Box.createVerticalGlue());

        // RIGHT PANEL (Navigation Buttons)
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

        // --- User Panel Button ---
        menuContainer.add(createStyledMenuButton("User Panel", "Create & View Invoices", () ->
                MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        // --- Admin Panel Button ---
        menuContainer.add(createStyledMenuButton("Admin Panel", "Inventory & Users", () ->
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_LOGIN_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        // --- About Button (Uniform Style) ---
        menuContainer.add(createStyledMenuButton("About", "Team & Version Info", () ->
                showAboutDialog()));

        menuContainer.add(Box.createVerticalStrut(15));

        // --- Exit Button ---
        menuContainer.add(createStyledMenuButton("Exit System", "Close Application", () ->
                System.exit(0)));

        rightPanel.add(menuContainer);

        // Add split panels to main layout
        add(leftPanel);
        add(rightPanel);
    }

    /**
     * Helper to create styled, wrapped text areas that look like labels.
     */
    private JTextArea createTextComponent(String text, Font font) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(font);
        textArea.setForeground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        return textArea;
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

    /**
     * Shows the About Dialog with Member names.
     */
    private void showAboutDialog() {
        String message = "<html><body style='width: 250px;'>" +
                "<h2 style='color: #82AAFF;'>UHAW System</h2>" +
                "<p><b>Developers (Members):</b></p>" +
                "<ul>" +
                "<li>Bañares, Peter Andrew</li>" +
                "<li>Margarata, Sean Eric</li>" +
                "<li>Muñoz, Carl Johannes</li>" +
                "<li>Santos, Gebhel Anselm</li>" +
                "</ul>" +
                "<br>" +
                "<p><i>Bicol University - College of Science</i><br>" +
                "CS 108: Object Oriented Programming</p>" +
                "</body></html>";

        JOptionPane.showMessageDialog(this, message, "About UHAW", JOptionPane.INFORMATION_MESSAGE);
    }
}