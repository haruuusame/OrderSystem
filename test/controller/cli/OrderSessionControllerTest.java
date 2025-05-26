package test.controller.cli;

import static org.junit.Assert.*;
import org.junit.*;
import controller.cli.OrderSessionController;
import model.*;

import java.util.*;

public class OrderSessionControllerTest {
    private static final String TEST_DB = "TestMenu.db";
    private static DBManager db;
    private static int burgerId, potatoId, drinkId;

    @BeforeClass
    public static void setupDB() {
        db = new DBManager(TEST_DB);
        db.connect();
        // 必ずクリーンアップ
        try {
            db.con.createStatement().executeUpdate("DELETE FROM order_detail");
            db.con.createStatement().executeUpdate("DELETE FROM order_header");
            db.con.createStatement().executeUpdate("DELETE FROM menu");
        } catch(Exception e) { throw new AssertionError(e); }
        // テスト用メニューを追加
        burgerId = db.addNewMenuItem("バーガー", 400, 10, "Food").get().getItemId();
        potatoId = db.addNewMenuItem("ポテト", 300, 15, "Side").get().getItemId();
        drinkId = db.addNewMenuItem("ドリンク", 200, 20, "Drink").get().getItemId();
    }

    private OrderSessionController controller;

    @Before
    public void setUp() {
        controller = new OrderSessionController(1, TEST_DB);
    }

    @Test
    public void addCartWorks() {
        boolean result = controller.addCart(burgerId, 2);
        assertTrue(result);
        List<OrderLine> lines = controller.getCart().asList();
        assertEquals(1, lines.size());
        assertEquals(2, lines.get(0).getQuantity());
        assertEquals("バーガー", lines.get(0).getMenu().getItemName());
    }

    @Test
    public void addCartFailsIfItemNotFound() {
        boolean result = controller.addCart(9999, 1); // 存在しないitemId
        assertFalse(result);
        assertEquals(0, controller.getCart().asList().size());
    }

    @Test
    public void checkoutWorks() {
        controller.addCart(potatoId, 1);
        Optional<Order> orderOpt = controller.checkout();
        assertTrue(orderOpt.isPresent());
        Order order = orderOpt.get();
        assertEquals(1, order.asList().size());
        assertEquals("ポテト", order.asList().get(0).getMenu().getItemName());
        // カートが空になっていること
        assertTrue(controller.getCart().asList().isEmpty());
    }

    @Test
    public void checkoutFailsIfEmpty() {
        Optional<Order> orderOpt = controller.checkout();
        assertFalse(orderOpt.isPresent());
    }

    @Test
    public void updateCartItemQuantityWorks() {
        controller.addCart(burgerId, 1);
        boolean result = controller.updateCartItemQuantity(burgerId, 5);
        assertTrue(result);
        assertEquals(5, controller.getCart().asList().get(0).getQuantity());
    }

    @Test
    public void updateCartItemQuantityFailsIfInvalidId() {
        boolean result = controller.updateCartItemQuantity(9999, 5);
        assertFalse(result);
    }

    @Test
    public void catalogFindByCategoryWorks() {
        boolean result = controller.catalogFindByCategory("Food");
        assertTrue(result);
        List<Menu> found = controller.getCatalogMenus();
        assertEquals(1, found.size());
        assertEquals("バーガー", found.get(0).getItemName());
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
        controller.addCart(potatoId, 1);
        assertFalse(controller.cartIsEmpty());
        controller.updateCartItemQuantity(potatoId, 0);
        assertTrue(controller.cartIsEmpty());
    }

    @Test
    public void fetchCatalogReflectsDBChange() {
        // 追加でメニューをDBに追加
        int id = db.addNewMenuItem("ナゲット", 350, 10, "Side").get().getItemId();
        // fetchCatalogでカタログを最新化
        controller.fetchCatalog();
        // 全カタログに4品あることを確認
        assertEquals(4, controller.getCatalogMenus().size());
    }

    @AfterClass
    public static void cleanupDB() {
        db.disconnect();
    }
}
