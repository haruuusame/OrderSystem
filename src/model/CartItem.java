package model;

public class CartItem {

    // ======= Field =======
    private Menu menu;
    private int quantity;

    // ======= Constructor =======
    public CartItem(Menu menu,int quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }

    // ======= Getter / Setter =======
    public Menu getMenu() {
        return menu;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean setQuantity(int quantity) {
        if (this.quantity == quantity) return true;
        if (quantity < 0) return false;

        this.quantity = quantity;
        return true;
    }

    public boolean addQuantity(int quantity) {
        if(quantity == 0) return true;
        if(this.quantity + quantity < 0) return false;

        this.quantity += quantity;
        return true;
    }

    // ======= Method =======

    public CartItem copy(){
        //Menuは在庫数を保持するため参照コピー
        return new CartItem(this.getMenu(),this.getQuantity()); 
    }

}
