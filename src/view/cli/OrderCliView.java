package view.cli;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import controller.cli.OrderSessionController;
import model.Menu;
import model.Order;
import model.OrderBase;
import model.OrderLine;
import util.ConsoleUtil;
/**
 * 購入画面(CLI)
 */
public class OrderCliView {

    // ======= Field =======
    private Scanner scanner;
    private OrderSessionController sessionController;

    // 簡易的なカテゴリリスト(enmu移行予定)
    private static final String RESET_COMMAND = "All";
    private static final Map<Integer, String> CATEGORY_MAP = new LinkedHashMap<>();
    static {
        CATEGORY_MAP.put(1,RESET_COMMAND);
        CATEGORY_MAP.put(2, "Food");
        CATEGORY_MAP.put(3, "Side");
        CATEGORY_MAP.put(4, "Drink");
        CATEGORY_MAP.put(5,"noItem");
    }

    // ======= Constructor =======
    public OrderCliView(OrderSessionController sessionController,Scanner scanner) {
        this.scanner = scanner;
        this.sessionController = sessionController;
    }
    public OrderCliView(OrderSessionController purCon) {
        this(purCon, new Scanner(System.in));
    }

    // ======= Method =======

    // 画面を呼び出すメソッド
    public void showMain() {
        // カタログフェッチ
        sessionController.fetchCatalog();
        // 初回に描画する
        ConsoleUtil.showHeader("OrderSystem");
        showMenuList();   
        showCart();

        // コマンド入力はプログラム終了まで受付続ける
        while(true){
            showCommand();
        }
    }

    // コマンド入力を受け付ける画面を描画
    private boolean showCommand() {

        // 説明文章の描画
        String msg_1 = "コマンドを入力してください";
        String msg_2 = "(1: メニュー表示 / 2: メニュー絞り込み / 3: カート表示 / 4: カートに追加 / 5: 内容変更 / 6: 注文 / 7: 終了)";
        ConsoleUtil.showHeader(msg_1,msg_2);

        // コマンドの受付
        int command = ConsoleUtil.safeIntInput("入力:",scanner);

        // コマンドによる処理の分岐
        switch (command) {
            case 1:
                return showMenuList();
            case 2:
                return showUpdateCatalog();
            case 3:
                return showCart();
            case 4:
                return showAddCart();
            case 5:
                return showUpdateCartItemQuantity();
            case 6:
                return showCheckout();
            case 7:
                return showExit();
            default:
                ConsoleUtil.showError("無効なコマンドです。再入力してください。");
                return false;
        }
    }

    // メニュー一覧を描画
    private boolean showMenuList() {
        ConsoleUtil.showHeader("メニュー");

        // currentCatalogを描画
        for(Menu menu:sessionController.getCatalogMenus()) {
            System.out.printf(" ・メニュー番号%d:%s %d円\n",menu.getItemId(),menu.getItemName(),menu.getPrice());
        }
        return true;
    }

    // Cart・Orderクラスの商品リストを描画。タイトルを指定可能。
    // showItemIdがtrueでメニュー番号を表示する。falseだと表示しない。
    private boolean showList(OrderBase orderBase, String title,boolean showItemId) {
        ConsoleUtil.showHeader(title);
        // 注文(カート内)の商品と個数を描画
        for(OrderLine item:orderBase.asList()) {
            String itemId_str = showItemId ? String.format("メニュー番号%d:",item.getMenu().getItemId()) : "";
            System.out.printf(" ・%s%s %d円 × %d\n",itemId_str,item.getMenu().getItemName(),item.getMenu().getPrice(),item.getQuantity());
        }
        String price_str = String.format("合計金額:%d円",orderBase.calculateTotalPrice());
        ConsoleUtil.showHeader(price_str);
        return true;
    }

    // カートの中身を描画
    private boolean showCart() {
        return showList(sessionController.getCart(),"カート",true);
    }

