package customer;

import model.Order;
import model.OrderLine;

import java.text.NumberFormat;
import java.util.Locale;

public class CustomerView {

    /**
     * お客様用の注文内容を表示するメソッド。
     * 注文番号、注文日時、注文内容（商品名、単価、数量、小計）、合計金額を出力します。
     *
     * @param order 表示する注文情報を持つ Order オブジェクト
     */
    public static void displayOrder(Order order) {
        // 注文控えのヘッダー部分
        System.out.println("===== お客様控え =====");
        System.out.println("注文番号: " + order.getOrderId()); // 注文番号を表示
        System.out.println("注文日時: " + order.getOrderDate()); // 注文日時を表示

        int total = 0; // 合計金額を計算するための変数
        System.out.println("\n--- 注文内容 ---");
        
        // 商品名、単価、数量、小計を列に並べて表示
        System.out.printf("%-20s %-10s %-10s %-10s%n", "商品名", "単価", "数量", "小計");

        // 注文に含まれる全ての商品について、商品情報を表示
        for (OrderLine line : order.getItemMap().values()) {
            // 商品の名前、価格、数量、小計を取得
            String itemName = line.getMenu().getItemName(); // 商品名
            int price = line.getMenu().getPrice(); // 商品の単価
            int quantity = line.getQuantity(); // 注文した数量
            int subtotal = price * quantity; // 小計（単価 × 数量）
            total += subtotal; // 小計を合計金額に加算

            // 各商品情報を整形して表示
            System.out.printf("%-20s %-10s %-10d %-10s%n",
                    itemName, // 商品名
                    formatCurrency(price), // 単価（通貨形式）
                    quantity, // 数量
                    formatCurrency(subtotal)); // 小計（通貨形式）
        }

        // 注文内容の区切り線と合計金額の表示
        System.out.println("------------------------------");
        System.out.println("合計金額: " + formatCurrency(total)); // 合計金額を表示
        System.out.println("==============================\n");
    }

    /**
     * 金額を通貨形式（例: ¥1,200）に整えるメソッド。
     *
     * @param amount 金額（整数）
     * @return 整形された通貨形式の文字列
     */
    private static String formatCurrency(int amount) {
        // 日本円の通貨形式で整形するためのフォーマッタを作成
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        return formatter.format(amount); // 金額を通貨形式に変換して返す
    }
}
