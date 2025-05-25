package test.model;

import org.junit.*;

import model.DBManager;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
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

    @Test
    public void testMenuCatalogByCategory() {
        assertTrue(db.createMenuCatalog("Food").isPresent());
        assertEquals(1, db.createMenuCatalog("Food").get().size());
        assertEquals(0, db.createMenuCatalog("NonExist").get().size());
    }
    @Test
    public void testFetchMenuByIdBoundary() {
        // 0や大きな値など
        assertFalse(db.fetchMenuById(0).isPresent());
        assertFalse(db.fetchMenuById(Integer.MAX_VALUE).isPresent());
    }
    @Test
    public void testOrderRegisterEmpty() {
        Order.Builder builder = new Order.Builder().status(0);
        assertFalse(db.registerOrder(builder).isPresent());
    }
    @Test
    public void testOrderRegisterOutOfStock() {
        Menu burger = db.fetchMenuById(burgerId).get();
        // 在庫以上の数量
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(burgerId, new OrderLine(burger, burger.getStockQuantity() + 100));
        Order.Builder builder = new Order.Builder().itemMap(map).status(0);
        assertFalse(db.registerOrder(builder).isPresent());
        // 在庫が減っていないこと
        int now = db.fetchMenuById(burgerId).get().getStockQuantity();
        assertEquals(burger.getStockQuantity(), now);
    }
    @Test
    public void testFetchOrdersAllEmpty() {
        // すべての注文削除済み前提
        assertEquals(0, db.fetchOrdersAll().size());
    }
    @Test
    public void testFetchOrdersByDateTimeRangeOutOfRange() {
        assertEquals(0, db.fetchOrdersByDateTimeRange(
            LocalDateTime.of(2099, 1, 1, 0, 0),
            LocalDateTime.of(2099, 12, 31, 23, 59)).size());
    }
    @Test
    public void testRestockMenuItemZeroAndNegative() {
        int before = db.fetchMenuById(friesId).get().getStockQuantity();
        assertTrue(db.restockMenuItem(friesId, 0)); // 変化なし
        assertEquals(before, db.fetchMenuById(friesId).get().getStockQuantity());
        assertTrue(db.restockMenuItem(friesId, -2)); // 減少許容なら
        assertEquals(before - 2, db.fetchMenuById(friesId).get().getStockQuantity());
    }
    @Test
    public void testUpdateStatusAllInvalidOrder() {
        // 存在しないorderId
        db.updateStatusAll(-999, 9);
        // 例外が起きずcatchされてエラー出力されるだけならOK
    }
    @Test
    public void testOrderRegisterMultipleSameItem() {
        Menu menu = db.fetchMenuById(burgerId).get();
        Map<Integer, OrderLine> map = new LinkedHashMap<>();
        map.put(burgerId, new OrderLine(menu, 1));
        map.put(burgerId, new OrderLine(menu, 3)); // 上書きになるか確認
        Order.Builder builder = new Order.Builder().itemMap(map).status(0);
        Order order = db.registerOrder(builder).get();
        assertEquals(1, order.asList().size()); // 最後のだけが有効
    }
    @Test
    public void testFetchOrdersByStatusAndDateTimeNoResult() {
        assertEquals(0, db.fetchOrdersByStatusAndDateTime(
            999, LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.of(2000, 1, 2, 0, 0)).size());
    }
    @Test
    public void testOperationAfterDisconnect() {
        db.disconnect();
        // エラーをcatchして止まらないことを確認
        assertFalse(db.fetchMenuById(burgerId).isPresent());
        db.connect(); // 再接続で他テストに影響しないように
    }
    @Test
    public void testAddMenuWithLongName() {
        String longName = "🍔".repeat(100);
        Optional<Menu> opt = db.addNewMenuItem(longName, 999, 1, "Special");
        assertTrue(opt.isPresent());
    }

}
