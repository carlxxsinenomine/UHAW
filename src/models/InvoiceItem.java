package models;

// Represents a single line item in an invoice or purchase receipt
// Captures details of a purchased product including quantity, unit price, and total amount
public class InvoiceItem {
    private String description;
    private int qty;
    private double unitPrice;
    private double amount;
    
    // Constructor for creating a new invoice line item
    // description: product name, qty: quantity purchased, unitPrice: price per unit, amount: total cost
    public InvoiceItem(String description, int qty, double unitPrice, double amount) {
        this.description = description;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }
    
    // Returns the product description
    public String getDescription() {
        return description;
    }
    
    // Updates the product description
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Returns the quantity purchased
    public int getQty() {
        return qty;
    }
    
    // Updates the quantity purchased
    public void setQty(int qty) {
        this.qty = qty;
    }
    
    // Returns the unit price
    public double getUnitPrice() {
        return unitPrice;
    }
    
    /**
     * Updates the unit price.
     * @param unitPrice the new unit price
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    /**
     * Gets the total amount for this line item.
     * @return the total cost (quantity × unit price)
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Updates the total amount for this line item.
     * @param amount the new total amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
