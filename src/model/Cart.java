package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public boolean addItem(Menu menu,int quantity) {
        if(quantity <= 0) return false;

        OrderLine ci = itemMap.computeIfAbsent(menu.getItemId(),id -> new OrderLine(menu, 0));
        
        return ci.addQuantity(quantity);
    }

    public boolean removeItem(int itemId) {
        return itemMap.remove(itemId) != null;
    }
    public boolean removeItem(Menu menu) {
        return removeItem(menu.getItemId());
    }

    public boolean updateQuantity(int itemId,int quantity){
        if(quantity < 0) return false;

        OrderLine ci = itemMap.get(itemId);
        if (ci == null) return false;

        if(quantity == 0) {
            return removeItem(itemId);
        }

        return ci.setQuantity(quantity);
    }
    public boolean updateQuantity(Menu menu,int quantity){
        return updateQuantity(menu.getItemId(), quantity);
    }

    public void clearCart() {
        itemMap.clear();
    }
    
    public boolean hasSufficientStockAll() {
        return itemMap.values().stream().allMatch(ci -> ci.getQuantity() <= ci.getMenu().getStockQuantity());
    }

    public boolean hasSufficientStock(int itemId) {
        OrderLine ci = itemMap.get(itemId);
        return ci != null && ci.getQuantity() <= ci.getMenu().getStockQuantity();
    }

    public boolean hasSufficientStock(Menu menu) {
        return hasSufficientStock(menu.getItemId());
    }
    
    public Optional<Order> checkout(){
        // 1) カートが空でないかチェック
        if(isEmpty()) return Optional.empty();

        // 2) 在庫チェック
        if(!hasSufficientStockAll()) return Optional.empty();

        // 3) その時点でのMenu情報で注文内容を確定
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
            // 7) 失敗時の部分ロールバック
            for (OrderLine ci : deducted) {
                Menu m = ci.getMenu();
                m.setStockQuantity(m.getStockQuantity() + ci.getQuantity());
            }

            return Optional.empty();
        }    
    }

}