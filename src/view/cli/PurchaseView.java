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
/**
 * 購入画面(CLI)
 */
public class PurchaseView {

    // ======= Field =======
    private Scanner scanner;
    private MenuCatalog fullCatalog;    // DBの代わりに一時的に実装
    private MenuCatalog currentCatalog;
    private Cart cart;
    private PurchaseController purCon;

    // 簡易的なカテゴリ(enmu移行予定)
    private static final Map<Integer, String> CATEGORY_MAP = Map.of(
    1, "Food",
    2, "Side",
    3, "Drink"
    );

    // ======= Constructor =======
    public PurchaseView(MenuCatalog fullCatalog) {
        scanner = new Scanner(System.in);
        this.fullCatalog = fullCatalog;
        this.currentCatalog = fullCatalog;
        cart = new Cart(1);
        this.purCon = new PurchaseController(cart,fullCatalog);
    }

    // ======= Method =======

    // 画面を呼び出すメソッド
    public void main_view() {
        // 初回に描画する
        ConsoleUtil.title_view("OrderSystem");
        menu_list_view();   
        cart_view();

        // コマンド入力はプログラム終了まで受付続ける
        while(true){
            command_view();
        }
    }

    // コマンド入力を受け付ける画面
    private boolean command_view() {

        // 説明文章の描画
        String msg_1 = "コマンドを入力してください";
        String msg_2 = "(menu / cart / add / update / filter / checkout  / exit)";
        ConsoleUtil.title_view(msg_1,msg_2);

        // コマンドの受付
        System.out.print("入力:");
        String command = scanner.nextLine();

        // コマンドによる処理の分岐
        switch (command) {
            case "menu":
                return menu_list_view();
            case "cart":
                return list_view(cart, "カート",true);
            case "add":
                return addCart_view();
            case "update":
                return updateCartItemQuantity_view();
            case "filter":
                return update_catalog_view();
            case "checkout":
                return checkout_view();
            case "exit":
                return exit_view();
            default:
                ConsoleUtil.error_view("無効なコマンドです。再入力してください。");
                return false;
        }
    }

    // メニューカタログを描画する画面
    private boolean menu_list_view() {
        ConsoleUtil.title_view("メニュー");

        // currentCatalogを描画
        for(Menu menu:currentCatalog.getAll()) {
            System.out.printf(" ・メニュー番号%d:%s %d円\n",menu.getItemId(),menu.getItemName(),menu.getPrice());
        }
        return true;
    }

    // Cart・Orderクラスの商品リストを描画する画面。タイトルを指定可能。
    private boolean list_view(OrderBase orderBase, String title,boolean showItemId) {
        ConsoleUtil.title_view(title);
        // 注文(カート内)の商品と個数を描画
        for(OrderLine item:orderBase.asList()) {
            String itemId_str = showItemId ? String.format("メニュー番号%d:",item.getMenu().getItemId()) : "";
            System.out.printf(" ・%s%s %d円 × %d\n",itemId_str,item.getMenu().getItemName(),item.getMenu().getPrice(),item.getQuantity());
        }
        String price_str = String.format("合計金額:%d円",orderBase.calculateTotalPrice());
        ConsoleUtil.title_view(price_str);
        return true;
    }

    // 
    private boolean cart_view() {
        return list_view(cart,"カート",true);
    }

    // カートに商品を追加する画面
    private boolean addCart_view(){

        // メニューカタログを描画
        menu_list_view();

        // 数字入力受付
        System.out.println("メニュー番号と個数を入力してください。");
        int itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        int quantity = ConsoleUtil.safeIntInput("個数:",scanner);

        // カート追加処理を依頼
        boolean success = purCon.addCart(itemId,quantity);
        
        // 失敗時にエラーを表示(カートはリセットされない)
        if(!success) {
            ConsoleUtil.error_view("カートへの追加に失敗しました(商品が存在しない、または個数が無効)。");
        }
        
        // カートを表示
        cart_view();

        return success;
    }

    // カートの中の商品の個数を変更する画面
    private boolean updateCartItemQuantity_view() {

        // カートが空だったらエラーを表示
        if(cart.isEmpty()) {
            ConsoleUtil.error_view("カートが空です。");
            return false;
        }

        // 数字入力受付
        System.out.println("個数を変更する商品を選択してください。");
        int itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        int quantity = ConsoleUtil.safeIntInput("新しい個数:",scanner);

        // カート変更処理を依頼
        boolean success = purCon.updateCartItemQuantity(itemId, quantity);

        // 失敗時にエラーを表示(カートはリセットされない)
        if(!success) {
            ConsoleUtil.error_view("変更に失敗しました。(商品が存在しない、または数量が不正)");
        }

        // カートを表示
        cart_view();

        return success;
    }

    // 注文処理を行う画面
    private boolean checkout_view(){

        // カートが空だったらエラーを表示
        if(cart.isEmpty()) {
            ConsoleUtil.error_view("カートが空です。");
            return false;
        }

        // チェックアウト処理を依頼
        System.out.println("注文を行います。");
        Optional<Order> checkout = purCon.checkout();

        if(checkout.isEmpty()) {
            // checkoutが空 = 注文に失敗(カートはリセットされない)
            ConsoleUtil.error_view("注文に失敗しました。");
            return false;
        }else{
            // checkoutが存在 = 注文に成功(カートはリセットされている)
            Order order = checkout.get();

            // 注文内容を描画
            System.out.println("以下の内容で注文を確定しました。");
            list_view(order, "注文内容",false);
            System.out.printf("注文番号は %d です。\n",order.getOrderId());
            return true;
        }        
    }

    // メニューカタログを更新・カテゴリで絞り込みする画面
    public boolean update_catalog_view() {

        // 説明文章の描画
        String msg_1 = "メニュー絞り込み";
        String msg_2 = "メニューをフィルタリングします。カテゴリ番号を入力してください。";
        StringBuilder msg_3B = new StringBuilder();
        CATEGORY_MAP.forEach((k,v) -> msg_3B.append(String.format("%d: %s",k,v)));
        String msg_3 = msg_3B.toString();
        ConsoleUtil.title_view(msg_1,msg_2,msg_3);
        
        // カテゴリ番号の入力受付
        int filter = ConsoleUtil.safeIntInput("カテゴリ番号", scanner);

        // 番号をカテゴリに変換
        String category_filter = CATEGORY_MAP.get(filter);
        if (category_filter == null) {
            // 番号に対応するカテゴリが存在しなければエラーを表示し、絞り込みを解除する。
            ConsoleUtil.error_view("不正な値です。");
            currentCatalog = fullCatalog;    
            return false;
        }

        // カテゴリからカタログを絞り込み
        List<Menu> filtered = purCon.catalogFindByCategory(category_filter);
        if(filtered.isEmpty()) {
            // カテゴリに対応する商品が存在しなければエラーを表示し、絞り込みを解除する。
            ConsoleUtil.error_view("商品が見つかりませんでした。全メニューを表示します。");
            currentCatalog = fullCatalog;    
            return false;
        }
        
        // メニューカタログを更新
        currentCatalog = new MenuCatalog(filtered);

        // メニューカタログを描画
        menu_list_view();
        return true;
        
    }

    // 終了処理を実行してプログラムを終了する
    private boolean exit_view(){
        System.out.println("ご利用ありがとうございました。終了します。");
        System.exit(0);
        // 到達しないがswitch-caseの一貫性を保つため残す
        return true;
    }

}
