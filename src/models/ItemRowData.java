package models;

import javax.swing.*;

// Container for managing a single shopping cart item row's UI components
// Holds all UI components related to one item in the shopping cart for easy updates
public class ItemRowData {
    private final String itemName;
    private final JSpinner spinner;
    private final JLabel totalLabel;
    
    // Constructor creating a new item row with UI components
    // itemName: product name, spinner: quantity control, totalLabel: calculated total price
    public ItemRowData(String itemName, JSpinner spinner, JLabel totalLabel) {
        this.itemName = itemName;
        this.spinner = spinner;
        this.totalLabel = totalLabel;
    }
    
    // Returns the item name
    public String getItemName() {
        return itemName;
    }
    
    // Returns the quantity spinner control
    public JSpinner getSpinner() {
        return spinner;
    }
    
    // Returns the total price label
    public JLabel getTotalLabel() {
        return totalLabel;
    }
}
