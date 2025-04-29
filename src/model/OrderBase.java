package model;

import java.util.ArrayList;
import java.util.List;

public abstract class OrderBase {

    // ======= Field =======
    protected List<CartItem> items;

    // ======= Constructor =======
    public OrderBase() {
        this.items = new ArrayList<>();
    }

    // ======= Getter / Setter =======
    public List<CartItem> getItems() {
        return items;
    }

    // ======= Method =======
    public int calculateTotalPrice() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getMenu().getPrice() * item.getQuantity();
        }
        return total;
    }

}
