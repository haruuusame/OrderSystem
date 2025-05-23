package test.model;

import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

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
        Menu added = db.addNewMenuItem("テスト商品", 1200, 10, "デザート");
        assertNotNull(added);
        assertEquals("テスト商品", added.getItemName());

        MenuCatalog catalog = db.createMenuCatalogAll();
        assertNotNull(catalog);
        assertTrue(catalog.has(added.getItemId()));
    }

    @Test
    public void testRestockMenuItemSuccess() {
        Menu m = db.addNewMenuItem("在庫補充", 800, 1, "惣菜");
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
        Menu m = db.addNewMenuItem("注文成功商品", 300, 10, "飲料");
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 2));
        Order order = new Order(itemMap);

        Order registered = db.registerOrder(order);
        assertNotNull(registered);
        assertEquals(order.getOrderDate(), registered.getOrderDate());
    }

    @Test
    public void testRegisterOrderFailureByStock() {
        Menu m = db.addNewMenuItem("在庫不足商品", 400, 1, "主菜");
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 999));
        Order order = new Order(itemMap);

        Order result = db.registerOrder(order);
        assertNull(result);
    }

    @Test
    public void testUpdateStatusAndHeaderSync() {
        Menu m = db.addNewMenuItem("ステータス商品", 500, 3, "その他");
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 1));
        Order order = new Order(itemMap);
        Order registered = db.registerOrder(order);
        assertNotNull(registered);

        db.updateStatus(registered.getOrderId(), m.getItemId(), 2);  // 2 = 完了
    }

    @Test
    public void testUpdateStatusAll() {
        Menu m1 = db.addNewMenuItem("一括ステータス1", 600, 2, "軽食");
        Menu m2 = db.addNewMenuItem("一括ステータス2", 700, 2, "軽食");

        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m1.getItemId(), new OrderLine(m1, 1));
        itemMap.put(m2.getItemId(), new OrderLine(m2, 1));
        Order order = new Order(itemMap);

        Order registered = db.registerOrder(order);
        assertNotNull(registered);

        db.updateStatusAll(registered.getOrderId(), 1);  // 全明細を処理中に
    }
}
