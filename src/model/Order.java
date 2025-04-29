package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Order extends OrderBase{

    // ======= Field =======
    private static AtomicInteger orderCount = new AtomicInteger(1);
    private int orderId;
    private LocalDateTime orderDate;

    // ======= Constructor =======
    public Order(List<CartItem> items){
        this.orderId = orderCount.getAndIncrement();
        this.orderDate = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        for(CartItem item : items){
            this.items.add(item.copy());
        }
    }

    // ======= Getter / Setter =======
    public int getOrderId() {
        return orderId;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
