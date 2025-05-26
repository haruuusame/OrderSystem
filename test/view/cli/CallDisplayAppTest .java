package view.cli;

import model.DBManager;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.ConsoleUtil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CallDisplayAppTest {

    private DBManager mockDBManager;
    private CallDisplayApp app;

    @BeforeEach
    public void setUp() {
        mockDBManager = mock(DBManager.class);
        app = new CallDisplayApp("dummy.db") {
            // DBManagerの差し替え
            {
                this.dbManager = mockDBManager;
            }
        };
    }

    @Test
    public void testDisplayOnce_withOrders() {
        // モックの注文データを用意
        List<Order> waiting = Arrays.asList(new Order(1), new Order(2));
        List<Order> called = Collections.singletonList(new Order(3));

        when(mockDBManager.fetchOrdersByStatus(0)).thenReturn(waiting);
        when(mockDBManager.fetchOrdersByStatus(1)).thenReturn(called);

        // 出力をキャプチャ
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        app.displayOnce();

        String output = outContent.toString();

        // 検証
        assertTrue(output.contains("[お待ち番号]"));
        assertTrue(output.contains("1"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("[呼び出し番号]"));
        assertTrue(output.contains("3"));
    }

    @Test
    public void testDisplayOnce_withNoOrders() {
        when(mockDBManager.fetchOrdersByStatus(0)).thenReturn(Collections.emptyList());
        when(mockDBManager.fetchOrdersByStatus(1)).thenReturn(Collections.emptyList());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        app.displayOnce();

        String output = outContent.toString();

        assertTrue(output.contains("(なし)"));
        assertTrue(output.contains("お手元のレシート番号をご参照ください"));
    }
}
