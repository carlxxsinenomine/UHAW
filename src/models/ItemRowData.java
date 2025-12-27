package models;

import javax.swing.*;

/**
 * ItemRowData - Container for managing a single shopping cart item row's UI components.
 * 
 * This class holds all the UI components related to one item in the shopping cart,
 * enabling efficient updates when customers change quantities or prices. By grouping
 * these components together, we can easily locate and update the correct row when
 * needed without searching the entire cart interface.
 * 
 * Components Managed:
 * - Item Name: The product name displayed in the row
 * - Quantity Spinner: The numeric input control for changing purchase quantity
 * - Total Label: The calculated total price for this item (quantity × unit price)
 * 
 * Design Pattern:
 * This class uses composition to associate related UI components, making it easier
 * to maintain the cart state and respond to user interactions like quantity changes.
 */
public class ItemRowData {
    private final String itemName;
    private final JSpinner spinner;
    private final JLabel totalLabel;
    
    /**
     * Creates a new item row container with UI components.
     *
     * @param itemName the name of the product in this cart row
     * @param spinner the quantity spinner control for this item
     * @param totalLabel the label showing the total price for this item
     */
    public ItemRowData(String itemName, JSpinner spinner, JLabel totalLabel) {
        this.itemName = itemName;
        this.spinner = spinner;
        this.totalLabel = totalLabel;
    }
    
    /**
     * Gets the name of the item in this row.
     * @return the item name
     */
    public String getItemName() {
        return itemName;
    }
    
    /**
     * Gets the quantity spinner control for this row.
     * @return the spinner component
     */
    public JSpinner getSpinner() {
        return spinner;
    }
    
    /**
     * Gets the total price label for this row.
     * @return the label showing the item's total cost
     */
    public JLabel getTotalLabel() {
        return totalLabel;
    }
}
