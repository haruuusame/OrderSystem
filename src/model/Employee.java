package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Employee {
    ArrayList<String> itemNameList = new ArrayList<>();
    ArrayList<Integer> itemPriceList = new ArrayList<>();
    ArrayList<Integer> itemQuantityList = new ArrayList<>();
    int totalSales = 0;

    public Employee(DBManager dbm) {
    }

    public static void showOrder(int orderId, DBManager dbm) {
        Optional<Order> opOrder = dbm.fetchOrderById(orderId);
        if (opOrder.isPresent()) {
            Order order = opOrder.get();
            checkOrder(order);
        } else {
            System.out.println("注文が見つかりませんでした。");
        }
    }

    //注文内容を確認するメソッド
    public static void checkOrder(Order order) {
        int totalPrice = order.calculateTotalPrice();

        System.out.println("注文内容確認");
        System.out.printf("お客様番号：%d\n", order.getOrderId());
        System.out.println("注文日時：" + order.getOrderDate());
        System.out.printf("ステータス：%s\n", convert(order.getStatus()));
        System.out.printf("%s | %s | %s\n", "商品名", "金額", "個数");
        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemPrice = line.getMenu().getPrice();
            int itemQuantity = line.getQuantity();
            System.out.printf("%s | %d | %d\n", itemName, itemPrice, itemQuantity);
        }
        System.out.printf("合計金額：%d\n", totalPrice);
    }

    //注文履歴を更新するメソッド
    public static void showOrderHistory(int status, DBManager dbm) {
        List<Order> orders;
        if (status == -1) {
            orders = dbm.fetchOrdersAll();
        } else if (0 <= status && status <= 3) {
            orders = dbm.fetchOrdersByStatus(status);
        } else {
            System.out.println("不正なステータスです。");
            return;
        }
        for (Order order : orders) {
            System.out.println("----------------------");
            checkOrder(order);
        }
    }

    //在庫の更新を行うメソッド
    public static void updateStock(int itemId, int quantity, DBManager dbm) {
        boolean success = dbm.restockMenuItem(itemId,quantity);
        if (success) {
            System.out.printf("商品ID%dの在庫を%d個補充しました。\n", itemId, quantity);
        } else {
            System.out.printf("在庫補充できませんでした。(商品ID：%d)\n", itemId);
        }
    }

    public static void updateOrderStatus(int orderId, int status, DBManager dbm) {
        dbm.updateStatusAll(orderId, status);
        System.out.printf("注文ID%dのステータスを%d:%sに変更しました。\n",orderId,status,convert(status));
    }

    public static String convert(int status) {
        String statusString;
        switch(status) {
            case 0 : statusString = "処理中"; break;
            case 1 : statusString = "提供中"; break;
            case 2 : statusString = "完了"; break;
            case 3 : statusString = "キャンセル"; break;
            default : statusString = ""; break;
        }
        return statusString;
    }
}