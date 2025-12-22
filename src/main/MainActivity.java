package main;

import java.awt.*;
import javax.swing.*;
import screens.*;

/**
 * MainActivity serves as the main entry point and navigation controller
 * for the invoice management application.
 * Uses CardLayout to switch between different screens.
 *
 * @author Your Name
 * @version 1.1
 */
public class MainActivity extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- NEW: Main Menu Identifier ---
    public static final String MAIN_MENU_SCREEN = "MAIN_MENU_SCREEN";

    // Screen identifiers - User Side
    public static final String USER_SCREEN = "USER_SCREEN";
    public static final String INVOICE_SCREEN = "INVOICE_SCREEN";
    public static final String SUMMARY_SCREEN = "SUMMARY_SCREEN";

    // Screen identifiers - Admin Side
    public static final String ADMIN_LOGIN_SCREEN = "ADMIN_LOGIN_SCREEN";
    public static final String ADMIN_DASHBOARD_SCREEN = "ADMIN_DASHBOARD_SCREEN";
    public static final String ADMIN_INVENTORY_SCREEN = "ADMIN_INVENTORY_SCREEN";
    public static final String ADMIN_INVOICES_SCREEN = "ADMIN_INVOICES_SCREEN";
    public static final String ADMIN_USERS_SCREEN = "ADMIN_USERS_SCREEN";

    // Singleton instance for global access
    private static MainActivity instance;

    /**
     * Constructor that initializes the MainActivity.
     * Sets up the window properties and creates all screens with CardLayout.
     */
    public MainActivity() {
        instance = this;

        setTitle("Invoice Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1200, 600));
        setLocationRelativeTo(null);

        // Initialize CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // --- 1. Create and Add Main Menu ---
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        cardPanel.add(mainMenuScreen, MAIN_MENU_SCREEN);

        // --- 2. Create and Add User Side Screens ---
        UserScreen userScreen = new UserScreen();
        InvoiceScreen invoiceScreen = new InvoiceScreen();
        SummaryScreen summaryScreen = new SummaryScreen();

        cardPanel.add(userScreen, USER_SCREEN);
        cardPanel.add(invoiceScreen, INVOICE_SCREEN);
        cardPanel.add(summaryScreen, SUMMARY_SCREEN);

        // --- 3. Create and Add Admin Side Screens ---
        AdminLoginScreen adminLoginScreen = new AdminLoginScreen();
        AdminDashboardScreen adminDashboardScreen = new AdminDashboardScreen();
        AdminInventoryScreen adminInventoryScreen = new AdminInventoryScreen();
        AdminInvoicesScreen adminInvoicesScreen = new AdminInvoicesScreen();
        AdminUsersScreen adminUsersScreen = new AdminUsersScreen();

        cardPanel.add(adminLoginScreen, ADMIN_LOGIN_SCREEN);
        cardPanel.add(adminDashboardScreen, ADMIN_DASHBOARD_SCREEN);
        cardPanel.add(adminInventoryScreen, ADMIN_INVENTORY_SCREEN);
        cardPanel.add(adminInvoicesScreen, ADMIN_INVOICES_SCREEN);
        cardPanel.add(adminUsersScreen, ADMIN_USERS_SCREEN);

        // Add card panel to frame
        add(cardPanel);

        // --- 4. Show Initial Screen (Now set to Main Menu) ---
        showScreen(MAIN_MENU_SCREEN);

        setVisible(true);
    }

    /**
     * Switches to the specified screen.
     *
     * @param screenName the identifier of the screen to display
     */
    public void showScreen(String screenName) {
        cardLayout.show(cardPanel, screenName);
    }

    /**
     * Returns the singleton instance of MainActivity.
     * Used by screens to trigger navigation.
     *
     * @return the MainActivity instance
     */
    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainActivity());
    }
}