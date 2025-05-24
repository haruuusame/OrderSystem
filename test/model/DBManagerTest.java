package test.model;

import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class DBManagerTest {
    private DBManager db;

    @Before
    public void setUp() {
        db = new DBManager("TestMenu.db");
        db.connect();
    }

    @After
    public void tearDown() {
        db.disconnect();
    }

    @Test
    public void testAddNewMenuItemAndCatalogRetrieval() {
        Menu added = db.addNewMenuItem("テスト商品", 1200, 10, "デザート").get();
        assertNotNull(added);
        assertEquals("テスト商品", added.getItemName());

        MenuCatalog catalog = db.createMenuCatalogAll().get();
        assertNotNull(catalog);
        assertTrue(catalog.has(added.getItemId()));
    }

    @Test
    public void testRestockMenuItemSuccess() {
        Menu m = db.addNewMenuItem("在庫補充", 800, 1, "惣菜").get();
        boolean result = db.restockMenuItem(m.getItemId(), 5);
        assertTrue(result);
    }

    @Test
    public void testRestockMenuItemFailure() {
        // 存在しないitemId
        boolean result = db.restockMenuItem(-9999, 5);
        assertFalse(result);
    }

    @Test
    public void testRegisterOrderSuccess() {
        Menu m = db.addNewMenuItem("注文成功商品", 300, 10, "飲料").get();
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 2));
        Order.Builder order = new Order.Builder().itemMap(itemMap).status(0);

        Order registered = db.registerOrder(order).get();
        assertNotNull(registered);
    }

    @Test
    public void testRegisterOrderFailureByStock() {
        Menu m = db.addNewMenuItem("在庫不足商品", 400, 1, "主菜").get();
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 999));
        Order.Builder order = new Order.Builder().itemMap(itemMap).status(0);
        
        Optional<Order> result = db.registerOrder(order);
        assertEquals(Optional.empty(),result);
    }

    @Test
    public void testUpdateStatusAndHeaderSync() {
        Menu m = db.addNewMenuItem("ステータス商品", 500, 3, "その他").get();
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 1));
        Order.Builder order = new Order.Builder().itemMap(itemMap).status(0);
        Order registered = db.registerOrder(order).get();
        assertNotNull(registered);

        db.updateStatus(registered.getOrderId(), m.getItemId(), 2);  // 2 = 完了
    }

    @Test
    public void testUpdateStatusAll() {
        Menu m1 = db.addNewMenuItem("一括ステータス1", 600, 2, "軽食").get();
        Menu m2 = db.addNewMenuItem("一括ステータス2", 700, 2, "軽食").get();

        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m1.getItemId(), new OrderLine(m1, 1));
        itemMap.put(m2.getItemId(), new OrderLine(m2, 1));
        Order.Builder order = new Order.Builder().itemMap(itemMap).status(0);

        Order registered = db.registerOrder(order).get();
        assertNotNull(registered);

        db.updateStatusAll(registered.getOrderId(), 1);  // 全明細を処理中に
    }
    @Test
    public void testOrderMultipleItems() {
        Menu burger = db.addNewMenuItem("ハンバーガー", 500, 5, "主菜").get();
        Menu fries = db.addNewMenuItem("ポテト", 300, 10, "サイド").get();
        Menu coffee = db.addNewMenuItem("コーヒー", 200, 8, "飲料").get();

        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(burger.getItemId(), new OrderLine(burger, 2));
        itemMap.put(fries.getItemId(), new OrderLine(fries, 3));
        itemMap.put(coffee.getItemId(), new OrderLine(coffee, 1));

        Order.Builder order = new Order.Builder().itemMap(itemMap).status(0);
        Order registered = db.registerOrder(order).get();

        assertNotNull(registered);
        assertEquals(3, registered.asMap().size());
    }

    @Test
    public void testFetchOrderByStatus() {
        Menu burger = db.addNewMenuItem("ステータス確認バーガー", 500, 3, "主菜").get();
        Order.Builder order = new Order.Builder().itemMap(Map.of(burger.getItemId(), new OrderLine(burger, 1)));
        Order registered = db.registerOrder(order).get();
        db.updateStatusAll(registered.getOrderId(), 1);  // ステータス1 = 処理中

        List<Order> result = db.fetchOrdersByStatus(1);
        assertTrue(result.stream().anyMatch(o -> o.getOrderId() == registered.getOrderId()));
    }

@Test
public void testFetchOrderByDateTimeRange() {
    // メニュー追加
    Menu coffee = db.addNewMenuItem("日付確認コーヒー", 250, 5, "飲料").get();

    // 注文登録 (orderDateはDBManager内で自動生成)
    Order.Builder order = new Order.Builder()
        .itemMap(Map.of(coffee.getItemId(), new OrderLine(coffee, 1)))
        .status(0);

    // 登録
    Order registered = db.registerOrder(order).get();

    // 登録された日時（registerOrderでセットされたorderDate）を取得
    LocalDateTime registeredDate = registered.getOrderDate();

    // テスト範囲を登録直前後でカバー
    List<Order> result = db.fetchOrdersByDateTimeRange(
        registeredDate.minusMinutes(1),
        registeredDate.plusMinutes(5)
    );

    assertTrue(result.stream().anyMatch(o -> o.getOrderId() == registered.getOrderId()));
}



    @Test
    public void testFetchOrderByStatusAndDateTime() {
        Menu fries = db.addNewMenuItem("複合確認ポテト", 300, 3, "サイド").get();
        Order.Builder order = new Order.Builder().itemMap(Map.of(fries.getItemId(), new OrderLine(fries, 1))).status(0);
        Order registered = db.registerOrder(order).get();
        assertNotNull(registered);

        db.updateStatusAll(registered.getOrderId(), 2);  // ステータス2 = 完了

        LocalDateTime registeredTime = registered.getOrderDate();
        List<Order> result = db.fetchOrdersByStatusAndDateTime(2,
            registeredTime.minusMinutes(1),
            registeredTime.plusMinutes(5)
        );

        assertTrue(result.stream().anyMatch(o -> o.getOrderId() == registered.getOrderId()));
    }

    @Test
    public void testStockReductionAfterOrder() {
        Menu burger = db.addNewMenuItem("在庫チェックバーガー", 700, 5, "主菜").get();
        Order.Builder order = new Order.Builder().itemMap(Map.of(burger.getItemId(), new OrderLine(burger, 3))).status(0);
        Order registered = db.registerOrder(order).get();
        assertNotNull(registered);

        MenuCatalog catalog = db.createMenuCatalogAll().get();
        Menu updated = catalog.get(burger.getItemId());
        assertEquals(2, updated.getStockQuantity());
    }

@Test
public void testRestockAfterOrder() {
    // 1個だけ在庫があるコーヒーを新規追加
    Menu coffee = db.addNewMenuItem("補充確認コーヒー", 150, 1, "飲料").get();

    // 1個注文（在庫が1なので、注文数は1にするのが妥当）
    Order.Builder order = new Order.Builder()
        .itemMap(Map.of(coffee.getItemId(), new OrderLine(coffee, 1)))
        .status(0);

    // 注文登録
    Order registered = db.registerOrder(order).orElse(null);

    // 登録に失敗したらテスト失敗
    assertNotNull(registered);

    // 5個補充
    db.restockMenuItem(coffee.getItemId(), 5);

    // 最新カタログ取得
    MenuCatalog catalog = db.createMenuCatalogAll().get();
    Menu updated = catalog.get(coffee.getItemId());

    // (元1個) - (1注文) + (5補充) = 5個になるはず
    assertEquals(5, updated.getStockQuantity());
}


}
