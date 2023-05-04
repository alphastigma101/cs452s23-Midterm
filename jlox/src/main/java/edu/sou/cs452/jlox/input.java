package edu.sou.cs452.jlox;
import java.util.Scanner;

class input {
    final String prompt;
    input(String prompt) { 
        this.prompt = prompt;
        if (this.prompt instanceof String) {
            Scanner scanner = new Scanner(this.prompt);
            System.out.println(scanner.nextLine().trim()); // removes trailing whitespace
            scanner.close();
        }
        //else {throw new RuntimeError(TokenType.STRING, "Expected a string"); }
    }

}