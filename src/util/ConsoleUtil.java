package util;

import java.util.Scanner;

public class ConsoleUtil {
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static int safeIntInput(String prompt,Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                scanner.nextLine();
                System.out.println("[エラー] 無効な数字です。再入力してください。");
            }
        }
    }
}
