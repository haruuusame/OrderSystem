package view.cli;

import model.DBManager;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CallDisplayAppTest {

    private DBManager mockDbManager;
    private CallDisplayApp app;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        mockDbManager = mock(DBManager.class);
        // DBManagerの実装を引数で渡せるようにCallDisplayAppを修正しないといけないので
        // ここではコンストラクタを直接使えないため、ラッパークラスなど検討するか、
        // 今回はリフレクションなど使うか、またはテスト用メソッドを用意する必要あり。

        // ここでは仮にCallDisplayAppにDBManagerを注入できるコンストラクタがあるものとして
        // app = new CallDisplayApp(mockDbManager);

        // 標準出力をキャプチャ
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testDisplayOrders() throws InterruptedException {
        // モックが返す注文データを準備
        Order order1 = mock(Order.class);
        when(order1.getOrderId()).thenReturn(101);
        Order order2 = mock(Order.class);
        when(order2.getOrderId()).thenReturn(102);

        when(mockDbManager.fetchOrdersByStatus(0)).thenReturn(List.of(order1));
        when(mockDbManager.fetchOrdersByStatus(1)).thenReturn(List.of(order2));


        // 検証
        String output = outContent.toString();

        assertTrue(output.contains("101"));
        assertTrue(output.contains("102"));
        assertTrue(output.contains("[お待ち番号]"));
        assertTrue(output.contains("[呼び出し番号]"));
        assertTrue(output.contains("お手元のレシート番号をご参照ください"));
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
}

