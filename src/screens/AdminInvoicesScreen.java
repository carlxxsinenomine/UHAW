package screens;

import components.AdminNavBarPanel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import main.AppConstants;

/**
 * AdminInvoicesScreen - The invoice management interface for administrators.
 * 
 * This screen provides admins with a comprehensive view of all system invoices
 * and tools to manage them. Administrators can monitor sales activity, search
 * for specific invoices, and perform administrative actions.
 * 
 * Features:
 * - Invoice Table: Displays all invoices with customer, date, and amount info
 * - Search Functionality: Find invoices by ID or date (YYYY-MM-DD format)
 * - View Invoice: Open detailed invoice content in a popup dialog
 * - Delete Invoice: Remove specific invoices from the system
 * - Refresh: Manually reload invoice list from files
 * - Real-time Updates: Auto-reflects new invoices from user purchases
 * 
 * Table Columns:
 * - Invoice ID: Unique invoice identifier
 * - Customer Name: Name of the purchasing customer
 * - Items Count: Number of items in the invoice
 * - Date: Date and time of invoice generation
 * - Total Amount: Total invoice value in PHP currency
 * 
 * Data Management:
 * - Loads invoices from text files in the invoices directory
 * - Displays invoices sorted by most recent first
 * - Supports date-based filtering for period analysis
 * - Manages invoice file operations (read, delete)
 * 
 * Admin Actions:
 * - View Invoice: Display complete invoice details and content
 * - Delete Invoice: Remove invoice files with confirmation
 * - Refresh: Reload invoice list to see latest transactions
 * - Search: Filter invoices by date range or ID pattern
 */
public class AdminInvoicesScreen extends JPanel {
    private DefaultTableModel tableModel;
    private JTable invoiceTable;
    private String currentSearchText = "";
    private AdminNavBarPanel navBarPanel;

    public AdminInvoicesScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        navBarPanel = new AdminNavBarPanel("Invoices");

        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();

        navBarPanel.setSearchListener(text -> {
            String searchText = text.trim();
            
            if (searchText.isEmpty() || 
                searchText.equals("Search") || 
                searchText.equals("Search (YYYY-MM-DD)")) {
                this.currentSearchText = "";
            } else {
                // Convert to uppercase and validate format
                this.currentSearchText = searchText.toUpperCase();
            }
            loadInvoicesFromFolder();
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

    private void resetSearch() {
        if (navBarPanel != null) {
            navBarPanel.resetSearch();
        }
        currentSearchText = "";
        loadInvoicesFromFolder();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel titleLabel = new JLabel("All Invoices");
        titleLabel.setFont(AppConstants.FONT_TITLE_XL);

        topPanel.add(titleLabel, BorderLayout.WEST);

        return topPanel;
    }

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

        loadInvoicesFromFolder();

        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(AppConstants.FONT_BODY_REGULAR);
        invoiceTable.setRowHeight(40);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getTableHeader().setFont(AppConstants.FONT_LABEL_BOLD);
        invoiceTable.getTableHeader().setBackground(AppConstants.BG_MEDIUM_GRAY);
        invoiceTable.getTableHeader().setReorderingAllowed(false);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        for (int i = 0; i < invoiceTable.getColumnCount(); i++) {
            invoiceTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConstants.BORDER_LIGHT_GRAY, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(AppConstants.FONT_LABEL_BOLD);
        refreshButton.setBackground(AppConstants.PRIMARY_BLUE);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refreshInvoices());

        JButton viewButton = new JButton("View Invoice");
        viewButton.setFont(AppConstants.FONT_LABEL_BOLD);
        viewButton.setBackground(AppConstants.ACCENT_GREEN);
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> viewSelectedInvoice());

        JButton deleteButton = new JButton("Delete Invoice");
        deleteButton.setFont(AppConstants.FONT_LABEL_BOLD);
        deleteButton.setBackground(AppConstants.ACCENT_RED);
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

    private void loadInvoicesFromFolder() {
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
                    // Convert invoice date to YYYY-MM-DD format
                    String normalizedDate = convertToYYYYMMDD(data.date);
                    
                    // Apply search filter
                    boolean matchesSearch = currentSearchText.isEmpty();
                    if (!matchesSearch && !currentSearchText.isEmpty()) {
                        // Check if search text is valid YYYY-MM-DD format
                        if (isValidYYYYMMDDFormat(currentSearchText)) {
                            // Apply strict filtering based on search pattern
                            matchesSearch = matchesStrictDatePattern(normalizedDate, currentSearchText);
                        } else {
                            // Invalid format - show nothing
                            matchesSearch = false;
                        }
                    }
                    
                    if (matchesSearch) {
                        tableModel.addRow(new Object[]{
                            data.invoiceId,
                            data.customerName,
                            String.valueOf(data.itemsCount),
                            data.date, // Keep original display format
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
     * Converts date to YYYY-MM-DD format for comparison
     */
    private String convertToYYYYMMDD(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        
        try {
            // First, try to parse as YYYY-MM-DD directly
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return dateStr;
            }
            
            // Try common date formats
            SimpleDateFormat[] inputFormats = {
                new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH),
                new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH),
                new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH),
                new SimpleDateFormat("dd/MM/yyyy"),
                new SimpleDateFormat("MM/dd/yyyy"),
                new SimpleDateFormat("dd-MM-yyyy")
            };
            
            for (SimpleDateFormat sdf : inputFormats) {
                try {
                    Date date = sdf.parse(dateStr);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return outputFormat.format(date);
                } catch (Exception e) {
                    // Try next format
                }
            }
            
            // If parsing fails, try to extract YYYY-MM-DD pattern
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}").matcher(dateStr);
            if (matcher.find()) {
                return matcher.group();
            }
            
        } catch (Exception e) {
            System.err.println("Error converting date: " + dateStr + " - " + e.getMessage());
        }
        
        return "";
    }

