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
                this.currentSearchText = searchText.toUpperCase();
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
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        for (int i = 0; i < purchaseTable.getColumnCount(); i++) {
            purchaseTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
        
        purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        purchaseTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        purchaseTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
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
                    // Convert invoice date to YYYY-MM-DD format
                    String normalizedDate = convertToYYYYMMDD(data.date);
                    
                    // Apply search filter
                    boolean matchesSearch = currentSearchText.isEmpty();
                    if (!matchesSearch && !currentSearchText.isEmpty()) {
                        // Check if search text is valid YYYY-MM-DD format
                        if (isValidYYYYMMDDFormat(currentSearchText)) {
                            // Apply strict filtering
                            matchesSearch = matchesStrictDatePattern(normalizedDate, currentSearchText);
                        } else {
                            matchesSearch = false;
                        }
                    }
                    
                    if (matchesSearch) {
                        tableModel.addRow(new Object[]{
                            data.purchaseId,
                            data.customerName,
                            String.valueOf(data.itemsCount),
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

    /**
     * Converts date to YYYY-MM-DD format for comparison
     */
    private String convertToYYYYMMDD(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        
        try {
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return dateStr;
            }
            
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
     */
    private boolean isValidYYYYMMDDFormat(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return false;
        }
        
        if (!searchText.matches("[\\d-]+")) {
            return false;
        }
        
        if (!Character.isDigit(searchText.charAt(0))) {
            return false;
        }
        
        int dashCount = 0;
        for (char c : searchText.toCharArray()) {
            if (c == '-') dashCount++;
        }
        
        if (dashCount == 0) {
            return searchText.matches("\\d{1,4}");
        } else if (dashCount == 1) {
            String[] parts = searchText.split("-");
            if (parts.length != 2) return false;
            if (!parts[0].matches("\\d{1,4}")) return false;
            return parts[1].isEmpty() || parts[1].matches("\\d{1,2}");
        } else if (dashCount == 2) {
            String[] parts = searchText.split("-");
            if (parts.length != 3) return false;
            if (!parts[0].matches("\\d{1,4}")) return false;
            if (!parts[1].matches("\\d{1,2}")) return false;
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
        
        if (!normalizedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        
        String[] dateParts = normalizedDate.split("-");
        String[] searchParts = searchPattern.split("-");
        
        // Year matching
        if (searchParts.length >= 1 && !searchParts[0].isEmpty()) {
            String yearPattern = searchParts[0];
            String invoiceYear = dateParts[0];
            
            if (yearPattern.length() > invoiceYear.length()) {
                return false;
            }
            if (!invoiceYear.startsWith(yearPattern)) {
                return false;
            }
        }
        
        // Month matching
        if (searchParts.length >= 2 && !searchParts[1].isEmpty()) {
            if (dateParts.length < 2) return false;
            
            String monthPattern = searchParts[1];
            String invoiceMonth = dateParts[1];
            
            if (monthPattern.length() == 1) {
                monthPattern = "0" + monthPattern;
            }
            if (invoiceMonth.length() == 1) {
                invoiceMonth = "0" + invoiceMonth;
            }
            
            if (monthPattern.length() > invoiceMonth.length()) {
                return false;
            }
            if (!invoiceMonth.startsWith(monthPattern)) {
                return false;
            }
        }
        
        // Day matching
        if (searchParts.length >= 3 && !searchParts[2].isEmpty()) {
            if (dateParts.length < 3) return false;
            
            String dayPattern = searchParts[2];
            String invoiceDay = dateParts[2];
            
            if (dayPattern.length() == 1) {
                dayPattern = "0" + dayPattern;
            }
            if (invoiceDay.length() == 1) {
                invoiceDay = "0" + invoiceDay;
            }
            
            if (dayPattern.length() > invoiceDay.length()) {
                return false;
            }
            if (!invoiceDay.startsWith(dayPattern)) {
                return false;
            }
        }
        
        return true;
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

                if (data.purchaseId.isEmpty()) {
                    if (line.startsWith("Invoice Number:")) {
                        String[] parts = line.split("Invoice Number:");
                        if (parts.length > 1) {
                            data.purchaseId = parts[1].trim();
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
                        totalAmount = Double.parseDouble(amountStr);
                        data.totalAmount = totalAmount;
                    } catch (NumberFormatException e) {
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

            if (itemsCount == 0) {
                itemsCount = countItemsSimple(file);
            }

            if (data.date == null || data.date.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                data.date = sdf.format(new Date(file.lastModified()));
            }

            if (data.purchaseId.isEmpty()) {
                String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".txt")) {
                    data.purchaseId = fileName.substring(0, fileName.length() - 4);
                } else {
                    data.purchaseId = fileName;
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