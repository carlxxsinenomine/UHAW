package screens;

import javax.swing.*;
import java.awt.*;
import main.MainActivity;

/**
 * AdminLoginScreen provides authentication for admin access.
 * Displays a login form with username and password fields.
 *
 * @author Your Name
 * @version 1.0
 */
public class AdminLoginScreen extends JPanel {

    /**
     * Constructor that initializes and displays the AdminLoginScreen.
     */
    public AdminLoginScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        // Login card panel
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        loginCard.setPreferredSize(new Dimension(400, 450));

        // Title
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(130, 170, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 50, 12, 50));
        loginButton.setMaximumSize(new Dimension(200, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Back to User button
        JButton backButton = new JButton("Back to User Side");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(130, 170, 255));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Error message label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Simple authentication (in real app, use proper security)
            if (username.equals("admin") && password.equals("admin123")) {
                errorLabel.setText(" ");
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_DASHBOARD_SCREEN);
            } else {
                errorLabel.setText("Invalid username or password");
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            MainActivity.getInstance().showScreen(MainActivity.USER_SCREEN);
        });

        // Add components to login card
        loginCard.add(titleLabel);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(usernameLabel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(usernameField);
        loginCard.add(passwordLabel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(errorLabel);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(loginButton);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(backButton);

        contentPanel.add(loginCard);
        add(contentPanel, BorderLayout.CENTER);
    }
}
