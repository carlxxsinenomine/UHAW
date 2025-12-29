package screens;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import main.AppConstants;
import main.MainActivity;

// The authentication gateway for administrative access
// Provides secure login functionality for administrators to access admin dashboard and tools
public class AdminLoginScreen extends JPanel {

    // Constructor for AdminLoginScreen - creates login interface with form fields and validation
    public AdminLoginScreen() {
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_LIGHT_GRAY);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        // Login card panel
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(AppConstants.BG_WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.BORDER_LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        loginCard.setPreferredSize(new Dimension(400, 450));

        // Title
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(AppConstants.FONT_TITLE_LARGE_REGULAR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(AppConstants.FONT_BODY_REGULAR);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(AppConstants.FONT_BODY_REGULAR);
        usernameField.setMaximumSize(new Dimension(300, 35));

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(AppConstants.FONT_BODY_REGULAR);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(AppConstants.FONT_BODY_REGULAR);
        passwordField.setMaximumSize(new Dimension(300, 35));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(AppConstants.PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(200, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Back button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(AppConstants.FONT_BODY_REGULAR);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(AppConstants.BG_WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Error message label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login action with CSV authentication
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateAdmin(username, password)) {
                errorLabel.setText(" ");
                usernameField.setText("");
                passwordField.setText("");
                MainActivity.getInstance().showScreen(MainActivity.ADMIN_DASHBOARD_SCREEN);
            } else {
                errorLabel.setText("Invalid username or password");
            }
        });

        // Back action
        backButton.addActionListener(e -> {
            errorLabel.setText(" ");
            usernameField.setText("");
            passwordField.setText("");
            MainActivity.getInstance().showScreen(MainActivity.MAIN_MENU_SCREEN);
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

    /**
     * Authenticates admin credentials by checking against the CSV file.
     * 
     * Credential File:
     * - Location: src/screens/admin/credentials.csv (in classpath resources)
     * - Format: Each line contains "username,password"
     * - Example: "admin,password123"
     * 
     * Authentication Process:
     * 1. Loads credentials.csv from classpath resources
     * 2. Iterates through each line to find matching credentials
     * 3. Performs exact string matching (case-sensitive)
     * 4. Returns true if exact match found, false otherwise
     * 
     * Error Handling:
     * - Returns false if CSV file not found
     * - Continues checking other credentials if parsing error occurs
     * - Logs errors to standard error stream for debugging
     * 
     * @param username The username entered by the admin
     * @param password The password entered by the admin
     * @return true if credentials match an entry in credentials.csv, false otherwise
     */
    private boolean authenticateAdmin(String username, String password) {
        String line;
        String csvSplitBy = ",";
        
        // This looks for the file relative to the compiled classes
        // Note the leading slash: /screens/admin/credentials.csv
        InputStream is = getClass().getResourceAsStream("/screens/admin/credentials.csv");

        if (is == null) {
            System.err.println("Could not find credentials.csv in classpath!");
            return false;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            // Skip header line if exists
            // br.readLine(); // Uncomment this if you have a header like "user,pass"

            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(csvSplitBy);

                if (credentials.length >= 2) {
                    String csvUsername = credentials[0].trim();
                    String csvPassword = credentials[1].trim();

                    if (csvUsername.equals(username) && csvPassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}