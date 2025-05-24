package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 購入が確定した正式な注文履歴を管理するクラス。
 */
public class Order extends OrderBase{

    // ======= Field =======
    private static AtomicInteger orderCount = new AtomicInteger(1);
    private final int orderId;
    private final LocalDateTime orderDate;
    private int status;

    // ======= Constructor =======
    public Order(Map<Integer,OrderLine> itemMap,int orderId,LocalDateTime orderDate, int status) {
        super(itemMap);
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(Map<Integer,OrderLine> itemMap) {
        // orderCount,現在時刻(JST)で初期化
        this(itemMap,orderCount.getAndIncrement(),LocalDateTime.now(ZoneId.of("Asia/Tokyo")), 0);
    }

    // Builderでの生成
    private Order(Builder builder) {
        super(builder.itemMap);
        this.orderId = builder.orderId;
        this.orderDate = builder.orderDate;
        this.status = builder.status;
    }

    // ======= Getter / Setter =======
    public int getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (status < 0 || status > 3) throw new IllegalArgumentException("不正なstatusです");
        this.status = status;
    }

    // ======= Builder Class =======
    public static class Builder {
        private int orderId;
        private LocalDateTime orderDate = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        private int status = 0;
        private Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();

        public Builder orderId(int orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder itemMap(Map<Integer, OrderLine> itemMap) {
            this.itemMap = itemMap;
            return this;
        }
        
        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Order build() {
            return new Order(this);
        }

        public Builder add(OrderLine ol) {
            this.itemMap.put(ol.getMenu().getItemId(), ol);
            return this;
        }

        public int getOrderId() {
            return orderId;
        }

        public Map<Integer, OrderLine> asMap(){
            return Collections.unmodifiableMap(itemMap);
        }

        public List<OrderLine> asList() {
            return List.copyOf(itemMap.values());
        }

        public LocalDateTime getOrderDate() {
            return orderDate;
        }

        public int getStatus() {
            return status;
        }

    }
}
