package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * CLIの描画の共通処理を管理するクラス
 */

public class ConsoleUtil {

    // ======= Constants =======
    public static final int DISPLAY_WIDTH = 60; // 表示幅

    // ======= Methods =======

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
                showError("無効な数字です。再入力してください。");
            }
        }
    }

    // ヘッダー文章を描画。引数は可変長
    public static void showHeader(String... lines) {
        String border = "+" + "-".repeat(DISPLAY_WIDTH+2) + "+";
    
        System.out.println(border);
        for (String line : lines) {
            for (String wrapped : wrapLine(line, DISPLAY_WIDTH)) {
                int padding = DISPLAY_WIDTH - getLineLength(wrapped);
                System.out.print("| " + wrapped);
                System.out.print(" ".repeat(padding));
                System.out.println(" |");
            }
        }
        System.out.println(border);
    }

    // エラーを表示
    public static void showError(String message){
        System.out.println("[エラー]" + message);
    }
    
    // 全角文字を半角二文字分としたときの文字列の長さを取得
    public static int getLineLength(String s) {
        int width = 0;
        for (char c : s.toCharArray()) {
            // 全角（日本語・記号）なら2幅、それ以外なら1幅とする
            if (isFullWidth(c)) {
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    // maxWidth文字区切りで文字列の分割を行う
    public static List<String> wrapLine(String line,int maxWidth) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int width = 0;

        for (char c : line.toCharArray()) {
            int charWidth = isFullWidth(c) ? 2 : 1;

            if (width + charWidth > maxWidth) {
                result.add(current.toString());
                current = new StringBuilder();
                width = 0;
            }

            current.append(c);
            width += charWidth;
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result;
    }

    // 全角であればtrueを返す
    public static boolean isFullWidth(char c){
        return String.valueOf(c).matches("[\\p{IsHiragana}\\p{IsKatakana}\\p{IsHan}１２３４５６７８９０ー～、。・「」『』【】（）｛｝！？：；｀＋−＝＿｜￥＾＠]");
    }

}
