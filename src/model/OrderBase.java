package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class OrderBase {

    // ======= Field =======
    protected Map<Integer, OrderLine> itemMap;

    // ======= Constructor =======
    public OrderBase() {
        this.itemMap = new LinkedHashMap<>();
    }
    public OrderBase(Map<Integer, OrderLine> itemMap) {
        //引数のmapをディープコピー
        Map<Integer, OrderLine> snapshot = new LinkedHashMap<>();
        itemMap.forEach((id, ci) -> snapshot.put(id, ci.copy()));
        this.itemMap = Collections.unmodifiableMap(snapshot);
    }

    // ======= Method =======
    public Map<Integer, OrderLine> asMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    public List<OrderLine> asList() {
        return List.copyOf(itemMap.values());
    }

    public int calculateTotalPrice() {
        return itemMap.values().stream().mapToInt(ci -> ci.getMenu().getPrice() * ci.getQuantity()).sum();
    }

    public int getQuantity(int itemId) {
        OrderLine ci = itemMap.get(itemId);
        return (ci != null) ? ci.getQuantity() : 0;
    }
    public int getQuantity(Menu menu) {
        return getQuantity(menu.getItemId());
    }

    public boolean contains(int itemId) {
        return itemMap.containsKey(itemId);
    }
    public boolean contains(Menu menu) {
        return contains(menu.getItemId());
    }

    public boolean isEmpty(){
        return itemMap.size() == 0;
    }

    public int size() {
        return itemMap.size();
    }
    
}
