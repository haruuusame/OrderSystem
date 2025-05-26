package view.cli;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import model.DBManager;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderLine;
import util.ConsoleUtil;

public class Employee {
    private static Scanner scanner = new Scanner(System.in);

    public static void showOrder(DBManager dbm) {
        int orderId = ConsoleUtil.safeIntInput("注文番号を入力してください:", scanner);
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
        System.out.printf("注文番号：%d\n", order.getOrderId());
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

    //注文履歴を表示するメソッド
    public static void showOrderHistory(DBManager dbm) {
        int status = ConsoleUtil.safeIntInput("表示するステータスを入力してください。\n" + //
                        "ステータス番号:(-1:全表示/ 0:処理中/ 1:提供中/ 2:完了/ 3:キャンセル)\n" + //
                        "", scanner);
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
    public static void updateStock(DBManager dbm) {
        showMenuList(dbm);
        int itemId = ConsoleUtil.safeIntInput("商品IDを入力してください:", scanner);
        int quantity = ConsoleUtil.safeIntInput("補充する数量を入力してください:", scanner);
        boolean success = dbm.restockMenuItem(itemId,quantity);
        if (success) {
            System.out.printf("商品ID%dの在庫を%d個補充しました。\n", itemId, quantity);
        } else {
            System.out.printf("在庫補充できませんでした。(商品ID：%d)\n", itemId);
        }
    }

    public static void updateOrderStatus(DBManager dbm) {
        int orderId = ConsoleUtil.safeIntInput("注文番号を入力してください:",scanner);
        int status = ConsoleUtil.safeIntInput("ステータスを入力してください:",scanner);
        if (0 <= status && status <= 3) {
            boolean success = dbm.updateStatusAll(orderId, status);
            if (success) {
                System.out.printf("注文番号%dのステータスを%d:%sに変更しました。\n",orderId,status,convert(status));
            } else {
                System.out.printf("ステータスを更新できませんでした。(注文番号：%d)\n",orderId);
            }
        } else {
                System.out.printf("不正なステータスです。\n",orderId);
        }
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

    private static void showCommand(DBManager dbm) {

        // 説明文章の描画
        String msg_1 = "コマンドを入力してください";
        String msg_2 = "(1: 注文内容の確認 / 2: 注文履歴の表示 / 3: 在庫の更新 / 4: ステータスの更新 / 5: 終了)";
        ConsoleUtil.showHeader(msg_1,msg_2);

        // コマンドの受付
        int command = ConsoleUtil.safeIntInput("入力:",scanner);

        // コマンドによる処理の分岐
        switch (command) {
            case 1:
                showOrder(dbm);
                break;
            case 2:
                showOrderHistory(dbm);
                break;
            case 3:
                updateStock(dbm);
                break;
            case 4:
                updateOrderStatus(dbm);
                break;
            case 5:
                showExit();
                break;
            default:
                ConsoleUtil.showError("無効なコマンドです。再入力してください。");
        }
    }

    public static void showMain(DBManager dbm){
        while(true){
            showCommand(dbm);
        }
    }

    // 終了処理を実行してプログラムを終了する
    private static void showExit(){
        System.out.println("ご利用ありがとうございました。終了します。");
        System.exit(0);
    }
    // メニューを表示
    private static void showMenuList(DBManager dbm) {
        ConsoleUtil.showHeader("メニュー");

        // currentCatalogを描画
        Optional<MenuCatalog> opCatalog = dbm.createMenuCatalogAll();
        if(!opCatalog.isPresent()){
            System.err.println("メニューの取得に失敗しました");
        }else{
            MenuCatalog catalog = opCatalog.get();
            for(Menu menu:catalog.getAll()) {
                System.out.printf(" ・メニュー番号%d:%s %d円 %d個\n",menu.getItemId(),menu.getItemName(),menu.getPrice(),menu.getStockQuantity());
            }
        }
    }
}