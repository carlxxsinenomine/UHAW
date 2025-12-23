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
    private NavBarPanel navBarPanel;

    public InvoiceScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        navBarPanel = new NavBarPanel("Purchase History");
        
        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();

        navBarPanel.setSearchListener(text -> {
            String searchText = text.trim();
            
            if (searchText.isEmpty() || 
                searchText.equals("Search") || 
                searchText.equals("Search (YYYY-MM-DD)")) {
                this.currentSearchText = "";
            } else {
                this.currentSearchText = searchText.toLowerCase();
            }
            loadPurchasesFromFolder();
        });

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                resetSearch();
            }
        });
    }
    
    public void resetSearch() {
        currentSearchText = "";
        if (navBarPanel != null) {
            navBarPanel.resetSearch();
        }
        loadPurchasesFromFolder();
    }

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

        loadPurchasesFromFolder();

        purchaseTable = new JTable(tableModel);
        purchaseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        purchaseTable.setRowHeight(40);
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        purchaseTable.getTableHeader().setBackground(new Color(200, 200, 200));
        purchaseTable.getTableHeader().setReorderingAllowed(false);
        
        // Set custom cell renderer to left-align all columns
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Apply left alignment to all columns
        for (int i = 0; i < purchaseTable.getColumnCount(); i++) {
            purchaseTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
        
        // Set specific column widths
        purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(180); // Purchase ID
        purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer Name
        purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Items Count (LEFT ALIGNED)
        purchaseTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Date
        purchaseTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total Amount
        
        purchaseTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(purchaseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

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

    private void loadPurchasesFromFolder() {
        if (tableModel == null) return;
        
        tableModel.setRowCount(0);
        
        File[] possibleDirs = {
            new File("src/main/invoices"),
            new File("src/invoices"),
            new File("invoices"),
            new File("../invoices"),
            new File("./invoices")
        };
        
        File invoicesDir = null;
        for (File dir : possibleDirs) {
            if (dir.exists() && dir.isDirectory()) {
                invoicesDir = dir;
                break;
            }
        }
        
        if (invoicesDir == null) {
            return;
        }
        
        File[] invoiceFiles = invoicesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (invoiceFiles == null || invoiceFiles.length == 0) {
            return;
        }
        
        Arrays.sort(invoiceFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        for (File file : invoiceFiles) {
            try {
                PurchaseData data = parsePurchaseFile(file);
                if (data != null && !data.purchaseId.isEmpty()) {
                    // Apply strict date-only search filter
                    boolean matchesSearch = currentSearchText.isEmpty();
                    if (!matchesSearch) {
                        // Validate that search text follows YYYY-MM-DD format (strict)
                        if (isValidDateFormat(currentSearchText)) {
                            // Only search in the date field
                            matchesSearch = data.date.toLowerCase().contains(currentSearchText);
                        } else {
                            // Invalid format - filter out everything
                            matchesSearch = false;
                        }
                    }
                    
                    if (matchesSearch) {
                        // Item count should be a plain number (no formatting), left-aligned
                        tableModel.addRow(new Object[]{
                            data.purchaseId,
                            data.customerName,
                            String.valueOf(data.itemsCount), // Plain string, will be left-aligned
                            data.date,
                            String.format("PHP %,.2f", data.totalAmount)
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading purchase: " + file.getName());
            }
        }
    }

    private PurchaseData parsePurchaseFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            PurchaseData data = new PurchaseData();
            String line;
            boolean inItemsSection = false;
            int itemsCount = 0;
            double totalAmount = 0.0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Find invoice ID - FIXED: Look for "Invoice Number:"
                if (data.purchaseId.isEmpty()) {
                    if (line.startsWith("Invoice Number:")) {
                        String[] parts = line.split("Invoice Number:");
                        if (parts.length > 1) {
                            data.purchaseId = parts[1].trim();
                        }
                    }
                }

                // Find customer name - FIXED: Handle the BILL TO: format correctly
                if (data.customerName.equals("Unknown")) {
                    if (line.contains("BILL TO:")) {
                        // Read next line which should contain "Name:"
                        String nameLine = reader.readLine();
                        if (nameLine != null) {
                            nameLine = nameLine.trim();
                            if (nameLine.startsWith("Name:")) {
                                String[] parts = nameLine.split("Name:");
                                if (parts.length > 1) {
                                    data.customerName = parts[1].trim();
                                    // Remove trailing comma if present
                                    if (data.customerName.endsWith(",")) {
                                        data.customerName = data.customerName.substring(0, data.customerName.length() - 1);
                                    }
                                }
                            }
                        }
                    }
                }

                // Find date
                if (line.startsWith("Date:")) {
                    String[] parts = line.split("Date:");
                    if (parts.length > 1) {
                        data.date = parts[1].trim();
                    }
                }

                // Find items section - FIXED: Look for "DESCRIPTION    QTY    UNIT PRICE    AMOUNT"
                if (line.contains("DESCRIPTION") && line.contains("QTY") && 
                    line.contains("UNIT PRICE") && line.contains("AMOUNT")) {
                    inItemsSection = true;
                    continue;
                }

                // Count items in the items section - FIXED: Better detection
                if (inItemsSection) {
                    // End of items section is a line with "---" or "TOTAL"
                    if (line.contains("---") || line.startsWith("TOTAL")) {
                        inItemsSection = false;
                    } 
                    // Count non-empty lines that look like item rows
                    else if (!line.isEmpty()) {
                        // Item rows have: item name, quantity, "PHP X.XX", "PHP X.XX"
                        // Simple check: if line contains "PHP" and has multiple spaces
                        if (line.contains("PHP") && line.split("\\s+").length >= 4) {
                            // Try to extract quantity (should be the second "word" after item name)
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 2) {
                                try {
                                    int qty = Integer.parseInt(parts[1]);
                                    if (qty > 0) {
                                        itemsCount++;
                                        System.out.println("Found item: " + line + " | Quantity: " + qty);
                                    }
                                } catch (NumberFormatException e) {
                                    // Not a valid quantity, but might still be an item row
                                    // Some items might have spaces in their names
                                    // Look for a number somewhere in the line
                                    for (int i = 1; i < parts.length; i++) {
                                        try {
                                            int qty = Integer.parseInt(parts[i]);
                                            if (qty > 0) {
                                                itemsCount++;
                                                System.out.println("Found item (alt): " + line + " | Quantity: " + qty);
                                                break;
                                            }
                                        } catch (NumberFormatException ex) {
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Find total amount - FIXED: Handle different formats
                if (line.contains("TOTAL AMOUNT DUE:")) {
                    String amountStr = line.replace("TOTAL AMOUNT DUE:", "")
                                          .replace("PHP", "")
                                          .replace(",", "")
                                          .trim();
                    try {
                        totalAmount = Double.parseDouble(amountStr);
                        data.totalAmount = totalAmount;
                    } catch (NumberFormatException e) {
                        // Try alternative parsing
                        String[] parts = line.split("\\s+");
                        for (String part : parts) {
                            if (part.matches("\\d+\\.?\\d*")) {
                                try {
                                    totalAmount = Double.parseDouble(part);
                                    data.totalAmount = totalAmount;
                                    break;
                                } catch (NumberFormatException ex) {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }

            // If we didn't find items with the above logic, use a simpler count
            if (itemsCount == 0) {
                itemsCount = countItemsSimple(file);
            }

            // If no date found, use file date
            if (data.date == null || data.date.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                data.date = sdf.format(new Date(file.lastModified()));
            }

            // If no invoice ID found, use filename
            if (data.purchaseId.isEmpty()) {
                String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".txt")) {
                    data.purchaseId = fileName.substring(0, fileName.length() - 4);
                } else {
                    data.purchaseId = fileName;
                }
            }

            // Clean up customer name (remove any trailing commas)
            if (data.customerName.endsWith(",")) {
                data.customerName = data.customerName.substring(0, data.customerName.length() - 1);
            }

            // Set items count
            data.itemsCount = itemsCount;

            System.out.println("Parsed invoice: " + data.purchaseId + 
                             ", Customer: " + data.customerName + 
                             ", Items: " + data.itemsCount + 
                             ", Total: " + data.totalAmount);

            return data;

        } catch (IOException e) {
            System.err.println("Error reading invoice file: " + file.getName());
            return null;
        }
    }

    private void refreshPurchases() {
        loadPurchasesFromFolder();
        JOptionPane.showMessageDialog(this, 
            "Purchase history refreshed!",
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private int countItemsSimple(File file) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean pastHeader = false;
            boolean beforeTotal = true;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Look for the header line
                if (line.contains("DESCRIPTION") && line.contains("QTY")) {
                    pastHeader = true;
                    continue;
                }
                
                // Look for the total line to stop counting
                if (line.contains("TOTAL AMOUNT DUE:")) {
                    beforeTotal = false;
                }
                
                // Count non-empty lines between header and total
                if (pastHeader && beforeTotal && !line.isEmpty() && !line.contains("---")) {
                    // Check if this looks like an item line (has at least 2 columns)
                    if (line.split("\\s+").length >= 2) {
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error in simple item count for: " + file.getName());
        }
        
        return Math.max(count, 0);
    }

    private void viewSelectedPurchase() {
        int selectedRow = purchaseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase to view.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = purchaseTable.convertRowIndexToModel(selectedRow);
        String purchaseId = (String) tableModel.getValueAt(modelRow, 0);
        
        File purchaseFile = null;
        String[] possiblePaths = {
            "src/main/invoices/" + purchaseId + ".txt",
            "src/invoices/" + purchaseId + ".txt",
            "invoices/" + purchaseId + ".txt"
        };
        
        for (String path : possiblePaths) {
            File testFile = new File(path);
            if (testFile.exists()) {
                purchaseFile = testFile;
                break;
            }
        }
        
        if (purchaseFile == null) {
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
                "Purchase Invoice: " + purchaseId, 
                JOptionPane.PLAIN_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error reading purchase file.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates if the search text follows the strict YYYY-MM-DD format.
     * Allows partial dates like "2025", "2025-12", or "2025-12-23"
     * but rejects incorrect formats like "12-23-2025" or "23-12-2025"
     */
    private boolean isValidDateFormat(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return false;
        }
        
        // Pattern: YYYY or YYYY-MM or YYYY-MM-DD
        // Year must be 4 digits, month and day must be 2 digits
        // Must start with a digit (year)
        if (!Character.isDigit(searchText.charAt(0))) {
            return false;
        }
        
        String[] parts = searchText.split("-");
        
        // Check each part
        if (parts.length == 1) {
            // Only year: must be 1-4 digits
            return parts[0].matches("\\d{1,4}");
        } else if (parts.length == 2) {
            // Year-Month: year must be 1-4 digits, month must be 1-2 digits
            return parts[0].matches("\\d{1,4}") && parts[1].matches("\\d{1,2}");
        } else if (parts.length == 3) {
            // Year-Month-Day: year must be 1-4 digits, month and day must be 1-2 digits
            return parts[0].matches("\\d{1,4}") && 
                   parts[1].matches("\\d{1,2}") && 
                   parts[2].matches("\\d{1,2}");
        }
        
        // More than 3 parts or invalid format
        return false;
    }

    private static class PurchaseData {
        String purchaseId = "";
        String customerName = "Unknown";
        String date = "";
        double totalAmount = 0.0;
        int itemsCount = 0;
    }
    
    public void refreshData() {
        resetSearch();
    }
}