package main;

import java.awt.*;
import javax.swing.*;
import screens.*;

/**
 * MainActivity - The main application window and screen management hub.
 * 
 * This class serves as the central controller for the entire UHAW (Unified Hardware for Automated Wholesale)
 * point-of-sale system. It manages screen navigation using a CardLayout pattern, allowing users to switch
 * between different screens (Main Menu, User Panel, Admin Dashboard, etc.) seamlessly.
 * 
 * Key Responsibilities:
 * - Initialize and display the application window
 * - Manage screen transitions using CardLayout
 * - Maintain references to screens that need data refresh functionality
 * - Provide centralized access to screen identifiers for navigation
 * - Coordinate data refresh across multiple screens when inventory changes
 * 
 * Screen Categories:
 * - Main Menu: Entry point for the application
 * - User Side: Customer purchase creation and history viewing
 * - Admin Side: Inventory management, invoice tracking, and system administration
 */
public class MainActivity extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- Screen References (Needed for Auto-Refresh) ---
    private UserScreen userScreen;
    private AdminInventoryScreen adminInventoryScreen;
    private InvoiceScreen invoiceScreen;

    // --- Main Menu Identifier ---
    public static final String MAIN_MENU_SCREEN = "MAIN_MENU_SCREEN";

    // --- User Side Identifiers ---
    public static final String USER_SCREEN = "USER_SCREEN";
    // This is the specific ID your User buttons are looking for:
    public static final String INVOICE_SCREEN = "INVOICE_SCREEN";
    public static final String SUMMARY_SCREEN = "SUMMARY_SCREEN";

    // Kept this variable definition as requested, but we use INVOICE_SCREEN for mapping
    public static final String PURCHASE_HISTORY_SCREEN = "PURCHASE_HISTORY_SCREEN";

    // --- Admin Side Identifiers ---
    public static final String ADMIN_LOGIN_SCREEN = "ADMIN_LOGIN_SCREEN";
    public static final String ADMIN_DASHBOARD_SCREEN = "ADMIN_DASHBOARD_SCREEN";
    public static final String ADMIN_INVENTORY_SCREEN = "ADMIN_INVENTORY_SCREEN";
    public static final String ADMIN_INVOICES_SCREEN = "ADMIN_INVOICES_SCREEN";
    public static final String ADMIN_USERS_SCREEN = "ADMIN_USERS_SCREEN";

    private static MainActivity instance;

    /**
     * Constructs the MainActivity and initializes the application window.
     * 
     * This constructor:
     * - Sets up the main window properties (title, size, layout)
     * - Creates and initializes all screen panels
     * - Organizes screens using CardLayout for efficient switching
     * - Sets the Main Menu as the initial screen
     * - Maximizes the window for full-screen display
     * 
     * Window Configuration:
     * - Title: "Invoice Management System"
     * - Size: 1400x800 initial, with 1850x700 minimum
     * - Layout: BorderLayout with CardLayout for screen management
     * - State: Maximized on startup
     */
    public MainActivity() {
        instance = this;

        setTitle("Invoice Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set a larger initial size and minimum size to ensure everything fits
        setSize(1400, 800);
        setMinimumSize(new Dimension(1850, 700)); // Increased minimum size
        setLocationRelativeTo(null);

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // --- 1. Create and Add Main Menu ---
        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        cardPanel.add(mainMenuScreen, MAIN_MENU_SCREEN);

        // --- 2. Initialize Screens (As Class Variables for Refreshing) ---
        userScreen = new UserScreen();
        adminInventoryScreen = new AdminInventoryScreen();
        invoiceScreen = new InvoiceScreen();

        // Other screens
        AdminLoginScreen adminLoginScreen = new AdminLoginScreen();
        AdminDashboardScreen adminDashboardScreen = new AdminDashboardScreen();
        AdminInvoicesScreen adminInvoicesScreen = new AdminInvoicesScreen();

        // --- 3. Add User Side Screens to CardPanel ---
        cardPanel.add(userScreen, USER_SCREEN);

        // FIX: We add InvoiceScreen using the "INVOICE_SCREEN" ID so the button finds it.
        cardPanel.add(invoiceScreen, INVOICE_SCREEN);

        // --- 4. Add Admin Side Screens to CardPanel ---
        cardPanel.add(adminLoginScreen, ADMIN_LOGIN_SCREEN);
        cardPanel.add(adminDashboardScreen, ADMIN_DASHBOARD_SCREEN);
        cardPanel.add(adminInventoryScreen, ADMIN_INVENTORY_SCREEN);
        cardPanel.add(adminInvoicesScreen, ADMIN_INVOICES_SCREEN);

        // Add card panel to frame with some padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        // --- 5. Show Initial Screen (Main Menu) ---
        showScreen(MAIN_MENU_SCREEN);

        // Center the window on screen
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Ensure proper sizing
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Triggers a data reload on all key screens.
     * Called when inventory is modified by Admin or User.
     * 
     * This method ensures that any changes made in one screen are immediately reflected
     * in all other screens. For example, when an admin adds a new inventory item, the
     * UserScreen will refresh to display the new item.
     * 
     * Screens Refreshed:
     * - UserScreen: Reloads inventory data for purchase creation
     * - AdminInventoryScreen: Reloads inventory items from JSON file
     * - InvoiceScreen: Reloads user's purchase history
     */
    public void refreshAllScreens() {
        if (userScreen != null) userScreen.refreshData();
        if (adminInventoryScreen != null) adminInventoryScreen.refreshData();
        if (invoiceScreen != null) invoiceScreen.refreshData();
    }

    /**
     * Displays a specific screen in the CardLayout container.
     * 
     * This method performs screen transition by showing the requested screen panel
     * and automatically refreshing data for screens that need fresh information
     * (e.g., when returning to a screen, its data is reloaded).
     * 
     * Auto-Refresh Behavior:
     * - USER_SCREEN: Reloads inventory and clears search filters
     * - INVOICE_SCREEN: Reloads purchase history from files
     * - ADMIN_INVENTORY_SCREEN: Reloads inventory from JSON file
     * 
     * @param screenName The identifier of the screen to display (use the constants defined in this class)
     * 
     * @see #USER_SCREEN
     * @see #INVOICE_SCREEN
     * @see #ADMIN_INVENTORY_SCREEN
     * @see #MAIN_MENU_SCREEN
     * @see #ADMIN_DASHBOARD_SCREEN
     * @see #ADMIN_LOGIN_SCREEN
     * @see #ADMIN_INVOICES_SCREEN
     */
    public void showScreen(String screenName) {
        cardLayout.show(cardPanel, screenName);
        
        // Refresh the screen when shown
        switch (screenName) {
            case USER_SCREEN:
                if (userScreen != null) userScreen.refreshData();
                break;
            case INVOICE_SCREEN:
                if (invoiceScreen != null) invoiceScreen.refreshData();
                break;
            case ADMIN_INVENTORY_SCREEN:
                if (adminInventoryScreen != null) adminInventoryScreen.refreshData();
                break;
        }
    }

    /**
     * Retrieves the singleton instance of MainActivity.
     * 
     * This method provides global access to the MainActivity instance throughout the application,
     * allowing other components (screens, panels, dialogs) to call methods like showScreen() or refreshAllScreens().
     * 
     * @return The singleton MainActivity instance, or null if not yet initialized
     */
    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * Main entry point for the UHAW application.
     * 
     * This method uses SwingUtilities.invokeLater() to ensure that all GUI initialization
     * happens on the Event Dispatch Thread (EDT), which is the proper way to handle Swing
     * components and prevent threading issues.
     * 
     * @param args Command line arguments (not currently used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainActivity());
    }
}