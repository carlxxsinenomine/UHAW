package main;

public class Tools extends Inventory {
    private String toolType;
    private String brand;
    
    public Tools() {
        super();
    }
    
    public Tools(String itemName, int unitPrice, int itemQuantity, String toolType, String brand) {
        super(itemName, unitPrice, itemQuantity);
        this.toolType = toolType;
        this.brand = brand;
    }
    
    public String getToolType() {
        return toolType;
    }
    
    public void setToolType(String toolType) {
        this.toolType = toolType;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    @Override
    public String toString() {
        return "Tools{" +
                "itemName='" + itemName + '\'' +
                ", unitPrice=" + unitPrice +
                ", itemQuantity=" + itemQuantity +
                ", toolType='" + toolType + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
