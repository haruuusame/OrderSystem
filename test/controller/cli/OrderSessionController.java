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
public class OrderSessionController {

    // ======= Field =======
    private Cart cart;
    private MenuCatalog filteredCatalog;
    private MenuCatalog fullCatalog; // DBの代わりに一時的に実装
    
    // ======= Constructor =======
    public OrderSessionController(int cartId,MenuCatalog fullCatalog) {
        this.cart = new Cart(cartId);
        this.fullCatalog = fullCatalog;
        this.filteredCatalog = fullCatalog;
    }

    // ======= Method =======

    // Viewから受け取ったカート追加要求をCartクラスで実行する
    public boolean addCart(int itemId, int quantity) {
        if (!fullCatalog.has(itemId)) return false;
        Menu menu = fullCatalog.get(itemId);
        return cart.addItem(menu, quantity);
    }

    // Viewから受け取ったチェックアウト処理をCartクラスで実行する
    public Optional<Order> checkout() {
        Optional<Order> checkout = cart.checkout();
        return checkout;
    }

    // Viewから受け取ったカート変更処理をCartクラスで実行する
    public boolean updateCartItemQuantity(int itemId, int quantity) {
        if (!fullCatalog.has(itemId)) return false;
        return cart.updateQuantity(itemId, quantity);
    }

    // Viewから受け取ったメニューカタログ更新処理をMenuCatalogクラスで実行する
    public boolean catalogFindByCategory(String category_filter) {
        List<Menu> menus = fullCatalog.findByCategory(category_filter);
        filteredCatalog = new MenuCatalog(menus);
        return true;
    } 

    // カタログをリセット
    public void resetCatalog(){
        this.filteredCatalog = fullCatalog;
    }

    // メニューリストを返す
    public List<Menu> getCatalogMenus() {
        return filteredCatalog.getAll();
    }

    // カートを返す(view側では編集しない)
    public Cart getCart() {
        return cart;
    }

    // カートが空であればtrueを返す
    public boolean cartIsEmpty(){
        return cart.isEmpty();
    }


}
