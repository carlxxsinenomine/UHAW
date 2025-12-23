package main;

public class PaintAndSupplies extends Inventory {
    private String color;
    private String finish;
    
    public PaintAndSupplies() {
        super();
    }
    
    public PaintAndSupplies(String itemName, int unitPrice, int itemQuantity, String color, String finish) {
        super(itemName, unitPrice, itemQuantity);
        this.color = color;
        this.finish = finish;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getFinish() {
        return finish;
    }
    
    public void setFinish(String finish) {
        this.finish = finish;
    }
    
    @Override
    public String toString() {
        return "PaintAndSupplies{" +
                "itemName='" + itemName + '\'' +
                ", unitPrice=" + unitPrice +
                ", itemQuantity=" + itemQuantity +
                ", color='" + color + '\'' +
                ", finish='" + finish + '\'' +
                '}';
    }
}
