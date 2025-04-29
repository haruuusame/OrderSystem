package model;

public class Menu{

    // ======= Field =======
    private int itemId;
    private String itemName;
    private int price;
    private int stockQuantity;
    private String category;

    // ======= Constructor =======
    public Menu(int itemId,String itemName,int price,int stockQuantity,String category){
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // ======= Getter / Setter =======
    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getCategory() {
        return category;
    }

    public boolean setStockQuantity(int stockQuantity) {
        if(stockQuantity < 0) return false;
        this.stockQuantity = stockQuantity;
        return true;
    }

    // ======= Other Methods =======
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Menu menu = (Menu) obj;
        return itemId == menu.itemId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(itemId);
    }

}