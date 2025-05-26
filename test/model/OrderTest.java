package test.model;

import model.OrderLine;
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
    public void testConstructor_setsAllFieldsCorrectly() {
        Map<Integer, OrderLine> m = new LinkedHashMap<>();
        m.put(1, new OrderLine(dummyMenu(1, 500), 2));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        Order order = new Order(m, 77, now, 3);

        assertEquals(77, order.getOrderId());
        assertEquals(now, order.getOrderDate());
        assertEquals(3, order.getStatus());
        assertEquals(1, order.asList().size());
        assertEquals(500*2, order.calculateTotalPrice());
    }

    @Test
    public void testDefaultConstructor_setsSequentialOrderIdAndCurrentTime() {
        Map<Integer, OrderLine> m = new LinkedHashMap<>();
        m.put(2, new OrderLine(dummyMenu(2, 200), 1));
        Order order1 = new Order(m);
        Order order2 = new Order(m);

        assertTrue(order2.getOrderId() > order1.getOrderId());

        LocalDateTime before = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        Order order3 = new Order(m);
        LocalDateTime after = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));

        assertFalse(order3.getOrderDate().isBefore(before));
        assertFalse(order3.getOrderDate().isAfter(after));
    }

    @Test
    public void testBuilder_setsFieldsCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2023, 5, 25, 10, 30);
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(3, new OrderLine(dummyMenu(3, 300), 4));

        Order order = new Order.Builder()
                .orderId(99)
                .orderDate(dt)
                .itemMap(map)
                .status(2)
                .build();

        assertEquals(99, order.getOrderId());
        assertEquals(dt, order.getOrderDate());
        assertEquals(2, order.getStatus());
        assertEquals(1, order.asList().size());
        assertEquals(300*4, order.calculateTotalPrice());
    }

    @Test
    public void testBuilder_addMethodAndAsListAsMap() {
        OrderLine ol1 = new OrderLine(dummyMenu(5, 123), 1);
        OrderLine ol2 = new OrderLine(dummyMenu(6, 222), 2);
        Order.Builder builder = new Order.Builder();
        builder.add(ol1).add(ol2);

        Map<Integer, OrderLine> map = builder.asMap();
        assertEquals(2, map.size());
        List<OrderLine> list = builder.asList();
        assertEquals(2, list.size());
        assertTrue(map.containsKey(ol1.getMenu().getItemId()));
        assertTrue(map.containsKey(ol2.getMenu().getItemId()));
    }

    @Test
    public void testItemsAreCopied() {
        OrderLine original = new OrderLine(dummyMenu(10, 800), 2);
        Map<Integer, OrderLine> m = new LinkedHashMap<>();
        m.put(original.getMenu().getItemId(), original);
        Order order = new Order(m);

        List<OrderLine> list = order.asList();
        assertEquals(1, list.size());
        assertNotSame(original, list.get(0)); // コピーであること
        assertEquals(original.getQuantity(), list.get(0).getQuantity());
        assertEquals(original.getMenu(), list.get(0).getMenu());
    }

    @Test
    public void testTotalPriceIsCalculatedCorrectly() {
        Map<Integer, OrderLine> m = new LinkedHashMap<>();
        m.put(1, new OrderLine(dummyMenu(1, 1000), 1));
        m.put(2, new OrderLine(dummyMenu(2, 250), 4));
        Order order = new Order(m);

        int expected = 1000 + 250*4;
        assertEquals(expected, order.calculateTotalPrice());
    }

    @Test
    public void testSetStatusAcceptsValidAndRejectsInvalid() {
        Order order = new Order(new LinkedHashMap<>());
        order.setStatus(0);
        order.setStatus(3);
        assertEquals(3, order.getStatus());

        // 不正値は例外
        try {
            order.setStatus(-1);
            fail("Exception not thrown for invalid status");
        } catch (IllegalArgumentException ignored) {}
        try {
            order.setStatus(4);
            fail("Exception not thrown for invalid status");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void testBuilderDefaultValues() {
        Order order = new Order.Builder().build();
        assertEquals(0, order.getStatus());
        assertTrue(order.asList().isEmpty());
        assertNotNull(order.getOrderDate());
    }
}
