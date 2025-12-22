package main;

import java.awt.*;
import javax.swing.*;
import screens.*;

/**
 * MainActivity serves as the main entry point and navigation controller.
 * FIXED: Mapped InvoiceScreen to 'INVOICE_SCREEN' so the User button works.
 *
 * @author Your Name
 * @version 1.6
 */
public class MainActivity extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

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

        // --- 2. Create and Add User Side Screens ---
        UserScreen userScreen = new UserScreen();

        // FIX: We add InvoiceScreen using the "INVOICE_SCREEN" ID so the button finds it.
        InvoiceScreen invoiceScreen = new InvoiceScreen();
        cardPanel.add(invoiceScreen, PURCHASE_HISTORY_SCREEN);

        // Add Summary Screen
        SummaryScreen summaryScreen = new SummaryScreen();
        cardPanel.add(summaryScreen, SUMMARY_SCREEN);

        cardPanel.add(userScreen, USER_SCREEN);

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

        // --- 4. Show Initial Screen (Main Menu) ---
        showScreen(MAIN_MENU_SCREEN);

        setVisible(true);
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