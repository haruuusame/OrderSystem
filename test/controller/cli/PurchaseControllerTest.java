package test.controller.cli;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import controller.cli.PurchaseController;
import model.Cart;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;

public class PurchaseControllerTest {

    private List<Menu> menus;
    private Cart cart;
    private PurchaseController purCon;

    // ===== Helper =====
    public PurchaseController dummyController() {
        cart = new Cart(1);
        menus = new ArrayList<>();
        menus.add(new Menu(1, "menu1", 400, 10, "Food"));
        menus.add(new Menu(2, "menu2", 300, 5, "Drink"));
        menus.add(new Menu(3, "menu3", 300, 15, "Side"));
        MenuCatalog catalog = new MenuCatalog(menus);
        return new PurchaseController(cart, catalog);
    }

    @Before
    public void setUp() {
        purCon = dummyController();
    }

    // ===== Tests =====

    @Test
    public void addCartWorks() {
        boolean result = purCon.addCart(1, 2);
        assertTrue(result);
        List<OrderLine> lines = cart.asList();
        assertEquals(1, lines.size());
        assertEquals(2, lines.get(0).getQuantity());
        assertEquals("menu1", lines.get(0).getMenu().getItemName());
    }

    @Test
    public void addCartFailsIfItemNotFound() {
        boolean result = purCon.addCart(99, 1);
        assertFalse(result);
        assertEquals(0, cart.asList().size());
    }

    @Test
    public void checkoutWorks() {
        purCon.addCart(1, 1);
        Optional<Order> orderOpt = purCon.checkout();
        assertTrue(orderOpt.isPresent());
        Order order = orderOpt.get();
        assertEquals(1, order.asList().size());
        assertEquals("menu1", order.asList().get(0).getMenu().getItemName());
    }

    @Test
    public void checkoutFailsIfEmpty() {
        Optional<Order> orderOpt = purCon.checkout();
        assertFalse(orderOpt.isPresent());
    }

    @Test
    public void updateCartItemQuantityWorks() {
        purCon.addCart(1, 1);
        boolean result = purCon.updateCartItemQuantity(1, 5);
        assertTrue(result);
        assertEquals(5, cart.asList().get(0).getQuantity());
    }

    @Test
    public void updateCartItemQuantityFailsIfInvalidId() {
        boolean result = purCon.updateCartItemQuantity(99, 5);
        assertFalse(result);
    }

    @Test
    public void catalogFindByCategoryWorks() {
        List<Menu> result = purCon.catalogFindByCategory("Food");
        assertEquals(1, result.size());
        assertEquals("menu1", result.get(0).getItemName());
    }

    @Test
    public void catalogFindByCategoryReturnsEmptyIfNoMatch() {
        List<Menu> result = purCon.catalogFindByCategory("NonExistCategory");
        assertTrue(result.isEmpty());
    }
}
