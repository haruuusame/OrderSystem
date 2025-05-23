package test.model;
import customer.CustomerView;
import model.Menu;
import model.Order;
import model.OrderLine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
public class CustomerViewTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private Menu createSampleMenu() {
        return new Menu(1, "ハンバーガー", 500, 10, "Food");
    }

   private Order createSampleOrder() {
    Menu menu = createSampleMenu();
    OrderLine line = new OrderLine(menu, 2); // 単価500×2 = 1000

    Map<Integer, OrderLine> itemMap = new HashMap<>();
    itemMap.put(1, line);

    return new Order(itemMap, 1001, LocalDateTime.of(2025, 5, 20, 12, 0));
}


    @Test
    public void displayOrder_outputsCorrectFormat() {
        Order order = createSampleOrder();

        // 実行
        CustomerView.displayOrder(order);

        String output = outContent.toString();
         System.out.println("【実際の出力】\n" + output);

        // 出力に含まれているべき内容を確認
        assertTrue(output.contains("===== お客様控え ====="));
        assertTrue(output.contains("注文番号: 1001"));
        assertTrue(output.contains("ハンバーガー"));
        assertTrue(output.contains("￥500")); // 単価
        assertTrue(output.contains("2"));    // 数量
        assertTrue(output.contains("￥1,000")); // 小計
        assertTrue(output.contains("合計金額: ￥1,000"));
    }
}
