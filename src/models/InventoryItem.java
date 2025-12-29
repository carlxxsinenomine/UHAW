package models;

// Represents a product in the inventory system
// Encapsulates product information including name, price, category, and quantity
public class InventoryItem {
    private String name;
    private double price;
    private String category;
    private int quantity;
    public String description; // Public for backward compatibility with admin screen
    
    // Constructor for creating a new inventory item
    // name: product name, price: selling price, category: product category, quantity: stock level
    public InventoryItem(String name, double price, String category, int quantity) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.description = "";
    }
    
    // Returns the product name
    public String getName() {
        return name;
    }
    
    // Returns the current selling price
    public double getPrice() {
        return price;
    }
    
    // Updates the selling price
    public void setPrice(double price) {
        this.price = price;
    }
    
    // Returns the product category
    public String getCategory() {
        return category;
    }
    
    // Updates the product category
    public void setCategory(String category) {
        this.category = category;
    }
    
    // Returns the current stock quantity
    public int getQuantity() {
        return quantity;
    }
    
    // Updates the stock quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
