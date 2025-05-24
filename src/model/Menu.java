package model;
/**
 * 商品情報を管理するクラス。
 * 商品番号、商品名、値段、在庫数、商品カテゴリといった属性を持つ。
 */
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
        // stockQuantityは0以上の値を受け付ける
        if(stockQuantity < 0) return false;
        this.stockQuantity = stockQuantity;
        return true;
    }
    // ======= Method =======
    
    // Menuクラスをディープコピー
    public Menu copy(){
        return new Menu(itemId,itemName,price,stockQuantity,category);
    }

    // ======= Other Methods =======
    
    //Menuクラスの区別はitemIdで行う
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