    /**
     * Checks if search text is valid YYYY-MM-DD format
     * Accepts patterns like: Y, YY, YYY, YYYY, YYYY-M, YYYY-MM, YYYY-MM-D
     * But must follow the dash-separated pattern
     */
    private boolean isValidYYYYMMDDFormat(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return false;
        }
        
        // Must contain only digits and dashes
        if (!searchText.matches("[\\d-]+")) {
            return false;
        }
        
        // Must start with a digit (year)
        if (!Character.isDigit(searchText.charAt(0))) {
            return false;
        }
        
        // Count dashes
        int dashCount = 0;
        for (char c : searchText.toCharArray()) {
            if (c == '-') dashCount++;
        }
        
        // Validate based on number of dashes
        if (dashCount == 0) {
            // Only year: Y, YY, YYY, or YYYY
            return searchText.matches("\\d{1,4}");
        } else if (dashCount == 1) {
            // Year-Month or Year-Month- (partial day)
            String[] parts = searchText.split("-");
            if (parts.length != 2) return false;
            if (!parts[0].matches("\\d{1,4}")) return false;
            
            // Month part can be empty, 1 digit, or 2 digits
            return parts[1].isEmpty() || parts[1].matches("\\d{1,2}");
        } else if (dashCount == 2) {
            // Year-Month-Day or partial
            String[] parts = searchText.split("-");
            if (parts.length != 3) return false;
            if (!parts[0].matches("\\d{1,4}")) return false;
            if (!parts[1].matches("\\d{1,2}")) return false;
            
            // Day part can be empty, 1 digit, or 2 digits
            return parts[2].isEmpty() || parts[2].matches("\\d{1,2}");
        }
        
