package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * 購入確定前の仮の注文データを管理するクラス。
 * 商品を追加・数量変更できる。
 */
public class Cart extends OrderBase {

    // ======= Field =======
    private int cartId;

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

    // 同じ商品があれば数量加算、なければ新規追加。
    public boolean addItem(Menu menu,int quantity) {
        if(quantity <= 0) return false;

        // カートに入っていない商品であれば、OrderLineに個数0で追加する
        OrderLine ci = itemMap.computeIfAbsent(menu.getItemId(),id -> new OrderLine(menu, 0));
        
        // quantity加算する
        return ci.addQuantity(quantity);
    }

    // カートから商品を削除する。成功したらtrueを返す
    public boolean removeItem(int itemId) {
        return itemMap.remove(itemId) != null;
    }
    public boolean removeItem(Menu menu) {
        return removeItem(menu.getItemId());
    }

    // カートの商品の個数を`quantity`に変更する。成功したらtrueを返す。
    public boolean updateQuantity(int itemId,int quantity){
        if(quantity < 0) return false;

        OrderLine ci = itemMap.get(itemId);
        if (ci == null) return false;

        // quantityが0の時は商品を削除とみなす
        if(quantity == 0) {
            return removeItem(itemId);
        }

        return ci.setQuantity(quantity);
    }
    public boolean updateQuantity(Menu menu,int quantity){
        return updateQuantity(menu.getItemId(), quantity);
    }

    // カートをクリアする
    public void clearCart() {
        itemMap.clear();
    }

    // カート内の全ての商品が在庫数以下になっていればtrueを返す。
    public boolean hasSufficientStockAll() {
        return itemMap.values().stream().allMatch(ci -> ci.getQuantity() <= ci.getMenu().getStockQuantity());
    }

    // カート内の特定の商品が在庫数以下になっていればtrueを返す。
    public boolean hasSufficientStock(int itemId) {
        OrderLine ci = itemMap.get(itemId);
        return ci != null && ci.getQuantity() <= ci.getMenu().getStockQuantity();
    }
    public boolean hasSufficientStock(Menu menu) {
        return hasSufficientStock(menu.getItemId());
    }
    
    // カートの中身を確定し、Orderオブジェクトに変換する。その際、在庫のチェックも行う。
    public Optional<Order> checkout(){
        // 1) カートが空でないかチェック
        if(isEmpty()) return Optional.empty();

        // 2) 在庫チェック
        if(!hasSufficientStockAll()) return Optional.empty();

        // 3) その時点でのMenu情報で注文内容を確定(itemMapをdeepcopy)
        Map<Integer, OrderLine> orderItem = new LinkedHashMap<>();
        itemMap.forEach((id, ci) -> orderItem.put(id, ci.deepcopy()));

        List<OrderLine> deducted = new ArrayList<>();
        try {
            // 4) 在庫一括引き落とし
            for (OrderLine ci : itemMap.values()) {
                Menu m = ci.getMenu();
                if(!m.setStockQuantity(m.getStockQuantity() - ci.getQuantity())) throw new IllegalArgumentException("在庫不足");
                deducted.add(ci);
            }
            // 5) Order生成
            Order order = new Order(Collections.unmodifiableMap(orderItem));

            // 6) カートを空にする
            clearCart();

            return Optional.of(order);
        } catch (RuntimeException ex) {
            // 7) 例外が発生した場合、一部在庫が引き落とされた可能性があるため、ロールバックを行う
            for (OrderLine ci : deducted) {
                Menu m = ci.getMenu();
                m.setStockQuantity(m.getStockQuantity() + ci.getQuantity());
            }

            return Optional.empty();
        }    
    }

}