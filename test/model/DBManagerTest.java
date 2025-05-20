package test.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.DBManager;
import model.Menu;
import model.Order;
import model.OrderLine;

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
    public void testAddNewMenuItem() {
        Menu added = db.addNewMenuItem("テスト商品", 1200, 10, "デザート");
        assertNotNull(added);
        assertEquals("テスト商品", added.getItemName());
        assertEquals(1200, added.getPrice());
    }

    @Test
    public void testRestockMenuItem() {
        Menu m = db.addNewMenuItem("在庫テスト", 800, 1, "惣菜");
        System.out.println(m.getItemId());
        boolean result = db.restockMenuItem(m.getItemId(), 5);
        assertTrue(result);
    }

    @Test
    public void testRegisterOrder() {
        Menu m = db.addNewMenuItem("注文テスト商品", 300, 10, "飲料");

        // OrderLine を Map に格納
        OrderLine ol = new OrderLine(m, 2);
        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), ol);

        Order order = new Order(itemMap); // 自動的に orderId 付与される

        boolean result = db.registerOrder(order);
        assertTrue(result);
    }

    @Test
    public void testUpdateStatus() {
        Menu m = db.addNewMenuItem("ステータス商品", 500, 3, "その他");

        Map<Integer, OrderLine> itemMap = new LinkedHashMap<>();
        itemMap.put(m.getItemId(), new OrderLine(m, 1));

        Order order = new Order(itemMap);
        boolean inserted = db.registerOrder(order);
        assertTrue(inserted);

        db.updateStatus(order.getOrderId(), m.getItemId(), 1);

        // ※ 本格的に確認するには SELECT が必要
        // 今回は実行成功だけ確認（例外なしでOKとする）
    }
}
