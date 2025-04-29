package test.model;

import model.Cart;
import model.Menu;
import model.Order;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class CartTest {

    private Menu dummyMenu() {
        return new Menu(1, "テストメニュー", 500, 20, "Food");
    }

    @Test
    public void addItem_NewItem_AddsSuccessfully() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertTrue(cart.addItem(menu, 2));
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void addItem_ExistingItem_AddsQuantity() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        cart.addItem(menu, 2);
        assertTrue(cart.addItem(menu, 3));
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void addItem_InvalidQuantity_Fails() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertFalse(cart.addItem(menu, 0));
        assertFalse(cart.addItem(menu, -1));
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void changeItemQuantity_UpdatesQuantity() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        assertTrue(cart.changeItemQuantity(menu, 5));
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void changeItemQuantity_RemoveWhenZero() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        assertTrue(cart.changeItemQuantity(menu, 0));
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void changeItemQuantity_InvalidCases() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertFalse(cart.changeItemQuantity(menu, 1));  // 追加されてない
        cart.addItem(menu, 2);
        assertFalse(cart.changeItemQuantity(menu, -1)); // 負の数量
    }

    @Test
    public void checkout_SuccessfulWhenStockIsEnough() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);
    
        Optional<Order> opt = cart.checkout();
        assertTrue(opt.isPresent());      // Optionalの中身が存在するか確認
        Order order = opt.get();           // getで安全に取り出す
    
        assertEquals(1, order.getItems().size());
        assertEquals(500 * 2, order.calculateTotalPrice());
        assertEquals(18, menu.getStockQuantity()); // 在庫が減った
        assertEquals(0, cart.getItems().size());   // カートは空
    }
    
    @Test
    public void checkout_FailsWhenStockIsInsufficient() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 100);
    
        Optional<Order> opt = cart.checkout();
        assertFalse(opt.isPresent());    // 失敗時は取り出し禁止（getしない）
    
        assertEquals(1, cart.getItems().size());   // カートそのまま
        assertEquals(20, menu.getStockQuantity()); // 在庫もそのまま
    }
    
}
