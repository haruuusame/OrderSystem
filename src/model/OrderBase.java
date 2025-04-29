package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * カートと注文履歴の共通機能(商品リスト管理、合計金額計算)を提供する抽象クラス。
 */
public abstract class OrderBase {

    // ======= Field =======
    protected Map<Integer, OrderLine> itemMap;

    // ======= Constructor =======
    public OrderBase() {
        this.itemMap = new LinkedHashMap<>();
    }
    public OrderBase(Map<Integer, OrderLine> itemMap) {
        // 引数のmapをディープコピー
        Map<Integer, OrderLine> snapshot = new LinkedHashMap<>();
        itemMap.forEach((id, ci) -> snapshot.put(id, ci.copy()));
        this.itemMap = Collections.unmodifiableMap(snapshot);
    }

    // ======= Method =======

    // `itemMap`を`unmodifiableMap`にして返す
    public Map<Integer, OrderLine> asMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    // `itemMap`を`List`にして返す
    public List<OrderLine> asList() {
        return List.copyOf(itemMap.values());
    }

    // リスト内の合計金額を返す
    public int calculateTotalPrice() {
        return itemMap.values().stream().mapToInt(ci -> ci.getMenu().getPrice() * ci.getQuantity()).sum();
    }

    // 商品の個数を返す
    public int getQuantity(int itemId) {
        OrderLine ci = itemMap.get(itemId);
        return (ci != null) ? ci.getQuantity() : 0;
    }
    public int getQuantity(Menu menu) {
        return getQuantity(menu.getItemId());
    }

    // 商品がリスト(マップ)に含まれているか返す 
    public boolean contains(int itemId) {
        return itemMap.containsKey(itemId);
    }
    public boolean contains(Menu menu) {
        return contains(menu.getItemId());
    }

    // マップが空であればtrueを返す
    public boolean isEmpty(){
        return itemMap.size() == 0;
    }

    // マップの項目数を返す
    public int size() {
        return itemMap.size();
    }
    
}
