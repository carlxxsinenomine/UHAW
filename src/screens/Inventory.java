package screens;

import javax.swing.*;

public class Inventory extends JFrame {
    String itemName;
    int unitPrice, itemQuantity;

    public Inventory(){}

    public Inventory(String itemName, int unitPrice, int itemQuantity){
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.itemQuantity = itemQuantity;
    }
    public String getItemName(){
        return itemName;
    }
    public int getPrice(){
        return unitPrice;
    }
    public int getItemAmount(){
        return itemQuantity;
    }
}
