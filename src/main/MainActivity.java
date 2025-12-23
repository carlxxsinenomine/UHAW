package main;

import java.awt.*;
import javax.swing.*;
import screens.*;

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

    public MainActivity() {
        instance = this;

        setTitle("Invoice Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1200, 600));
        setLocationRelativeTo(null);

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

        // Add card panel to frame
        add(cardPanel);

        // --- 5. Show Initial Screen (Main Menu) ---
        showScreen(MAIN_MENU_SCREEN);

        setVisible(true);
    }

    /**
     * Triggers a data reload on all key screens.
     * Called when inventory is modified by Admin or User.
     */
    public void refreshAllScreens() {
        if (userScreen != null) userScreen.refreshData();
        if (adminInventoryScreen != null) adminInventoryScreen.refreshData();
    }

    public void showScreen(String screenName) {
        cardLayout.show(cardPanel, screenName);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainActivity());
    }
}