package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Order extends OrderBase{

    // ======= Field =======
    private static AtomicInteger orderCount = new AtomicInteger(1);
    private final int orderId;
    private final LocalDateTime orderDate;

    // ======= Constructor =======
    public Order(Map<Integer,CartItem> itemMap,int orderId,LocalDateTime orderDate) {
        super(itemMap);
        this.orderId = orderId;
        this.orderDate = orderDate;
    }

    public Order(Map<Integer,CartItem> itemMap) {
        this(itemMap,orderCount.getAndIncrement(),LocalDateTime.now(ZoneId.of("Asia/Tokyo")));
    }

    // ======= Getter / Setter =======
    public int getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
