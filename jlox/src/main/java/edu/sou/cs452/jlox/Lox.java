package edu.sou.cs452.jlox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    /** 
     * Abs is a subclass 
     * @param None
     * @return none
    */
    private static class Abs extends AbstractInterpreter { }
    private static final Abs abstractinterpreter = new Abs();
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    public static void main(String[] args) throws IOException {
        /*if ( args.length > 1 ) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); 
        } 
        else if ( args.length == 1 ) { runFile(args[0]); } 
        else { runPrompt(); }*/
        runPrompt();
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    private static void runPrompt() throws IOException {
        hadError = false;
        InputStreamReader input = new InputStreamReader(System.in); // this is where the program gets ready for the user to enter some input 
        BufferedReader reader = new BufferedReader(input);
        for (;;) { 
            System.out.print("> ");
            String line = reader.readLine();
            if ( line == null ) break;
            run(line);
            hadError = false;
        }
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();
        // Stop if there was a syntax error.
        if (hadError) return;
        abstractinterpreter.interpret(expression);
        interpreter.interpret(expression);

        // For now, just print the tokens.
        for (Token token : tokens) { System.out.println(token); }
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    static void error(int line, String message) { report(line, "", message); }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) { report(token.line, " at end", message); } 
        else { report(token.line, " at '" + token.lexeme + "'", message); }
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}  

