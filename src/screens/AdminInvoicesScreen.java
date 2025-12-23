package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class AdminInvoicesScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable invoiceTable;
    private String currentSearchText = "";
    private AdminNavBarPanel navBarPanel; // Store reference to nav bar

    /**
     * Constructor that initializes and displays the AdminInvoicesScreen.
     */
    public AdminInvoicesScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Navigation bar
        navBarPanel = new AdminNavBarPanel("Invoices");

        // Title panel
        JPanel topPanel = createTopPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Set search listener AFTER table is created
        navBarPanel.setSearchListener(text -> {
            String searchText = text.trim();
            
            if (searchText.isEmpty() || 
                searchText.equals("Search") || 
                searchText.equals("Search (YYYY-MM-DD)")) {
                this.currentSearchText = "";
            } else {
                this.currentSearchText = searchText.toLowerCase();
            }
            loadInvoicesFromFolder();
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
        loadInvoicesFromFolder();
    }

    /**
     * Creates the top panel with title.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("All Invoices");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        topPanel.add(titleLabel, BorderLayout.WEST);

        return topPanel;
    }

    /**
     * Creates the table panel displaying all invoices.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        String[] columnNames = {"Invoice ID", "Customer Name", "Items Count", "Date", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load actual invoice data from files
        loadInvoicesFromFolder();

        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Arial", Font.PLAIN, 14));
        invoiceTable.setRowHeight(40);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        invoiceTable.getTableHeader().setBackground(new Color(200, 200, 200));
        invoiceTable.getTableHeader().setReorderingAllowed(false);
        
        // Set cell renderer for left alignment
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        for (int i = 0; i < invoiceTable.getColumnCount(); i++) {
            invoiceTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
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
        refreshButton.addActionListener(e -> refreshInvoices());

        JButton viewButton = new JButton("View Invoice");
        viewButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewButton.setBackground(new Color(34, 139, 34));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> viewSelectedInvoice());

        JButton deleteButton = new JButton("Delete Invoice");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> deleteSelectedInvoice());

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    /**
     * Loads all invoices from the invoices folder with search filter.
     */
    private void loadInvoicesFromFolder() {
        if (tableModel == null) return; // Guard against null tableModel
        
        tableModel.setRowCount(0);
        
        // Try multiple invoice directory locations
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
        
        if (invoicesDir == null || !invoicesDir.isDirectory()) {
            return;
        }
        
        File[] invoiceFiles = invoicesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (invoiceFiles == null || invoiceFiles.length == 0) {
            return;
        }
        
        Arrays.sort(invoiceFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        for (File file : invoiceFiles) {
            try {
                InvoiceData data = parseInvoiceFile(file);
                if (data != null && !data.invoiceId.isEmpty()) {
                    // Apply search filter
                    boolean matchesSearch = currentSearchText.isEmpty();
                    if (!matchesSearch) {
                        matchesSearch = data.invoiceId.toLowerCase().contains(currentSearchText) ||
                                       data.customerName.toLowerCase().contains(currentSearchText) ||
                                       data.date.toLowerCase().contains(currentSearchText);
                    }
                    
                    if (matchesSearch) {
                        tableModel.addRow(new Object[]{
                            data.invoiceId,
                            data.customerName,
                            String.valueOf(data.itemsCount), // Plain string for left alignment
                            data.date,
                            String.format("PHP %,.2f", data.totalAmount)
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading invoice: " + file.getName() + " - " + e.getMessage());
            }
        }
    }

    /**
     * Parses an invoice file and extracts relevant data.
     */
    private InvoiceData parseInvoiceFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            InvoiceData data = new InvoiceData();
            String line;
            boolean inItemsSection = false;
            int itemsCount = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Find invoice ID
                if (data.invoiceId.isEmpty()) {
                    if (line.startsWith("Invoice Number:")) {
                        String[] parts = line.split("Invoice Number:");
                        if (parts.length > 1) {
                            data.invoiceId = parts[1].trim();
                        }
                    }
                }
                
                // Find customer name
                if (data.customerName.equals("Unknown")) {
                    if (line.contains("BILL TO:")) {
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
                
                // Find items section
                if (line.contains("DESCRIPTION") && line.contains("QTY") && 
                    line.contains("UNIT PRICE") && line.contains("AMOUNT")) {
                    inItemsSection = true;
                    continue;
                }
                
                // Count items in the items section
                if (inItemsSection) {
                    if (line.contains("---") || line.startsWith("TOTAL")) {
                        inItemsSection = false;
                    } 
                    // Count non-empty lines that look like item rows
                    else if (!line.isEmpty()) {
                        // Item rows have: item name, quantity, "PHP X.XX", "PHP X.XX"
                        if (line.contains("PHP") && line.split("\\s+").length >= 4) {
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 2) {
                                try {
                                    int qty = Integer.parseInt(parts[1]);
                                    if (qty > 0) {
                                        itemsCount++;
                                    }
                                } catch (NumberFormatException e) {
                                    // Look for a number somewhere in the line
                                    for (int i = 1; i < parts.length; i++) {
                                        try {
                                            int qty = Integer.parseInt(parts[i]);
                                            if (qty > 0) {
                                                itemsCount++;
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
                
                // Find total amount
                if (line.contains("TOTAL AMOUNT DUE:")) {
                    String amountStr = line.replace("TOTAL AMOUNT DUE:", "")
                                          .replace("PHP", "")
                                          .replace(",", "")
                                          .trim();
                    try {
                        data.totalAmount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        data.totalAmount = 0.0;
                    }
                }
            }
            
            // If we didn't find items with the above logic, use a simpler count
            if (itemsCount == 0) {
                itemsCount = countItemsSimple(file);
            }
            
            // If no date found, use file date
            if (data.date == null || data.date.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                data.date = sdf.format(new Date(file.lastModified()));
            }
            
            // If no invoice ID found, use filename
            if (data.invoiceId.isEmpty()) {
                String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".txt")) {
                    data.invoiceId = fileName.substring(0, fileName.length() - 4);
                } else {
                    data.invoiceId = fileName;
                }
            }
            
            // Clean up customer name
            if (data.customerName.endsWith(",")) {
                data.customerName = data.customerName.substring(0, data.customerName.length() - 1);
            }
            
            // Set items count
            data.itemsCount = itemsCount;
            
            return data;
            
        } catch (IOException e) {
            System.err.println("Error reading invoice file: " + file.getName());
            return null;
        }
    }
    
    /**
     * Simple item counting method
     */
    private int countItemsSimple(File file) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean pastHeader = false;
            boolean beforeTotal = true;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.contains("DESCRIPTION") && line.contains("QTY")) {
                    pastHeader = true;
                    continue;
                }
                
                if (line.contains("TOTAL AMOUNT DUE:")) {
                    beforeTotal = false;
                }
                
                if (pastHeader && beforeTotal && !line.isEmpty() && !line.contains("---")) {
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

    /**
     * Refreshes the invoice list.
     */
    private void refreshInvoices() {
        loadInvoicesFromFolder();
        JOptionPane.showMessageDialog(this, 
            "Invoice list refreshed!",
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Views the selected invoice file.
     */
    private void viewSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an invoice to view.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view row index to model row index (for sorting)
        int modelRow = invoiceTable.convertRowIndexToModel(selectedRow);
        String invoiceId = (String) tableModel.getValueAt(modelRow, 0);
        
        // Try multiple locations for the invoice file
        File invoiceFile = null;
        String[] possiblePaths = {
            "src/main/invoices/" + invoiceId + ".txt",
            "src/invoices/" + invoiceId + ".txt",
            "invoices/" + invoiceId + ".txt",
            "../invoices/" + invoiceId + ".txt",
            "./invoices/" + invoiceId + ".txt"
        };
        
        for (String path : possiblePaths) {
            File testFile = new File(path);
            if (testFile.exists()) {
                invoiceFile = testFile;
                break;
            }
        }
        
        if (invoiceFile == null || !invoiceFile.exists()) {
            JOptionPane.showMessageDialog(this, 
                "Invoice file not found: " + invoiceId + ".txt",
                "File Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(invoiceFile))) {
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
                "Invoice: " + invoiceId, 
                JOptionPane.PLAIN_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error reading invoice file.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected invoice file.
     */
    private void deleteSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an invoice to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view row index to model row index (for sorting)
        int modelRow = invoiceTable.convertRowIndexToModel(selectedRow);
        String invoiceId = (String) tableModel.getValueAt(modelRow, 0);
        
        int confirmResult = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete invoice " + invoiceId + "?\nThis action cannot be undone.",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmResult != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Try multiple locations for the invoice file
        File invoiceFile = null;
        String[] possiblePaths = {
            "src/main/invoices/" + invoiceId + ".txt",
            "src/invoices/" + invoiceId + ".txt",
            "invoices/" + invoiceId + ".txt"
        };
        
        for (String path : possiblePaths) {
            File testFile = new File(path);
            if (testFile.exists()) {
                invoiceFile = testFile;
                break;
            }
        }
        
        if (invoiceFile == null || !invoiceFile.exists()) {
            JOptionPane.showMessageDialog(this, 
                "Invoice file not found: " + invoiceId + ".txt",
                "File Not Found", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (invoiceFile.delete()) {
                JOptionPane.showMessageDialog(this, 
                    "Invoice " + invoiceId + " has been deleted successfully.",
                    "Delete Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshInvoices();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete invoice file: " + invoiceId + ".txt",
                    "Delete Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting invoice: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inner class to store invoice data.
     */
    private static class InvoiceData {
        String invoiceId = "";
        String customerName = "Unknown";
        String date = "";
        double totalAmount = 0.0;
        int itemsCount = 0;
    }
}