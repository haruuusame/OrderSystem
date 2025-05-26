package app;

import controller.cli.OrderSessionController;
import view.cli.OrderCliView;

/**
 * 購入・注文画面を起動するクラス。
 * 実行すると実際にCLIが描画される。
 */
public class OrderMain {
    public static void main(String[] args) {

        OrderSessionController sessionController = new OrderSessionController(1, "TestMenu.db");
        OrderCliView view = new OrderCliView(sessionController);

        view.showMain();  
    }
}