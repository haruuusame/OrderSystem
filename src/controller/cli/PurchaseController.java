package controller.cli;

import java.util.List;
import java.util.Optional;

import model.Cart;
import model.Menu;
import model.MenuCatalog;
import model.Order;
/**
 * PurchaseViewと他のクラスの間の手続きを行うクラス。
 */
public class PurchaseController {

    // ======= Field =======
    private Cart cart;
    private MenuCatalog catalog;
    
    // ======= Constructor =======
    public PurchaseController(Cart cart,MenuCatalog catalog) {
        this.cart = cart;
        this.catalog = catalog;
    }

    // ======= Method =======

    // 受け付けたカート追加処理をCartクラスに渡す
    public boolean addCart(int itemId, int quantity) {
        if (!catalog.has(itemId)) return false;
        Menu menu = catalog.get(itemId);
        return cart.addItem(menu, quantity);
    }

    // 受け付けたチェックアウト処理をCartクラスに渡す
    public Optional<Order> checkout() {
        Optional<Order> checkout = cart.checkout();
        return checkout;
    }

    // 受け付けたカート変更処理をCartクラスに渡す
    public boolean updateCartItemQuantity(int itemId, int quantity) {
        if (!catalog.has(itemId)) return false;
        return cart.updateQuantity(itemId, quantity);
    }

    // 受け付けたメニューカタログ更新処理をMenuCatalogクラスに渡す
    public List<Menu> catalogFindByCategory(String category_filter) {
        return catalog.findByCategory(category_filter);
    } 

    


}
