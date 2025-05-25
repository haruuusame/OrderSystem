package test.model;

import org.junit.*;

import model.DBManager;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;

import static org.junit.Assert.*;
import java.util.*;

public class DBManagerTest {
    static final String TEST_DB = "TestMenu.db";
    static DBManager db;
    static int burgerId, friesId, drinkId;

    @BeforeClass
    public static void setupOnce() {
        db = new DBManager(TEST_DB);
        db.connect();
        // 初回のみ全テーブル初期化＋メニュー初期投入
        try {
            db.con.createStatement().executeUpdate("DELETE FROM order_detail");
            db.con.createStatement().executeUpdate("DELETE FROM order_header");
            db.con.createStatement().executeUpdate("DELETE FROM menu");
        } catch (Exception e) {}
        burgerId = db.addNewMenuItem("ハンバーガー", 350, 10, "Food").get().getItemId();
        friesId = db.addNewMenuItem("ポテト", 180, 15, "Side").get().getItemId();
        drinkId = db.addNewMenuItem("ドリンク", 120, 20, "Drink").get().getItemId();
    }

    @AfterClass
    public static void tearDownOnce() {
        db.disconnect();
    }

    @Before
    public void clearOrders() {
        // 注文だけ毎回クリア
        try {
            db.con.createStatement().executeUpdate("DELETE FROM order_detail");
            db.con.createStatement().executeUpdate("DELETE FROM order_header");
        } catch (Exception e) {}
    }

    @Test
    public void testMenuCatalogFixed() {
        MenuCatalog catalog = db.createMenuCatalogAll().get();
        assertEquals(3, catalog.size());
        assertNotNull(catalog.get(burgerId));
        assertNotNull(catalog.get(friesId));
        assertNotNull(catalog.get(drinkId));
    }

    @Test
    public void testOrderRegisterAndFetch() {
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(burgerId, new OrderLine(db.fetchMenuById(burgerId).get(), 2));
        map.put(friesId, new OrderLine(db.fetchMenuById(friesId).get(), 1));
        Order.Builder builder = new Order.Builder().itemMap(map).status(0);
        Order order = db.registerOrder(builder).get();

        Optional<Order> fetchedOrder = db.fetchOrderById(order.getOrderId());
        assertTrue(fetchedOrder.isPresent());
        assertEquals(order.getOrderId(), fetchedOrder.get().getOrderId());
        assertEquals(2, fetchedOrder.get().asList().size());
    }

    @Test
    public void testOrderStatusAndFetch() {
        // 登録
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(drinkId, new OrderLine(db.fetchMenuById(drinkId).get(), 1));
        Order order0 = db.registerOrder(new Order.Builder().itemMap(map).status(0)).get();
        Order order1 = db.registerOrder(new Order.Builder().itemMap(map).status(0)).get();

        // statusで取得
        assertEquals(2, db.fetchOrdersByStatus(0).stream().count());

        // order1のstatus変更
        db.updateStatusAll(order1.getOrderId(), 1);

        assertEquals(1, db.fetchOrdersByStatus(1).stream().filter(o->o.getOrderId()==order1.getOrderId()).count());
    }

    @Test
    public void testRestockMenuItem() {
        Menu fries = db.fetchMenuById(friesId).get();
        int before = fries.getStockQuantity();
        assertTrue(db.restockMenuItem(friesId, 5));
        Menu updated = db.fetchMenuById(friesId).get();
        assertEquals(before + 5, updated.getStockQuantity());
    }

    @Test
    public void testUpdateStatusAll() {
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(drinkId, new OrderLine(db.fetchMenuById(drinkId).get(), 1));
        Order order = db.registerOrder(new Order.Builder().itemMap(map).status(0)).get();

        db.updateStatusAll(order.getOrderId(), 3); // キャンセル
        Order updated = db.fetchOrderById(order.getOrderId()).get();
        assertEquals(3, updated.getStatus());
    }

    @Test
    public void testErrorCases() {
        assertFalse(db.fetchMenuById(-1).isPresent());
        assertFalse(db.fetchOrderById(-1).isPresent());
        assertFalse(db.restockMenuItem(-99, 10));
    }
}
