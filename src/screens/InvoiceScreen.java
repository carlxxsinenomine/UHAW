package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class InvoiceScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable purchaseTable;
    private String currentSearchText = "";
    private NavBarPanel navBarPanel; // Store reference to nav bar

    /**
     * Constructor that initializes and displays the PurchaseHistoryScreen.
     * Sets up the navigation bar, title, and purchase history table.
     */
    public InvoiceScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar with search listener
        navBarPanel = new NavBarPanel("Purchase History");
        
        // Title and action buttons panel
        JPanel topPanel = createTopPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Set search listener AFTER table is created
        navBarPanel.setSearchListener(text -> {
            this.currentSearchText = text.toLowerCase().trim();
            loadPurchasesFromFolder();
        });

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        // Add component listener to detect when screen becomes visible
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                resetSearch();
            }
        });
    }
    
    /**
     * Resets the search when screen becomes visible
     */
    private void resetSearch() {
        if (navBarPanel != null) {
            navBarPanel.resetSearch();
        }
        currentSearchText = "";
        loadPurchasesFromFolder();
    }

    /**
     * Creates the top panel with title and search functionality.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("My Purchase History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        return topPanel;
    }

    /**
     * Creates the table panel displaying all purchases.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));

        String[] columnNames = {"Purchase ID", "Customer Name", "Items Count", "Date", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load actual purchase data from files
        loadPurchasesFromFolder();

        purchaseTable = new JTable(tableModel);
        purchaseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        purchaseTable.setRowHeight(40);
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        purchaseTable.getTableHeader().setBackground(new Color(200, 200, 200));
        purchaseTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(130, 170, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshPurchases());

        JButton viewButton = new JButton("View Purchase");
        viewButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewButton.setBackground(new Color(34, 139, 34));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> viewSelectedPurchase());

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    /**
     * Loads all purchases from the invoices folder with search filter.
     */
    private void loadPurchasesFromFolder() {
        if (tableModel == null) return; // Guard against null tableModel
        
        tableModel.setRowCount(0);
        File invoicesDir = new File("invoices");
        
        if (!invoicesDir.exists()) {
            invoicesDir = new File("src/main/invoices");
        }
        
        if (!invoicesDir.exists() || !invoicesDir.isDirectory()) {
            return;
        }
        
        File[] invoiceFiles = invoicesDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (invoiceFiles == null || invoiceFiles.length == 0) {
            return;
        }
        
        Arrays.sort(invoiceFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        for (File file : invoiceFiles) {
            try {
                PurchaseData data = parsePurchaseFile(file);
                if (data != null) {
                    // Apply search filter - search by Purchase ID, Customer Name, or Date
                    if (currentSearchText.isEmpty() || 
                        data.purchaseId.toLowerCase().contains(currentSearchText) ||
                        data.customerName.toLowerCase().contains(currentSearchText) ||
                        data.date.contains(currentSearchText)) {
                        
                        tableModel.addRow(new Object[]{
                            data.purchaseId,
                            data.customerName,
                            data.itemsCount,
                            data.date,
                            String.format("PHP %,.2f", data.totalAmount)
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading purchase: " + file.getName() + " - " + e.getMessage());
            }
        }
    }

    /**
     * Parses a purchase file and extracts relevant data.
     */
    private PurchaseData parsePurchaseFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            PurchaseData data = new PurchaseData();
            String line;
            int lineNum = 0;
            int itemsCount = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                if (lineNum == 3 && line.startsWith("INV")) {
                    data.purchaseId = line;
                }
                else if (line.contains("Billed to")) {
                    String nextLine = reader.readLine();
                    if (nextLine != null) {
                        String[] parts = nextLine.trim().split("\\s{2,}");
                        if (parts.length > 0) {
                            data.customerName = parts[0].trim();
                        }
                    }
                }
                else if (line.matches("^\\s*[A-Za-z0-9\\s.-]+\\s+\\d+\\s+[0-9,\\.]+\\s+PHP.*")) {
                    // Count items by matching item rows
                    itemsCount++;
                }
                else if (line.contains("Amount due")) {
                    String amountStr = line.substring(line.indexOf("Amount due") + 10).trim();
                    amountStr = amountStr.replace("PHP", "").replace(",", "").trim();
                    try {
                        data.totalAmount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        data.totalAmount = 0.0;
                    }
                }
            }
            
            data.itemsCount = itemsCount;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            data.date = sdf.format(new Date(file.lastModified()));
            
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Refreshes the purchase list.
     */
    private void refreshPurchases() {
        loadPurchasesFromFolder();
        JOptionPane.showMessageDialog(this, 
            "Purchase history refreshed!",
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Views the selected purchase details.
     */
    private void viewSelectedPurchase() {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase to view.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String purchaseId = (String) tableModel.getValueAt(selectedRow, 0);
        File purchaseFile = new File("invoices", purchaseId + ".txt");
        
        if (!purchaseFile.exists()) {
            purchaseFile = new File("src/main/invoices", purchaseId + ".txt");
        }
        
        if (!purchaseFile.exists()) {
            JOptionPane.showMessageDialog(this, 
                "Purchase file not found: " + purchaseId + ".txt",
                "File Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(purchaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            JTextArea textArea = new JTextArea(content.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setCaretPosition(0);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 600));
            
            JOptionPane.showMessageDialog(this, 
                scrollPane,
                "Purchase: " + purchaseId, 
                JOptionPane.PLAIN_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error reading purchase file: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inner class to store purchase data.
     */
    private static class PurchaseData {
        String purchaseId = "";
        String customerName = "Unknown";
        String date = "";
        double totalAmount = 0.0;
        int itemsCount = 0;
    }
}