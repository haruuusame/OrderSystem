package test.view.cli;

import java.util.ArrayList;
import java.util.List;

import model.Menu;
import model.MenuCatalog;
import view.cli.PurchaseView;

/**
 * PurchaseView を手動で実行・確認するためのテスト。
 * 実行すると実際にCLIが描画される。
 */
public class PurchaseViewTest {
    public static void main(String[] args) {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1, "ハンバーガー", 200, 10, "Food"));
        menus.add(new Menu(2, "チーズバーガー", 350, 15, "Food"));
        menus.add(new Menu(3, "ポテト", 200, 20, "Sude"));
        menus.add(new Menu(4, "ナゲット", 350, 8, "Side"));
        menus.add(new Menu(5, "コーラ", 100, 30, "Drink"));
        menus.add(new Menu(6, "コーヒー", 100, 30, "Drink"));

        MenuCatalog catalog = new MenuCatalog(menus);

        PurchaseView view = new PurchaseView(catalog);
        
        view.main_view();  
    }
}
