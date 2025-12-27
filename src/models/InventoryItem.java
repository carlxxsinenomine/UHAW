package models;

/**
 * InventoryItem - Represents a product in the inventory system.
 * 
 * This class encapsulates all information about a product, including its identification,
 * pricing, categorization, and current stock level. It serves as the core data model for
 * inventory management operations and provides controlled access to product attributes
 * through getter and setter methods.
 * 
 * Key Features:
 * - Immutable product name: Once created, the product name cannot be changed
 * - Mutable pricing: Prices can be updated to reflect market changes or promotions
 * - Category tracking: Products are organized by category for easier management
 * - Stock management: Quantity can be adjusted as inventory is bought or sold
 * 
 * Usage:
 * This class is typically used when loading inventory from storage, managing stock levels,
 * and displaying product information in the admin panel.
 */
public class InventoryItem {
    private final String name;
    private double price;
    private String category;
    private int quantity;
    
    /**
     * Creates a new inventory item with the specified details.
     *
     * @param name the product name (immutable after creation)
     * @param price the current selling price
     * @param category the product category for organization
     * @param quantity the current stock level
     */
    public InventoryItem(String name, double price, String category, int quantity) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }
    
    /**
     * Gets the product name.
     * @return the name of this inventory item
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the current selling price.
     * @return the price of this item
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Updates the selling price of this item.
     * @param price the new selling price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Gets the product category.
     * @return the category this item belongs to
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Updates the product category.
     * @param category the new category for this item
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * Gets the current stock quantity.
     * @return the number of units currently in stock
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Updates the stock quantity.
     * @param quantity the new stock level
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
