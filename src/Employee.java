import java.util.ArrayList;
import model.Menu;
import model.Order;
import model.OrderLine;

public class Employee {
    ArrayList<String> itemNameList = new ArrayList<>();
    ArrayList<Integer> itemPriceList = new ArrayList<>();
    ArrayList<Integer> itemQuantityList = new ArrayList<>();
    int totalSales = 0;

    //注文内容を確認するメソッド
    public static void checkOrder(Order order) {
        int totalPrice = order.calculateTotalPrice();

        System.out.println("注文内容確認");
        System.out.printf("お客様番号：%d\n", order.getOrderId());
        System.out.println("注文日時：" + order.getOrderDate());
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
    public static void updateOrderHistory(Order order, Employee employee) {
        int totalPrice = order.calculateTotalPrice();

        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemPrice = line.getMenu().getPrice();
            int itemQuantity = line.getQuantity(); 
            
            employee.itemNameList.add(itemName);
            employee.itemPriceList.add(itemPrice);
            employee.itemQuantityList.add(itemQuantity);
        }

        int n = employee.itemNameList.size();
        employee.totalSales += totalPrice;
        System.out.println("注文履歴");
        System.out.printf("お客様番号：%d\n", order.getOrderId());
        System.out.printf("注文日時：" + order.getOrderDate());
        for (int i = 0; i < n; i++) {
            System.out.printf("%s | %d | %d\n", 
            employee.itemNameList.get(i), employee.itemPriceList.get(i), employee.itemQuantityList.get(i));
        }
        System.out.printf("合計金額：%d\n", totalPrice);
        System.out.printf("合計売上額：%d\n", employee.totalSales);
    }

    //在庫の更新を行うメソッド
    public static void updateStock(Order order, Menu menu) {
        for (OrderLine line : order.asList()) {
            String itemName = line.getMenu().getItemName();
            int itemQuantity = line.getQuantity();
            if (menu.getStockQuantity() >= itemQuantity) {
                //menu.getStockQuantity() -= itemQuantity;
                System.out.printf("%sが%d個注文され残りは%d個です。\n",
                itemName, itemQuantity, menu.getStockQuantity());
            }
        }
    }
}