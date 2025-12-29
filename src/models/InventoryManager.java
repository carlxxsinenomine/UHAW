package models;

import java.io.*;
import java.util.*;

// Manager class for handling all inventory data operations
// Handles loading, saving, adding, editing, and deleting inventory items from JSON storage
public class InventoryManager {
    private List<InventoryItem> inventoryItems;
    private static final String[] POSSIBLE_PATHS = {
        "src/items/inventory.json",
        "items/inventory.json",
        "../items/inventory.json"
    };

    // Constructor initializing the inventory items list
    public InventoryManager() {
        this.inventoryItems = new ArrayList<>();
    }

    // Loads inventory data from JSON file
    // If file not found, loads sample data as fallback
    public void loadInventory() {
        try {
            File file = findInventoryFile();
            if (file != null && file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    StringBuilder content = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        content.append(scanner.nextLine());
                    }
                    parseInventoryJson(content.toString());
                }
            } else {
                loadSampleData();
            }
        } catch (Exception e) {
            loadSampleData();
        }
    }

    // Finds the inventory JSON file by checking multiple possible paths
    private File findInventoryFile() {
        for (String path : POSSIBLE_PATHS) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    // Parses JSON content and populates the inventory list
    private void parseInventoryJson(String jsonContent) {
        try {
            inventoryItems.clear();
            String jsonStr = jsonContent.replaceAll("\\s+", " ");
            int arrayStart = jsonStr.indexOf('[');
            int arrayEnd = jsonStr.lastIndexOf(']');

            if (arrayStart == -1 || arrayEnd == -1) {
                return;
            }

            jsonStr = jsonStr.substring(arrayStart + 1, arrayEnd);
            String[] items = jsonStr.split("\\},\\s*\\{");

            for (String item : items) {
                item = item.trim();
                if (item.startsWith("{")) {
                    item = item.substring(1);
                }
                if (item.endsWith("}")) {
                    item = item.substring(0, item.length() - 1);
                }
                if (item.isEmpty()) {
                    continue;
                }

                try {
                    String itemName = extractJsonValue(item, "itemName");
                    String valueStr = extractJsonValue(item, "value");
                    String category = extractJsonValue(item, "category");
                    String quantityStr = extractJsonValue(item, "quantity");
                    String description = extractJsonValue(item, "description");

                    if (itemName != null && valueStr != null) {
                        double value = Double.parseDouble(valueStr.replaceAll(",", "").trim());
                        int quantity = (quantityStr != null) ? Integer.parseInt(quantityStr.trim()) : 0;
                        InventoryItem newItem = new InventoryItem(itemName, value, category, quantity);
                        if (description != null) {
                            newItem.description = description;
                        }
                        inventoryItems.add(newItem);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing item: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

    // Extracts a value from JSON string for a given key
    private String extractJsonValue(String jsonStr, String key) {
        String searchStr = "\"" + key + "\"";
        int keyIndex = jsonStr.indexOf(searchStr);
        if (keyIndex == -1) {
            return null;
        }

        int colonIndex = jsonStr.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return null;
        }

        int valueStart = colonIndex + 1;
        while (valueStart < jsonStr.length() && Character.isWhitespace(jsonStr.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= jsonStr.length()) {
            return null;
        }

        if (jsonStr.charAt(valueStart) == '"') {
            int valueEnd = jsonStr.indexOf('"', valueStart + 1);
            return (valueEnd != -1) ? jsonStr.substring(valueStart + 1, valueEnd) : null;
        } else {
            int valueEnd = valueStart;
            while (valueEnd < jsonStr.length() && jsonStr.charAt(valueEnd) != ',' && jsonStr.charAt(valueEnd) != '}') {
                valueEnd++;
            }
            return jsonStr.substring(valueStart, valueEnd).trim();
        }
    }

    // Loads sample/default inventory data for testing
    private void loadSampleData() {
        inventoryItems.clear();
        inventoryItems.add(new InventoryItem("Product A", 150.00, "1", 10));
        inventoryItems.add(new InventoryItem("Product B", 250.00, "2", 5));
        inventoryItems.add(new InventoryItem("Product C", 75.00, "3", 15));
    }

    // Returns all inventory items as a list
    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(inventoryItems);
    }

    // Returns inventory items as a map (key: item name, value: InventoryItem)
    // Useful for quick lookup by name
    public Map<String, InventoryItem> getItemsAsMap() {
        Map<String, InventoryItem> map = new HashMap<>();
        for (InventoryItem item : inventoryItems) {
            map.put(item.getName(), item);
        }
        return map;
    }

    // Searches for items matching the search text (case-insensitive)
    // Returns a filtered list based on item name
    public List<InventoryItem> searchItems(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllItems();
        }

        List<InventoryItem> results = new ArrayList<>();
        String lowerSearch = searchText.toLowerCase().trim();
        for (InventoryItem item : inventoryItems) {
            if (item.getName().toLowerCase().contains(lowerSearch)) {
                results.add(item);
            }
        }
        return results;
    }

    // Adds a new item to the inventory
    public void addItem(InventoryItem item) {
        if (item != null) {
            inventoryItems.add(item);
        }
    }

    // Removes an item by name
    public void deleteItem(String itemName) {
        inventoryItems.removeIf(item -> item.getName().equals(itemName));
    }

    // Gets a single item by name
    public InventoryItem getItemByName(String itemName) {
        for (InventoryItem item : inventoryItems) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Updates an existing item's details
    public void updateItem(String originalName, InventoryItem updatedItem) {
        for (int i = 0; i < inventoryItems.size(); i++) {
            if (inventoryItems.get(i).getName().equals(originalName)) {
                inventoryItems.set(i, updatedItem);
                return;
            }
        }
    }

    // Saves the current inventory to JSON file
    // Writes all items in JSON format to the inventory file
    public void saveInventory() {
        try {
            File file = findInventoryFile();
            if (file == null) {
                // Create file if it doesn't exist
                file = new File("items/inventory.json");
                file.getParentFile().mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("[");
                for (int i = 0; i < inventoryItems.size(); i++) {
                    InventoryItem item = inventoryItems.get(i);
                    writer.println("  {");
                    writer.println("    \"itemName\": \"" + escapeJson(item.getName()) + "\",");
                    writer.println("    \"value\": " + item.getPrice() + ",");
                    writer.println("    \"category\": \"" + item.getCategory() + "\",");
                    writer.println("    \"description\": \"" + escapeJson(item.description) + "\",");
                    writer.println("    \"quantity\": " + item.getQuantity());
                    writer.print("  }");
                    if (i < inventoryItems.size() - 1) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }
                writer.println("]");
            }
        } catch (Exception e) {
            System.err.println("Error saving inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Escapes special characters in JSON strings
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Returns the total number of items in inventory
    public int getItemCount() {
        return inventoryItems.size();
    }

    // Checks if an item exists by name
    public boolean itemExists(String itemName) {
        return inventoryItems.stream().anyMatch(item -> item.getName().equals(itemName));
    }
}
