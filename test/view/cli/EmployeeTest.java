package view.cli;

import java.time.LocalDateTime;
import java.util.*;

import model.*;

public class EmployeeTest {
    public static void main(String[] args) {
        // モックDB作成
        DBManagerMock dbm = new DBManagerMock();

        System.out.println("=== showOrder テスト ===");
        Employee.showOrder(1, dbm);
        System.out.println();

        System.out.println("=== showOrderHistory (-1: 全件) テスト ===");
        Employee.showOrderHistory(-1, dbm);
        System.out.println();

        System.out.println("=== updateStock テスト ===");
        Employee.updateStock(1001, 5, dbm);
        System.out.println();

        System.out.println("=== updateOrderStatus テスト ===");
        Employee.updateOrderStatus(1, 2, dbm);
    }

    // ===== モックDB定義 =====
    static class DBManagerMock extends DBManager {
        private List<Order> dummyOrders;

        public DBManagerMock() {
            dummyOrders = new ArrayList<>();

            Menu menu1 = new Menu(101, "コーヒー", 300, 10, "ドリンク");
            Menu menu2 = new Menu(102, "サンドイッチ", 500, 10, "フード");

            OrderLine line1 = new OrderLine(menu1, 2);
            OrderLine line2 = new OrderLine(menu2, 1);
            OrderLine line3 = new OrderLine(menu2, 3);

            // Order Builder を使って作成
            Order order1 = new Order.Builder()
                    .orderId(1)
                    .orderDate(LocalDateTime.now())
                    .status(0)
                    .add(line1)
                    .add(line2)
                    .build();

            Order order2 = new Order.Builder()
                    .orderId(2)
                    .orderDate(LocalDateTime.now())
                    .status(2)
                    .add(line3)
                    .build();

            dummyOrders.add(order1);
            dummyOrders.add(order2);
        }

        public Optional<Order> fetchOrderById(int orderId) {
            for (Order order : dummyOrders) {
                if (order.getOrderId() == orderId) {
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        }

        public List<Order> fetchOrdersAll() {
            return dummyOrders;
        }

        public List<Order> fetchOrdersByStatus(int status) {
            List<Order> result = new ArrayList<>();
            for (Order order : dummyOrders) {
                if (order.getStatus() == status) {
                    result.add(order);
                }
            }
            return result;
        }

        public boolean restockMenuItem(int itemId, int quantity) {
            return true; // 常に成功にするモック処理
        }

        public void updateStatusAll(int orderId, int status) {
            for (Order order : dummyOrders) {
                if (order.getOrderId() == orderId) {
                    order.setStatus(status);
                }
            }
        }
    }
}



