package app;

import model.DBManager;
import view.cli.Employee;

/**
 * 社員向け管理画面を起動するクラス。
 * 実行すると在庫管理や注文ステータス変更などのCLIが表示される。
 */
public class EmployeeMain {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager("TestMenu.db");
        dbManager.connect();

        Employee.showMain(dbManager);

        dbManager.disconnect();
    }
}
