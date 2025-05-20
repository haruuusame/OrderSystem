import model.Order;
import model.OrderLine;
import model.Menu;
import java.*;

public class Employee {
    ArrayList itemNameList = new ArrayList<String>();
    ArrayList itemPriceList = new ArrayList<Integer>();
    ArrayList itemQuantityList = new ArrayList<Integer>();
    int totalSales = 0;

    //注文内容を確認するメソッド
    public static void checkOrder(Order order) {
        int totalPrice = order.calculateTotalPrice();

        System.out.println("注文内容確認");
        System.out.println("商品名 | 金額 | 個数");
        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemPrice = line.getMenu().getPrice();
            int itemQuantity = line.getQuantity();
            System.out.printf("%s | %d | %d\n", itemName, itemPrice, itemQuantity);
        }
        System.out.printf("合計金額：%d\n", totalPrice);
    }

    //注文履歴を更新するメソッド
    public static void updateOrderHistory(Order order) {
        int totalPrice = order.calculateTotalPrice();

        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemPrice = line.getMenu().getPrice();
            int itemQuantity = line.getQuantity(); 
            
            itemNameList.add(itemName);
            itemPriceList.add(itemPrice);
            itemQuantityList.add(itemQuantity);
        }

        int n = itemNameList.size();
        totalSales += totalPrice;
        System.out.println("注文履歴");
        for (int i = 0; i < n; i++) {
            System.out.printf("%s | %d | %d\n", 
            itemNameList.get(i), itemPriceList.get(i), itemQuantityList.get(i));
        }
        System.out.printf("合計金額：%d\n", totalPrice);
        System.out.printf("合計売上額：%d\n", totalSales);
    }

    //在庫の更新を行うメソッド
    public static void updateStock(Order order, Menu menu) {
        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemQuantity = line.getQuantity();
            menu.getStockQuantity() -= itemQuantity;
            System.out.printf("%sが%d個注文され残りは%d個です。\n",
            itemName, itemQuantity, menu.getStockQuantity())
        }
    }
}