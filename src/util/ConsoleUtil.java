package util;

import java.util.Scanner;
/**
 * CLIの共通処理を管理するクラス
 */
public class ConsoleUtil {

    // 画面をクリアする(UNIX系で有効)
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // 数字の入力のみを受け付け、正しい値が入力されるまで繰り返す
    public static int safeIntInput(String prompt,Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                scanner.nextLine();
                error_view("無効な数字です。再入力してください。");
            }
        }
    }

    // タイトルフォーマットで文章を描画。引数は可変長
    public static void title_view(String... title){
        System.out.println("+------------------------------------------------------+");
        for(String t:title){
            System.out.printf("| %-50s |\n", t);
        }
        System.out.println("+------------------------------------------------------+");
    }

    // エラーを表示
    public static void error_view(String message){
        System.out.println("[エラー]" + message);
    }

}
