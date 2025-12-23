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
        JPanel navBarPanel = new AdminNavBarPanel("Invoices");

        // Title and action buttons panel
        JPanel topPanel = createTopPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainContainer.add(navBarPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
    }

    /**
     * Creates the top panel with title and search functionality.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("All Invoices");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(130, 170, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

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
     * Loads all invoices from the invoices folder.
     */
    private void loadInvoicesFromFolder() {
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
                InvoiceData data = parseInvoiceFile(file);
                if (data != null) {
                    tableModel.addRow(new Object[]{
                        data.invoiceId,
                        data.customerName,
                        data.itemsCount,
                        data.date,
                        String.format("PHP %,.2f", data.totalAmount)
                    });
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
            int lineNum = 0;
            int itemsCount = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                if (lineNum == 3 && line.startsWith("INV")) {
                    data.invoiceId = line;
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
        
        String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
        File invoiceFile = new File("invoices", invoiceId + ".txt");
        
        if (!invoiceFile.exists()) {
            invoiceFile = new File("src/main/invoices", invoiceId + ".txt");
        }
        
        if (!invoiceFile.exists()) {
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
                "Error reading invoice file: " + e.getMessage(),
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
        
        String invoiceId = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirmResult = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete invoice " + invoiceId + "?\nThis action cannot be undone.",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmResult != JOptionPane.YES_OPTION) {
            return;
        }
        
        File invoiceFile = new File("invoices", invoiceId + ".txt");
        
        if (!invoiceFile.exists()) {
            invoiceFile = new File("src/main/invoices", invoiceId + ".txt");
        }
        
        if (!invoiceFile.exists()) {
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