package screens;

import components.NavBarPanel;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import main.MainActivity;

public class UserScreen extends JPanel {

    private Map<String, Double> itemValueMap;
    private Map<String, String> itemCategoryMap;
    private Set<String> selectedCategories;
    private JPanel itemsPanel;
    private JLabel overallTotalLabel;
    private JLabel totalItemsLabel;
    private java.util.List<JSpinner> quantitySpinners;

    public UserScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        itemValueMap = new HashMap<>();
        itemCategoryMap = new HashMap<>();
        selectedCategories = new HashSet<>();
        quantitySpinners = new java.util.ArrayList<>();

        loadInventoryData();
        selectedCategories.addAll(Arrays.asList("1", "2", "3"));

        // --- MODIFIED HEADER SECTION ---
        JPanel navBarPanel = new NavBarPanel("Home");

        // Wrapper for the header
        JPanel navWrapper = new JPanel(new BorderLayout());
        navWrapper.setOpaque(false);
        navWrapper.add(navBarPanel, BorderLayout.CENTER);

        // "Back to Menu" Button (Top Left)
        JButton backButton = new JButton("« Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setForeground(new Color(130, 170, 255));
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(130, 170, 255), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> MainActivity.getInstance().showScreen(MainActivity.MAIN_MENU_SCREEN));

        // Container for Back Button to give it padding
        JPanel backBtnContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtnContainer.setOpaque(false);
        backBtnContainer.add(backButton);
        navWrapper.add(backBtnContainer, BorderLayout.NORTH);

        JLabel companyName = new JLabel("Generate New Invoice for Peter Loves Carl Co.");
        companyName.setFont(new Font("Arial", Font.BOLD, 32));
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        companyName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 20, 10, 20),
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(130, 170, 255))
        ));
        navWrapper.add(companyName, BorderLayout.SOUTH);
        // --- END HEADER MODIFICATION ---

        // Main contents
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel informationInputContainer = getCustomerInfoPanel();
        JPanel invoiceTablePanel = getInvoiceTablePanel();
        JPanel bottomPanel = getBottomPanel();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(informationInputContainer, BorderLayout.NORTH);
        contentPanel.add(invoiceTablePanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.WHITE);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.add(navWrapper, BorderLayout.NORTH);
        containerPanel.add(contentPanel, BorderLayout.CENTER);

        add(containerPanel);
    }

    // ... [REST OF YOUR EXISTING CODE: loadInventoryData, parseInventoryJson, etc.] ...
    // Note: Copy the rest of the methods (loadInventoryData, parseInventoryJson, getCustomerInfoPanel, etc.)
    // from your original file here. I have only modified the constructor to add the Back button.

    // For brevity, I am not pasting the methods that didn't change.
    // Please ensure you keep them in the file!

    // ... [Include all other methods here] ...

    // --- Helper for context ---
    private void loadInventoryData() {
        // (Paste your original loadInventoryData code here)
        try {
            File file = null;
            String[] possiblePaths = { "src/items/inventory.json", "items/inventory.json", "../items/inventory.json" };
            for (String path : possiblePaths) {
                File testFile = new File(path);
                if (testFile.exists()) { file = testFile; break; }
            }
            if (file != null && file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder content = new StringBuilder();
                    while (scanner.hasNextLine()) content.append(scanner.nextLine());
                    parseInventoryJson(content.toString());
                }
            } else {
                loadSampleData();
            }
        } catch (Exception e) { loadSampleData(); }
    }

    private void loadSampleData() {
        itemValueMap.put("Product A", 150.00); itemCategoryMap.put("Product A", "1");
        itemValueMap.put("Service X", 250.00); itemCategoryMap.put("Service X", "2");
        itemValueMap.put("Product D", 199.99); itemCategoryMap.put("Product D", "3");
    }

    private void parseInventoryJson(String jsonContent) {
        // (Paste your original parseInventoryJson code here)
        try {
            String jsonStr = jsonContent.replaceAll("\\s+", " ");
            int arrayStart = jsonStr.indexOf('[');
            int arrayEnd = jsonStr.lastIndexOf(']');
            if (arrayStart != -1 && arrayEnd != -1) {
                jsonStr = jsonStr.substring(arrayStart + 1, arrayEnd);
                String[] items = jsonStr.split("\\},\\s*\\{");
                for (String item : items) {
                    item = item.replace("{", "").replace("}", "");
                    String itemName = extractJsonValue(item, "itemName");
                    String valueStr = extractJsonValue(item, "value");
                    String category = extractJsonValue(item, "category");
                    if (itemName != null && valueStr != null && category != null) {
                        try {
                            double value = Double.parseDouble(valueStr.replaceAll(",.*", "").trim());
                            itemValueMap.put(itemName, value);
                            itemCategoryMap.put(itemName, category);
                        } catch (Exception e) {}
                    }
                }
            }
        } catch (Exception e) {}
    }

    private String extractJsonValue(String jsonStr, String key) {
        // (Paste your original extractJsonValue code here)
        String searchStr = "\"" + key + "\"";
        int keyIndex = jsonStr.indexOf(searchStr);
        if (keyIndex == -1) return null;
        int colonIndex = jsonStr.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        int valueStart = colonIndex + 1;
        while (valueStart < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(valueStart))) valueStart++;
        if (valueStart >= jsonStr.length()) return null;
        if (jsonStr.charAt(valueStart) == '"') {
            int valueEnd = jsonStr.indexOf('"', valueStart + 1);
            return (valueEnd != -1) ? jsonStr.substring(valueStart + 1, valueEnd) : null;
        } else {
            int valueEnd = valueStart;
            while (valueEnd < jsonStr.length() && jsonStr.charAt(valueEnd) != ',' && jsonStr.charAt(valueEnd) != '}') valueEnd++;
            return jsonStr.substring(valueStart, valueEnd).trim();
        }
    }

    private JPanel getCustomerInfoPanel() {
        JPanel informationInputContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        informationInputContainer.setOpaque(false);
        // ... (Add your Name, Contact, Address fields here as before) ...
        // Simply returning a placeholder to make this code compile if you copy-paste blindly.
        // PLEASE USE YOUR ORIGINAL CODE FOR THIS METHOD
        return informationInputContainer;
    }

    // NOTE: I am abbreviating to save space, but you must keep getInvoiceTablePanel, createItemRow, etc.
    // They do not need changes. Only the constructor changed.

    private JPanel getInvoiceTablePanel() {
        // (Paste your original getInvoiceTablePanel code here)
        JPanel p = new JPanel();
        // ...
        return p;
    }

    private JPanel getBottomPanel() {
        // (Paste your original getBottomPanel code here)
        JPanel p = new JPanel();
        // ...
        return p;
    }
}