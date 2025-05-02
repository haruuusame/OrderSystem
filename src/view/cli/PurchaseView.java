package view.cli;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import controller.cli.PurchaseController;
import model.Cart;
import model.Menu;
import model.MenuCatalog;
import model.Order;
import model.OrderBase;
import model.OrderLine;
import util.ConsoleUtil;

public class PurchaseView {
    private Scanner scanner;
    private MenuCatalog fullCatalog;
    private MenuCatalog currentCatalog;
    private Cart cart;
    private PurchaseController purchaseCon;

    private static final Map<Integer, String> CATEGORY_MAP = Map.of(
    1, "Food",
    2, "Side",
    3, "Drink"
    );
    public PurchaseView(MenuCatalog fullCatalog) {
        scanner = new Scanner(System.in);
        this.fullCatalog = fullCatalog;
        this.currentCatalog = fullCatalog;
        cart = new Cart(1);
        this.purchaseCon = new PurchaseController(cart,fullCatalog);
    }
    public void main_view() {
            menu_list_view();
            list_view(cart,"カート");
        while(true){
            command_view();
        }
    }

    public boolean menu_list_view() {
        System.out.println("+------------------------------------------------------+");
        System.out.println("|                       メニュー                        |");
        System.out.println("+------------------------------------------------------+");

        for(Menu menu:currentCatalog.getAll()) {
            System.out.printf(" ・メニュー番号%d:%s %d円\n",menu.getItemId(),menu.getItemName(),menu.getPrice());
        }
        return true;
    }

    public boolean list_view(OrderBase orderBase, String title) {
        System.out.println("+------------------------------------------------------+");
        System.out.printf("| %-50s |\n", title);
        System.out.println("+------------------------------------------------------+");
        for(OrderLine item:orderBase.asList()) {
            System.out.printf(" ・メニュー番号%d:%s %d円 × %d\n",item.getMenu().getItemId(),item.getMenu().getItemName(),item.getMenu().getPrice(),item.getQuantity());
        }
        System.out.println("+------------------------------------------------------+");
        System.out.printf(" 合計金額:%d円\n",orderBase.calculateTotalPrice());
        System.out.println("+------------------------------------------------------+");
        return true;
    }

    public boolean command_view() {
        System.out.println("--------------------------------------------------------");
        System.out.println("コマンドを入力してください (menu / cart / addCart / updateCart / filter / checkout  / exit)");
        System.out.print("入力:");
        String command = scanner.nextLine();
        
        switch (command) {
            case "menu":
                return menu_list_view();
            case "cart":
                return list_view(cart, "カート");
            case "addCart":
                return addCart_view();
            case "updateCart":
                return updateCartItemQuantity_view();
            case "filter":
                return update_catalog_view();
            case "checkout":
                return checkout_view();
            case "exit":
                return exit_view();
            default:
                error_view("無効なコマンドです。再入力してください。");
                return false;
        }
    }

    public boolean addCart_view(){
        menu_list_view();
        int itemId,quantity;
        System.out.println("メニュー番号と個数を入力してください。");

        itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        quantity = ConsoleUtil.safeIntInput("個数:",scanner);

        boolean success = purchaseCon.addCart(itemId,quantity);
        
        if(!success) {
            error_view("カートへの追加に失敗しました(商品が存在しない、または個数が無効)。");
        }
        list_view(cart, "カート");
        return success;
    }
    public boolean checkout_view(){
        if(cart.isEmpty()) {
            error_view("カートが空です。");
            return false;
        }
        System.out.println("注文を行います。");
        Optional<Order> checkout = purchaseCon.checkout();

        if(checkout.isEmpty()) {
            error_view("注文に失敗しました。");
            return false;
        }else{
            Order order = checkout.get();
            System.out.println("以下の内容で注文を確定しました。");
            list_view(order, "注文内容");
            System.out.printf("注文番号は %d です。\n",order.getOrderId());
            return true;
        }        
    }
    public boolean updateCartItemQuantity_view() {
        int itemId, quantity;
        System.out.println("個数を変更する商品を選択してください。");

        itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        quantity = ConsoleUtil.safeIntInput("新しい個数:",scanner);

        boolean success = purchaseCon.updateCartItemQuantity(itemId, quantity);
        if(!success) {
            error_view("変更に失敗しました。(商品が存在しない、または数量が不正)");
        }
        list_view(cart,"カート");
        return success;
    }
    public boolean exit_view(){
        System.out.println("ご利用ありがとうございました。終了します。");
        System.exit(0);
        return true;
    }
    public void error_view(String message){
        System.out.println("[エラー]" + message);
    }

    public boolean update_catalog_view() {
        System.out.println("メニューをフィルタリングします。カテゴリ番号を入力してください。");
        CATEGORY_MAP.forEach((k,v) -> System.out.printf("%d: %s",k,v));
        System.out.println();
        
        int filter = ConsoleUtil.safeIntInput("カテゴリ番号", scanner);
        String category_filter = CATEGORY_MAP.get(filter);
        if (category_filter == null) {
            error_view("不正な値です。");
            currentCatalog = fullCatalog;    
            return false;
        }

        List<Menu> filtered = purchaseCon.catalogFindByCategory(category_filter);
        if(filtered.isEmpty()) {
            error_view("アイテムが見つかりませんでした。全メニューを表示します。");
            currentCatalog = fullCatalog;    
            return false;
        }
        
        currentCatalog = new MenuCatalog(filtered);
        menu_list_view();
        return true;
        
    }
}
