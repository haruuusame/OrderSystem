package test.view.cli;

import java.util.ArrayList;
import java.util.List;

import controller.cli.OrderSessionController;
import model.Menu;
import model.MenuCatalog;
import view.cli.OrderCliView;

/**
 * PurchaseView を手動で実行・確認するためのテストクラス。
 * 実行すると実際にCLIが描画される。
 */
public class OrderCliViewTest {
    public static void main(String[] args) {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1, "ハンバーガー", 200, 10, "Food"));
        menus.add(new Menu(2, "チーズバーガー", 350, 15, "Food"));
        menus.add(new Menu(3, "ポテト", 200, 20, "Side"));
        menus.add(new Menu(4, "ナゲット", 350, 8, "Side"));
        menus.add(new Menu(5, "コーラ", 100, 30, "Drink"));
        menus.add(new Menu(6, "コーヒー", 100, 30, "Drink"));

        MenuCatalog catalog = new MenuCatalog(menus);

        OrderSessionController purCon = new OrderSessionController(1, catalog);
        OrderCliView view = new OrderCliView(purCon);

        view.showMain();  
    }
}
