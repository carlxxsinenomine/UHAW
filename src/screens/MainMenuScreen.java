package screens;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.AppConstants;
import main.MainActivity;

/**
 * MainMenuScreen - The initial landing page for the UHAW application.
 * 
 * This screen serves as the entry point to the application, presenting users with a welcoming
 * interface and navigation options to the main system components. It is divided into two sections:
 * a left info panel explaining the system, and a right navigation panel with action buttons.
 * 
 * Features:
 * - Split-panel layout: Left side displays system information and branding
 * - Navigation Buttons: User Panel, Admin Panel, About, and Exit options
 * - Hover Effects: Interactive buttons with visual feedback on mouse over
 * - Professional Design: Uses brand colors and consistent typography
 * 
 * Navigation Options:
 * - User Panel: Access to customer purchase creation and history
 * - Admin Panel: Access to admin login screen for inventory and invoice management
 * - About: Display team information and system details
 * - Exit: Gracefully close the application
 */
public class MainMenuScreen extends JPanel {

    /**
     * Constructs the MainMenuScreen with a split-panel layout.
     * 
     * Layout Structure:
     * - Left Panel (50%): System branding, title, subtitle, and description
     * - Right Panel (50%): Navigation menu with styled buttons
     * 
     * The constructor initializes all UI components, sets up styling using AppConstants,
     * and configures button click listeners for navigation.
     */
    public MainMenuScreen() {
        setLayout(new GridLayout(1, 2)); // Split 50/50


        // LEFT PANEL (Info / About Side)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(AppConstants.PRIMARY_BLUE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        //Add Content Elements
        JTextArea titleLabel = createTextComponent(
                "UHAW: Unified Hardware for Automated Wholesale/Retail",
                AppConstants.FONT_TITLE_LARGE
        );

        // Subtitle: POS System
        JTextArea subTitleLabel = createTextComponent(
                "A point-of-sale (POS) system",
                AppConstants.FONT_SUBTITLE
        );
        subTitleLabel.setForeground(AppConstants.TEXT_LIGHT); // Lighter blue/white

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
        menuTitle.setFont(AppConstants.FONT_TITLE_LARGE_REGULAR);
        menuTitle.setForeground(AppConstants.TEXT_DARK);
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
     * Helper method to create styled text areas that function as non-editable labels.
     * 
     * This method creates text areas with word wrapping enabled, suitable for displaying
     * multi-line text content. The text areas are:
     * - Non-editable and non-focusable
     * - Transparent background to blend with parent panels
     * - Set to left alignment for consistent text layout
     * - Fully configurable with custom fonts and colors
     * 
     * @param text The text content to display
     * @param font The font to apply to the text
     * @return A configured JTextArea with the specified text and font styling
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
     * Creates a sophisticated menu button with dynamic hover effects.
     * 
     * This method constructs a styled button with:
     * - Title and subtitle labels for hierarchical information
     * - Arrow icon that changes color on hover
     * - Smooth hover effects with background and border color transitions
     * - Hand cursor to indicate interactivity
     * - Click action that executes the provided Runnable
     * 
     * Button Design:
     * - Dimensions: 300x70 pixels
     * - Normal state: White background with light gray border
     * - Hover state: Light blue background with primary blue border
     * - Layout: BorderLayout with title/subtitle on left and arrow on right
     * 
     * @param title The main button title (displayed in bold)
     * @param subtitle The secondary description text (displayed in gray)
     * @param action The Runnable to execute when the button is clicked
     * @return A configured JButton with hover effects and action listener
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

    /**
     * Displays an informational dialog box about the UHAW system.
     * 
     * This dialog presents:
     * - System name and branding
     * - List of developer team members
     * - Course and university information
     * 
     * The dialog is formatted as HTML for better visual presentation,
     * making it easy to read the team information and project context.
     * 
     * Team members displayed:
     * - Bañares, Peter Andrew
     * - Margarata, Sean Eric
     * - Muñoz, Carl Johannes
     * - Santos, Gebhel Anselm
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