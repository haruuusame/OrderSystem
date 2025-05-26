package test.view.cli;

import controller.cli.OrderSessionController;
import view.cli.OrderCliView;

/**
 * PurchaseView を手動で実行・確認するためのテストクラス。
 * 実行すると実際にCLIが描画される。
 */
public class OrderCliViewTest {
    public static void main(String[] args) {

        OrderSessionController sessionController = new OrderSessionController(1, "TestMenu.db");
        OrderCliView view = new OrderCliView(sessionController);

        view.showMain();  
    }
}
