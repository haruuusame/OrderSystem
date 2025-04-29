package test.model;

import model.CartItem;
import model.Menu;
import model.Order;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.Assert.*;

public class OrderTest {

    private Menu dummyMenu(int id, int price) {
        return new Menu(id, "商品" + id, price, 100, "カテゴリ");
    }

    @Test
    public void orderIdIsSequential() {
        Order order1 = new Order(List.of(new CartItem(dummyMenu(1, 500), 2)));
        Order order2 = new Order(List.of(new CartItem(dummyMenu(2, 300), 1)));
        
        assertTrue(order2.getOrderId() > order1.getOrderId());
    }

    @Test
    public void orderDateIsNow() {
        LocalDateTime before = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
        Order order = new Order(List.of());
        LocalDateTime after = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));

        assertTrue(!order.getOrderDate().isBefore(before));
        assertTrue(!order.getOrderDate().isAfter(after));
    }

    @Test
    public void itemsAreCopied() {
        CartItem original = new CartItem(dummyMenu(1, 500), 2);
        Order order = new Order(List.of(original));
        
        assertEquals(1, order.getItems().size());
        assertNotSame(original, order.getItems().get(0)); // copyされている
        assertEquals(original.getQuantity(), order.getItems().get(0).getQuantity());
        assertEquals(original.getMenu(), order.getItems().get(0).getMenu());
    }

    @Test
    public void totalPriceIsCalculatedCorrectly() {
        Order order = new Order(List.of(
            new CartItem(dummyMenu(1, 500), 2),
            new CartItem(dummyMenu(2, 300), 3)
        ));

        int expected = 500 * 2 + 300 * 3;
        assertEquals(expected, order.calculateTotalPrice());
    }
}
