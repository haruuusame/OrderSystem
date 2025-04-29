package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cart extends OrderBase {

    // ======= Field =======
    private int cartId;
    private Map<Menu,CartItem> menuItems = new HashMap<>(); //参照の高速化のためのフィールド

    // ======= Constructor =======
    public Cart(int cartId) {
        super();
        this.cartId = cartId;
    }

    // ======= Getter / Setter =======
    public int getCartId() {
        return cartId;
    }

    // ======= Method =======
    public boolean addItem(Menu menu,int quantity) {
        if(quantity <= 0) return false;
        CartItem item = menuItems.get(menu);
        if(item == null) {
            // 初回追加
            item = new CartItem(menu,quantity);
            menuItems.put(menu,item);
            items.add(item);
        } else if(!item.addQuantity(quantity)) {
            // 数量加算に失敗
            return false;
        }
        return true;
    }

    public boolean changeItemQuantity(Menu menu,int quantity){
        CartItem item = menuItems.get(menu);

        if(item == null) return false;
        if(quantity < 0) return false;
        if(quantity == 0){
            removeItem(item);
            return true;
        }

        return item.setQuantity(quantity);
    }

    public Optional<Order> checkout(){
        for(CartItem item:items){
            if(item.getQuantity() > item.getMenu().getStockQuantity()) {
                return Optional.empty();
            }
        }

        for(CartItem item:items){
            Menu menu = item.getMenu();
            menu.setStockQuantity(menu.getStockQuantity() - item.getQuantity());
        }

        Order order = new Order(items);
        items.clear();
        menuItems.clear();
        return Optional.of(order);
    }

    // カートの中の item を削除し、 menuItems からも削除する
    private void removeItem(CartItem item){
        items.remove(item);
        menuItems.remove(item.getMenu());
    }

}