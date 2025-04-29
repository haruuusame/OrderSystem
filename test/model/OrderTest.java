package test.model;

import model.CartItem;
import model.Menu;
import model.Order;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class OrderTest {

    private Menu dummyMenu(int id, int price) {
        return new Menu(id, "商品" + id, price, 100, "カテゴリ");
    }

    @Test
    public void orderIdIsSequential() {
        Map<Integer, CartItem> m1 = new LinkedHashMap<>();
        m1.put(1, new CartItem(dummyMenu(1,500),2));
        Order o1 = new Order(m1);

        Map<Integer, CartItem> m2 = new LinkedHashMap<>();
        m2.put(2, new CartItem(dummyMenu(2,300),1));
        Order o2 = new Order(m2);

        assertTrue(o2.getOrderId() > o1.getOrderId());
    }

    @Test
    public void orderDateIsNow() {
        LocalDateTime before = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        Order order = new Order(Map.of());
        LocalDateTime after = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));

        assertFalse(order.getOrderDate().isBefore(before));
        assertFalse(order.getOrderDate().isAfter(after));
    }

    @Test
    public void itemsAreCopied() {
        CartItem original = new CartItem(dummyMenu(1,500),2);
        Map<Integer, CartItem> m = new LinkedHashMap<>();
        m.put(original.getMenu().getItemId(), original);
        Order order = new Order(m);

        List<CartItem> list = order.asList();
        assertEquals(1, list.size());
        assertNotSame(original, list.get(0));
        assertEquals(original.getQuantity(), list.get(0).getQuantity());
        assertEquals(original.getMenu(), list.get(0).getMenu());
    }

    @Test
    public void totalPriceIsCalculatedCorrectly() {
        Map<Integer, CartItem> m = new LinkedHashMap<>();
        m.put(1, new CartItem(dummyMenu(1,500),2));
        m.put(2, new CartItem(dummyMenu(2,300),3));
        Order order = new Order(m);

        int expected = 500*2 + 300*3;
        assertEquals(expected, order.calculateTotalPrice());
    }
}
