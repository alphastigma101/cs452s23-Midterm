package edu.sou.cs452.jlox;
import java.io.InputStream;
import java.util.Scanner;
public class Utils {
    public static void input(Object prompt) {
        if (prompt instanceof InputStream) {
            if (prompt instanceof String) {
                Scanner scanner = new Scanner((String)prompt);
                prompt = scanner.nextLine().trim();
                System.out.print(prompt);
                scanner.close();
            }      
            else if (prompt instanceof Double) {
                System.out.print(prompt);
            }
            else if (prompt instanceof Integer) {
                System.out.print(prompt);
            }
        }
        else {
            if (prompt instanceof String) {
                Scanner scanner = new Scanner((String)prompt);
                prompt = scanner.nextLine().trim();
                System.out.print(prompt);
                scanner.close();
            }      
            else if (prompt instanceof Double) {
                System.out.print(prompt);
            }
            else if (prompt instanceof Integer) {
                System.out.print(prompt);
            }
        }
    }
}