package view.cli;

import model.DBManager;
import model.Order;
import util.ConsoleUtil;

import java.util.List;

public class CallDisplayApp {

    private final DBManager dbManager;

    public CallDisplayApp(String dbFileName) {
        this.dbManager = new DBManager(dbFileName);
    }

    public void run() {
        dbManager.connect();

        try {
            while (true) {
                ConsoleUtil.clearScreen();

                // お待ち番号（status = 0）を取得
                List<Order> waitingOrders = dbManager.fetchOrdersByStatus(0);

                // 呼び出し番号（status = 2）を取得
                List<Order> calledOrders = dbManager.fetchOrdersByStatus(1);

                // 表示
                System.out.println("====================================");
                System.out.println("[お待ち番号]");
                if (waitingOrders.isEmpty()) {
                    System.out.println("(なし)");
                } else {
                    for (Order order : waitingOrders) {
                        System.out.println(order.getOrderId());
                    }
                }

                System.out.println("\n[呼び出し番号]");
                if (calledOrders.isEmpty()) {
                    System.out.println("(なし)");
                } else {
                    for (Order order : calledOrders) {
                        System.out.println(order.getOrderId());
                    }
                }

                System.out.println("\n====================================");
                System.out.println("お手元のレシート番号をご参照ください");

                // 10秒待機
                Thread.sleep(10000);
            }

        } catch (InterruptedException e) {
            System.err.println("[中断] " + e.getMessage());
        } finally {
            dbManager.disconnect();
        }
    }
}
