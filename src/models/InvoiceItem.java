package models;

/**
 * InvoiceItem - Represents a single line item in an invoice or purchase receipt.
 * 
 * This class captures the details of a purchased product as it appears on an invoice,
 * including what was bought, how much was purchased, the unit price, and the total
 * amount charged. This data is essential for transaction records and purchase history.
 * 
 * Key Attributes:
 * - Description: The name or description of the product purchased
 * - Quantity: How many units of the product were purchased
 * - Unit Price: The price per unit at the time of purchase
 * - Amount: The total cost (quantity × unit price)
 * 
 * Usage:
 * This class is used when creating invoices, recording purchase transactions, and
 * displaying purchase history to customers. Each invoice may contain multiple InvoiceItems.
 */
public class InvoiceItem {
    private String description;
    private int qty;
    private double unitPrice;
    private double amount;
    
    /**
     * Creates a new invoice line item with the specified purchase details.
     *
     * @param description the name or description of the product purchased
     * @param qty the quantity of units purchased
     * @param unitPrice the price per unit at the time of purchase
     * @param amount the total amount for this line item
     */
    public InvoiceItem(String description, int qty, double unitPrice, double amount) {
        this.description = description;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }
    
    /**
     * Gets the product description.
     * @return the description of the purchased item
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Updates the product description.
     * @param description the new product description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the quantity purchased.
     * @return the number of units purchased
     */
    public int getQty() {
        return qty;
    }
    
    /**
     * Updates the quantity purchased.
     * @param qty the new quantity
     */
    public void setQty(int qty) {
        this.qty = qty;
    }
    
    /**
     * Gets the unit price.
     * @return the price per unit at time of purchase
     */
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
