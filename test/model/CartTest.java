package test.model;

import model.Cart;
import model.OrderLine;
import model.Menu;
import model.Order;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.Assert.*;

public class CartTest {

    private Menu dummyMenu() {
        return new Menu(1, "テストメニュー", 500, 20, "Food");
    }

    @Test
    public void addItemNewItemAddsSuccessfully() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertTrue(cart.addItem(menu, 2));
        List<OrderLine> items = cart.asList();
        assertEquals(1, items.size());
        assertEquals(2, cart.asList().get(0).getQuantity());
    }

    @Test
    public void addItemExistingItemAddsQuantity() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        cart.addItem(menu, 2);
        assertTrue(cart.addItem(menu, 3));
        assertEquals(1, cart.asList().size());
        assertEquals(5, cart.asList().get(0).getQuantity());
    }

    @Test
    public void addItemInvalidQuantityFails() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertFalse(cart.addItem(menu, 0));
        assertFalse(cart.addItem(menu, -1));
        assertTrue(cart.asList().isEmpty());
    }

    @Test
    public void removeItemWorksCorrectly() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        // 正常に削除される
        assertTrue(cart.removeItem(menu));
        assertTrue(cart.isEmpty());

        // 存在しないものの削除は false
        assertFalse(cart.removeItem(menu));
    }

    @Test
    public void updateQuantityUpdatesQuantity() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        assertTrue(cart.updateQuantity(menu, 5));
        assertTrue(cart.updateQuantity(menu.getItemId(), 5));
        assertEquals(5, cart.asList().get(0).getQuantity());
    }

    @Test
    public void updateQuantityRemoveWhenZero() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        assertTrue(cart.updateQuantity(menu, 0));
        cart.addItem(menu, 2);
        assertTrue(cart.updateQuantity(menu.getItemId(), 0));
        assertTrue(cart.asList().isEmpty());
    }

    @Test
    public void updateQuantityInvalidCases() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();

        assertFalse(cart.updateQuantity(menu, 1));  // 存在しない
        cart.addItem(menu, 2);
        assertFalse(cart.updateQuantity(menu, -1)); // 負の数量
    }

    @Test
    public void clearCartEmptiesTheCart() {
        Cart cart = new Cart(1);
        cart.addItem(dummyMenu(), 5);
        assertFalse(cart.isEmpty());

        cart.clearCart();
        assertTrue(cart.isEmpty());
    }


    @Test
    public void checkoutSuccessfulWhenStockIsEnough() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 2);

        Optional<Order> opt = cart.checkout();
        assertTrue(opt.isPresent());
        Order order = opt.get();

        assertEquals(1, order.asList().size());
        assertEquals(500 * 2, order.calculateTotalPrice());
        assertEquals(18, menu.getStockQuantity());
        assertTrue(cart.asList().isEmpty());
    }

    @Test
    public void checkoutFailsWhenStockIsInsufficient() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        cart.addItem(menu, 100);

        Optional<Order> opt = cart.checkout();
        assertFalse(opt.isPresent());

        assertEquals(1, cart.asList().size());
        assertEquals(20, menu.getStockQuantity());
    }

    @Test
    public void checkoutFailsWhenCartIsEmpty() {
        Cart cart = new Cart(1);
        Optional<Order> opt = cart.checkout();

        assertFalse(opt.isPresent());
    }

    @Test
    public void checkisEmptyIsCorrect() {
        Cart cart = new Cart(1);
        
        // カートは空
        assertTrue(cart.isEmpty());

        // カートは空でない
        cart.addItem(dummyMenu(),1);
        assertFalse(cart.isEmpty());
    }

    @Test
    public void checkhasSufficientStockAllIsCorrect() {
        Cart cart = new Cart(1);
        Menu menu = new Menu(2,"name",100,10,"Food");
        
        // カートが空の時はtrue
        assertTrue(cart.isEmpty());

        // カートの中身がすべて在庫数以下なのでtrue
        cart.addItem(dummyMenu(),20);
        assertTrue(cart.hasSufficientStockAll());
        
        // カートの中身に在庫数より大きいものが存在するのでfalse
        cart.addItem(menu,20);
        assertFalse(cart.hasSufficientStockAll());

        // カートの中身がすべて在庫数以下なのでtrue
        cart.updateQuantity(menu,4);
        assertTrue(cart.hasSufficientStockAll());

    }

    @Test
    public void checkhasSufficientStockIsCorrect() {
        Cart cart = new Cart(1);
        Menu menu = new Menu(2,"name",100,10,"Food");

        cart.addItem(dummyMenu(),20);
        // カートの中身が在庫数以下なのでtrue
        assertTrue(cart.hasSufficientStock(dummyMenu().getItemId()));
        
        cart.addItem(menu,20);
        assertTrue(cart.hasSufficientStock(dummyMenu()));
        // カートの中身が在庫数より多いのでfalse
        assertFalse(cart.hasSufficientStock(menu));

        cart.updateQuantity(menu,4);
        assertTrue(cart.hasSufficientStock(menu));

    }

    //OrderBaseのテスト項目
    
    @Test
    public void asMapAndAsListReturnCorrectData() {
        Cart cart = new Cart(1);
        Menu menu1 = dummyMenu();
        Menu menu2 = new Menu(2, "サイド", 300, 10, "Side");

        cart.addItem(menu1, 1);
        cart.addItem(menu2, 2);

        Map<Integer, OrderLine> map = cart.asMap();
        List<OrderLine> list = cart.asList();

        assertEquals(2, map.size());
        assertTrue(map.containsKey(menu1.getItemId()));
        assertTrue(map.containsKey(menu2.getItemId()));

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(ci -> ci.getMenu().equals(menu1)));
        assertTrue(list.stream().anyMatch(ci -> ci.getMenu().equals(menu2)));
    }
    
    @Test
    public void calculateTotalPriceIsCorrect() {
        Cart cart = new Cart(1);
        cart.addItem(dummyMenu(), 2); // 500 * 2 = 1000
        cart.addItem(new Menu(2, "ドリンク", 200, 10, "Drink"), 3); // 200 * 3 = 600

        assertEquals(1600, cart.calculateTotalPrice());
    }

    @Test
    public void getQuantityReturnsCorrectValue() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        Menu notAdded = new Menu(99, "未追加", 100, 5, "Other");

        cart.addItem(menu, 4);
        assertEquals(4, cart.getQuantity(menu));
        assertEquals(4, cart.getQuantity(menu.getItemId()));

        assertEquals(0, cart.getQuantity(notAdded));
        assertEquals(0, cart.getQuantity(notAdded.getItemId()));
    }

    @Test
    public void containsWorksCorrectly() {
        Cart cart = new Cart(1);
        Menu menu = dummyMenu();
        Menu notAdded = new Menu(2, "別商品", 400, 5, "Other");

        assertFalse(cart.contains(menu));
        assertFalse(cart.contains(menu.getItemId()));

        cart.addItem(menu, 1);

        assertTrue(cart.contains(menu));
        assertTrue(cart.contains(menu.getItemId()));
        assertFalse(cart.contains(notAdded));
        assertFalse(cart.contains(notAdded.getItemId()));
    }

    @Test
    public void sizeReflectsNumberOfItems() {
        Cart cart = new Cart(1);
        assertEquals(0, cart.size());

        cart.addItem(dummyMenu(), 2);
        assertEquals(1, cart.size());

        cart.addItem(new Menu(2, "別の商品", 300, 15, "Drink"), 1);
        assertEquals(2, cart.size());
    }




    


}
