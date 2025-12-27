package screens;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import main.MainActivity;

public class AdminLoginScreen extends JPanel {

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

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 35));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(130, 170, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(200, 45));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Back button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(Color.WHITE);
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
     * Authenticates admin credentials from CSV file
     * @param username The username to check
     * @param password The password to check
     * @return true if credentials are valid, false otherwise
     */
    private boolean authenticateAdmin(String username, String password) {
        String line;
        String csvSplitBy = ",";
        
        try {
            // Use ClassLoader to get the file from the package
            InputStream is = getClass().getClassLoader()
                .getResourceAsStream("screens/admin/credentials.csv");
            
            if (is == null) {
                System.err.println("Credentials file not found in resources");
                return false;
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            // Skip header line if exists
            String header = br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(csvSplitBy);
                
                if (credentials.length >= 2) {
                    String csvUsername = credentials[0].trim();
                    String csvPassword = credentials[1].trim();
                    
                    if (csvUsername.equals(username) && csvPassword.equals(password)) {
                        br.close();
                        return true;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error reading credentials file: " + e.getMessage());
        }
        
        return false;
    }
}