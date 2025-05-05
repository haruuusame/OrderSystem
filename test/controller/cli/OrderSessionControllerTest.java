package test.controller.cli;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import controller.cli.OrderSessionController;
import model.Cart;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;

public class OrderSessionControllerTest {

    private List<Menu> menus;
    private Cart cart;
    private OrderSessionController controller;

    // ===== Helper =====
    public OrderSessionController dummyController() {
        cart = new Cart(1);
        menus = new ArrayList<>();
        menus.add(new Menu(1, "menu1", 400, 10, "Food"));
        menus.add(new Menu(2, "menu2", 300, 5, "Drink"));
        menus.add(new Menu(3, "menu3", 300, 15, "Side"));
        MenuCatalog catalog = new MenuCatalog(menus);
        return new OrderSessionController(1, catalog);
    }

    @Before
    public void setUp() {
        controller = dummyController();
    }

    // ===== Tests =====

    @Test
    public void addCartWorks() {
        boolean result = controller.addCart(1, 2);
        assertTrue(result);
        List<OrderLine> lines = controller.getCart().asList();
        assertEquals(1, lines.size());
        assertEquals(2, lines.get(0).getQuantity());
        assertEquals("menu1", lines.get(0).getMenu().getItemName());
    }

    @Test
    public void addCartFailsIfItemNotFound() {
        boolean result = controller.addCart(99, 1);
        assertFalse(result);
        assertEquals(0, cart.asList().size());
    }

    @Test
    public void checkoutWorks() {
        controller.addCart(1, 1);
        Optional<Order> orderOpt = controller.checkout();
        assertTrue(orderOpt.isPresent());
        Order order = orderOpt.get();
        assertEquals(1, order.asList().size());
        assertEquals("menu1", order.asList().get(0).getMenu().getItemName());
    }

    @Test
    public void checkoutFailsIfEmpty() {
        Optional<Order> orderOpt = controller.checkout();
        assertFalse(orderOpt.isPresent());
    }

    @Test
    public void updateCartItemQuantityWorks() {
        controller.addCart(1, 1);
        boolean result = controller.updateCartItemQuantity(1, 5);
        assertTrue(result);
        assertEquals(5, controller.getCart().asList().get(0).getQuantity());
    }

    @Test
    public void updateCartItemQuantityFailsIfInvalidId() {
        boolean result = controller.updateCartItemQuantity(99, 5);
        assertFalse(result);
    }

    @Test
    public void catalogFindByCategoryWorks() {
        boolean result = controller.catalogFindByCategory("Food");
        assertTrue(result);
        assertEquals(1, controller.getCatalogMenus().size());
        assertEquals("menu1", controller.getCatalogMenus().get(0).getItemName());
    }

    @Test
    public void catalogFindByCategoryReturnsEmptyIfNoMatch() {
        boolean result = controller.catalogFindByCategory("NonExistCategory");
        assertTrue(result);
        assertTrue(controller.getCatalogMenus().isEmpty());
    }

    @Test
    public void resetCatalogRestoresAllMenus() {
        controller.catalogFindByCategory("Drink");
        assertEquals(1, controller.getCatalogMenus().size()); // ドリンクのみ

        controller.resetCatalog();
        assertEquals(3, controller.getCatalogMenus().size()); // 全メニュー
    }

    @Test
    public void cartIsEmptyWorks() {
        assertTrue(controller.cartIsEmpty());
        controller.addCart(1,1);
        assertFalse(controller.cartIsEmpty());
        controller.updateCartItemQuantity(1, 0);
        assertTrue(controller.cartIsEmpty());
    }

}
