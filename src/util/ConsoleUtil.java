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
    public static void title_view(String... lines) {
        final int displayWidth = 60; // 表示幅
        String border = "+" + "-".repeat(displayWidth+2) + "+";
    
        System.out.println(border);
        for (String line : lines) {
            int padding = displayWidth - getDisplayWidth(line);
            System.out.print("| " + line);
            System.out.print(" ".repeat(padding));
            System.out.println(" |");
        }
        System.out.println(border);
    }
    

    // 文字列から全角文字を半角二文字換算の長さを取得
    public static int getDisplayWidth(String s) {
        int width = 0;
        for (char c : s.toCharArray()) {
            // 全角（日本語・記号）なら2幅、それ以外なら1幅とする
            if (String.valueOf(c).matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}１２３４５６７８９０ー～、。・「」『』【】（）｛｝！？：；｀＋−＝＿｜￥＾＠]")) {
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }
    

    // エラーを表示
    public static void error_view(String message){
        System.out.println("[エラー]" + message);
    }

}