        return false;
    }

    /**
     * Strict date pattern matching for YYYY-MM-DD format
     */
    private boolean matchesStrictDatePattern(String normalizedDate, String searchPattern) {
        if (normalizedDate == null || normalizedDate.isEmpty() || 
            searchPattern == null || searchPattern.isEmpty()) {
            return false;
        }
        
        // Ensure normalizedDate is in YYYY-MM-DD format
        if (!normalizedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        
        // Split both dates
        String[] dateParts = normalizedDate.split("-");
        String[] searchParts = searchPattern.split("-");
        
        // Year matching
        if (searchParts.length >= 1 && !searchParts[0].isEmpty()) {
            String yearPattern = searchParts[0];
            String invoiceYear = dateParts[0];
            
            // Match year pattern (supports partial years)
            if (yearPattern.length() > invoiceYear.length()) {
                return false;
            }
            if (!invoiceYear.startsWith(yearPattern)) {
                return false;
            }
        }
        
        // Month matching (only if month part exists in search)
        if (searchParts.length >= 2 && !searchParts[1].isEmpty()) {
            if (dateParts.length < 2) return false;
            
            String monthPattern = searchParts[1];
            String invoiceMonth = dateParts[1];
            
            // Pad single digit months with leading zero
            if (monthPattern.length() == 1) {
                monthPattern = "0" + monthPattern;
            }
            if (invoiceMonth.length() == 1) {
                invoiceMonth = "0" + invoiceMonth;
            }
            
            // Match month pattern
            if (monthPattern.length() > invoiceMonth.length()) {
                return false;
            }
            if (!invoiceMonth.startsWith(monthPattern)) {
                return false;
            }
        }
        
        // Day matching (only if day part exists in search)
        if (searchParts.length >= 3 && !searchParts[2].isEmpty()) {
            if (dateParts.length < 3) return false;
            
            String dayPattern = searchParts[2];
            String invoiceDay = dateParts[2];
            
            // Pad single digit days with leading zero
            if (dayPattern.length() == 1) {
                dayPattern = "0" + dayPattern;
            }
            if (invoiceDay.length() == 1) {
                invoiceDay = "0" + invoiceDay;
            }
            
            // Match day pattern
            if (dayPattern.length() > invoiceDay.length()) {
                return false;
            }
            if (!invoiceDay.startsWith(dayPattern)) {
                return false;
            }
        }
        
        return true;
    }

    private InvoiceData parseInvoiceFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            InvoiceData data = new InvoiceData();
            String line;
            boolean inItemsSection = false;
            int itemsCount = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (data.invoiceId.isEmpty()) {
                    if (line.startsWith("Invoice Number:")) {
                        String[] parts = line.split("Invoice Number:");
                        if (parts.length > 1) {
                            data.invoiceId = parts[1].trim();
                        }
                    }
                }
                
                if (data.customerName.equals("Unknown")) {
                    if (line.contains("BILL TO:")) {
                        String nameLine = reader.readLine();
                        if (nameLine != null) {
                            nameLine = nameLine.trim();
                            if (nameLine.startsWith("Name:")) {
                                String[] parts = nameLine.split("Name:");
                                if (parts.length > 1) {
                                    data.customerName = parts[1].trim();
                                    if (data.customerName.endsWith(",")) {
                                        data.customerName = data.customerName.substring(0, data.customerName.length() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (line.startsWith("Date:")) {
                    String[] parts = line.split("Date:");
                    if (parts.length > 1) {
                        data.date = parts[1].trim();
                    }
                }
                
                if (line.contains("DESCRIPTION") && line.contains("QTY") && 
                    line.contains("UNIT PRICE") && line.contains("AMOUNT")) {
                    inItemsSection = true;
                    continue;
                }
                
                if (inItemsSection) {
                    if (line.contains("---") || line.startsWith("TOTAL")) {
                        inItemsSection = false;
                    } 
                    else if (!line.isEmpty()) {
                        if (line.contains("PHP") && line.split("\\s+").length >= 4) {
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 2) {
                                try {
                                    int qty = Integer.parseInt(parts[1]);
                                    if (qty > 0) {
                                        itemsCount++;
                                    }
                                } catch (NumberFormatException e) {
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
            
            if (itemsCount == 0) {
                itemsCount = countItemsSimple(file);
            }
            
            if (data.date == null || data.date.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                data.date = sdf.format(new Date(file.lastModified()));
            }
            
            if (data.invoiceId.isEmpty()) {
                String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".txt")) {
                    data.invoiceId = fileName.substring(0, fileName.length() - 4);
                } else {
                    data.invoiceId = fileName;
                }
            }
            
            if (data.customerName.endsWith(",")) {
                data.customerName = data.customerName.substring(0, data.customerName.length() - 1);
            }
            
            data.itemsCount = itemsCount;
            
            return data;
            
        } catch (IOException e) {
            System.err.println("Error reading invoice file: " + file.getName());
            return null;
        }
    }
    
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

    private void refreshInvoices() {
        loadInvoicesFromFolder();
        JOptionPane.showMessageDialog(this, 
            "Invoice list refreshed!",
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an invoice to view.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = invoiceTable.convertRowIndexToModel(selectedRow);
        String invoiceId = (String) tableModel.getValueAt(modelRow, 0);
        
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

    private void deleteSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an invoice to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
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

    private static class InvoiceData {
        String invoiceId = "";
        String customerName = "Unknown";
        String date = "";
        double totalAmount = 0.0;
        int itemsCount = 0;
    }
}