package controller.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Cart;
import model.DBManager;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;
/**
 * PurchaseViewと他のクラスの間の手続きを行うクラス。
 */
public class OrderSessionController {

    // ======= Field =======
    private Cart cart;
    private MenuCatalog filteredCatalog;
    private MenuCatalog fullCatalog; // DBの代わりに一時的に実装
    private DBManager dbm;
    
    // ======= Constructor =======
    public OrderSessionController(int cartId,String dbname) {
        this.cart = new Cart(cartId);
        this.dbm = new DBManager(dbname);
        dbm.connect();
        this.fullCatalog = dbm.createMenuCatalogAll().get();
        this.filteredCatalog = this.fullCatalog;
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
        // 1) カートが空でないかチェック
        if(cart.isEmpty()) return Optional.empty();

        // 2) その時点でのMenu情報でOrderを生成(itemMapをdeepcopy)
        Map<Integer, OrderLine> orderItem = new LinkedHashMap<>();
        cart.asMap().forEach((id, ci) -> orderItem.put(id, ci.deepcopy()));

        // 3) Orderのビルダー生成
        Order.Builder preOrder = new Order.Builder()
            .itemMap(Collections.unmodifiableMap(orderItem))
            .status(0);

        // 4) DBアクセス
        Optional<Order> opOrder =  dbm.registerOrder(preOrder);

        // 5) カートを空にする
        if(opOrder.isPresent()){
            cart.clearCart();
        }

        return opOrder;
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

    // カタログをフェッチ
    public void fetchCatalog(){
        Optional<MenuCatalog> opCatalog = dbm.createMenuCatalogAll();
        if(opCatalog.isPresent()){
            this.fullCatalog = opCatalog.get();
        }else{
            System.err.println("[エラー] カタログの取得に失敗");
        }
        resetCatalog();
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