    // カートに商品を追加する画面を描画
    private boolean showAddCart(){

        // メニューカタログを描画
        showMenuList();

        // 数字入力受付
        System.out.println("メニュー番号と個数を入力してください。");
        int itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        int quantity = ConsoleUtil.safeIntInput("個数:",scanner);

        // カート追加処理を依頼
        boolean success = sessionController.addCart(itemId,quantity);
        
        // 失敗時にエラーを表示(カートはリセットされない)
        if(!success) {
            ConsoleUtil.showError("カートへの追加に失敗しました(商品が存在しない、または個数が無効)。");
        }
        
        // カートを表示
        showCart();

        return success;
    }

    // カートの中の商品の個数を変更する画面
    private boolean showUpdateCartItemQuantity() {

        // カートが空だったらエラーを表示
        if(sessionController.cartIsEmpty()) {
            ConsoleUtil.showError("カートが空です。");
            return false;
        }

        // 数字入力受付
        System.out.println("個数を変更する商品を選択してください。");
        showCart();
        int itemId = ConsoleUtil.safeIntInput("メニュー番号:", scanner);
        int quantity = ConsoleUtil.safeIntInput("新しい個数:",scanner);

        // カート変更処理を依頼
        boolean success = sessionController.updateCartItemQuantity(itemId, quantity);

        // 失敗時にエラーを表示(カートはリセットされない)
        if(!success) {
            ConsoleUtil.showError("変更に失敗しました。(商品が存在しない、または数量が不正)");
        }

        // カートを表示
        showCart();

        return success;
    }

    // 注文処理を行う画面を描画
    private boolean showCheckout(){

        // カートが空だったらエラーを表示
        if(sessionController.cartIsEmpty()) {
            ConsoleUtil.showError("カートが空です。");
            return false;
        }

        // チェックアウト処理を依頼
        System.out.println("注文を行います。");
        Optional<Order> checkout = sessionController.checkout();

        if(checkout.isEmpty()) {
            // checkoutが空 = 注文に失敗(カートはリセットされない)
            ConsoleUtil.showError("注文に失敗しました。");
            return false;
        }else{
            // checkoutが存在 = 注文に成功(カートはリセットされている)
            Order order = checkout.get();

            // 注文内容を描画
            System.out.println("以下の内容で注文を確定しました。");
            showList(order, "注文内容",false);
            System.out.printf("注文番号は %d です。\n",order.getOrderId());
            return true;
        }        
    }

    // メニューカタログを更新・カテゴリで絞り込みする画面を描画
    public boolean showUpdateCatalog() {
        // 説明文章の描画
        String msg_1 = "メニュー絞り込み";
        String msg_2 = "メニューをフィルタリングします。カテゴリ番号を入力してください。";
        StringBuilder msg_3B = new StringBuilder();
        msg_3B.append("(");
        CATEGORY_MAP.forEach((k,v) -> msg_3B.append(String.format("%d: %s / ",k,v)));
        msg_3B.append(")");
        String msg_3 = msg_3B.toString();
        ConsoleUtil.showHeader(msg_1,msg_2,msg_3);
        
        // カテゴリ番号の入力受付
        int filter = ConsoleUtil.safeIntInput("カテゴリ番号:", scanner);

        // 番号をカテゴリに変換
        String category_filter = CATEGORY_MAP.get(filter);

        // 番号に対応するカテゴリが存在しなければエラーを表示。
        if (category_filter == null) {
            ConsoleUtil.showError("不正な値です。");
            return false;
        }

        // RESET_COMMANDの時はカタログをリセットしてtrueを返す。
        if (category_filter.equals(RESET_COMMAND)) {
            sessionController.resetCatalog();
            showMenuList();
            return true;
        }

        // カテゴリからカタログを絞り込み
        sessionController.catalogFindByCategory(category_filter);

        boolean success = !sessionController.getCatalogMenus().isEmpty();
        if(!success) {
            // カテゴリに対応する商品が存在しなければエラーを表示し、絞り込みを解除する。
            ConsoleUtil.showError("商品が見つかりませんでした。全メニューを表示します。");
            sessionController.resetCatalog();
        }

        // メニューカタログを描画
        showMenuList();
        return success;
        
    }

    // 終了処理を実行してプログラムを終了する
    private boolean showExit(){
        System.out.println("ご利用ありがとうございました。終了します。");
        System.exit(0);
        // 到達しないがswitch-caseの一貫性を保つため残す
        return true;
    }

}
