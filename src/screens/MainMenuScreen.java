// This is the initial launch page
// Divided into two sections: left info panel and right navigation panel (admin, user, about ,exit)

package screens;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.AppConstants;
import main.MainActivity;


public class MainMenuScreen extends JPanel {

    // Main menu screen constructor
    public MainMenuScreen() {
        setLayout(new GridLayout(1, 2)); // Split 50/50


        // LEFT PANEL (General Info and Description)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(AppConstants.PRIMARY_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Add Content Elements
        JTextArea titleLabel = createTextComponent(
                "UHAW: Unified Hardware for Automated Wholesale/Retail",
                AppConstants.FONT_TITLE_LARGE
        );

        // Description
        String descText = "– A hardware-software solution that processes sales " +
                "transactions, and generates sales invoice – supporting retail operations.";
        JTextArea descLabel = createTextComponent(
                descText,
                AppConstants.FONT_BODY_LARGE
        );
        descLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        leftPanel.add(Box.createVerticalGlue()); // Push content to center
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(10));
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
        menuTitle.setFont(AppConstants.FONT_TITLE_LARGE_REGULAR);
        menuTitle.setForeground(AppConstants.TEXT_DARK);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        menuContainer.add(menuTitle);

        // User Panel Button
        menuContainer.add(createStyledMenuButton("User Panel", "Create & View Invoices", () ->
                MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        // Admin Panel Button
        menuContainer.add(createStyledMenuButton("Admin Panel", "Inventory & Users", () ->
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_LOGIN_SCREEN)));

        menuContainer.add(Box.createVerticalStrut(15));

        // About Button
        menuContainer.add(createStyledMenuButton("About", "Team & Version Info", () ->
                showAboutDialog()));

        menuContainer.add(Box.createVerticalStrut(15));

        // Exit Button
        menuContainer.add(createStyledMenuButton("Exit System", "Close Application", () ->
                System.exit(0)));

        rightPanel.add(menuContainer);

        // Add split panels to main layout
        add(leftPanel);
        add(rightPanel);
    }

    /**
     * Helper method to create styled text components
     * @param text
     * @param font
     * @return A configured JTextArea
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
     * Menu button method constructor 
     * @param title
     * @param subtitle
     * @param action
     * @return A configured JButton
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
        titleLbl.setForeground(AppConstants.TEXT_DARK);
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
                button.setBorder(BorderFactory.createLineBorder(AppConstants.PRIMARY_BLUE, 1));
                arrowLbl.setForeground(AppConstants.PRIMARY_BLUE);
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

    // Displays about dialog
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