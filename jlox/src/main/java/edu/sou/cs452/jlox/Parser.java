package edu.sou.cs452.jlox;
import java.util.List;
import static edu.sou.cs452.Lab5.TokenType.*;
class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;
    /** 
     * This function is the default constructor for Parse()
     * @param tokens is a List type
     * @return None
    */
    Parser(List<Token> tokens) { this.tokens = tokens; }
    Expr parse() {
        try { return expression(); } 
        catch (ParseError error) { return null; }
    }
    /** 
     * This function expression()...
     * @param types is a enum type of TokenTypes
     * @param message is a String type. Capitalization String is a wrapper for the object that is declared with 
     * @return Returns a new instance of a subclass such as Binary and Grouping. When it creates a new instance, it saves the values that were assigned to it
    */
    private Expr expression() {
        // Whenever you recrusive call it, it changes to the next token in the list
        if (match(LEFT_PAREN,RIGHT_PAREN)) {
            Expr expr = expression();
            if (match(RIGHT_PAREN)) { 
                advance();
                return pair(expr); 
            }
            else if (match(MINUS, PLUS, STAR, SLASH)) {
                Token operator = previous();
                Expr right = expression();
                if (previous() == tokens.get(current - 1)) {
                    // This statement gets the inner
                    pair(expr);
                    return new Expr.Binary(expr, operator, right);
                }
                else { consume(RIGHT_PAREN, "Expexted a ) at the end of the expression"); }
                throw error(peek(), "Expect a binary operation such as +, -, *, /");
            } 
        } 
        else if (match(NUMBER)) { return new Expr.Literal(previous().literal); }
        throw error(peek(), "Expect an expression."); 
    }
    /** 
     * This function pair() returns a new instance of grouping which saves the values that were assigned to it 
     * @param expr is a Expr type 
     * @return returns new Expr.Grouping(expr);
    */
    private Expr pair(Expr expr) {  return new Expr.Grouping(expr); }
    /** 
     * This function match() iterates through the list of tokens that was created by the Scanner class
     * Will return false if the current Token in the list is not the token that was passed into match()
     * If it does return true, that means we have found the token in the list and will execute the code inside of it
     * @parameter types is a List of TokenTypes
     * @return True or False
    */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    /** 
     * This function consume() consumes the token by using the function advance()
     * Will throw an error out if something happens
     * @param types is a enum type of TokenTypes
     * @param message is a String type. Capitalization String is a wrapper for the object that is declared with 
     * @return Returns advance()
    */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }
    /** 
     * This function error().....
     * Will return ...
     * @param token is a List of TokenTypes
     * @param message is a String type. Capitalization String is a wrapper for the object that is declared with 
     * @return True or False
    */
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }
    /** 
     * This function check() checks to see if the element is EOF by calling in isAtEnd(). If it is not, it will call in peek()
     * If peek() gets called in, it will return the current token and see if the token that was passed into match() matches the token that was returned by peek()
     * @param types is a enum object. Whatever TokenType has inside it, type will be that token
     * @return Will return false if the element of the list is 
    */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    /** 
     * This function advance() advances through the list by incrementing the variable current
     * @param None
     * @return Will return previous() 
    */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    /** 
     * This function isAtEnd() checks to see if the element is EOF by using the function peek().
     * If the last token is EOF, then it has reached the end of the string and will return true
     * @param None
     * @return Will return False if the current element is not EOF, will return true if the current element is EOF
    */
    private boolean isAtEnd() { return peek().type == EOF; }
    /** 
     * This function peek() gets the current token and returns it back to check()
     * @param None
     * @return Returns the current element of the list 
    */
    private Token peek() { return tokens.get(current); }
    /** 
     * This function previous() gets the previous element of the list which is the previous token 
     * @param None
     * @return Returns the previous element in the list 
    */
    private Token previous() { return tokens.get(current - 1); }
}