package edu.sou.cs452.jlox;
import java.util.List;
import java.util.Arrays;
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

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) { statements.add(declaration()); }
        return statements; 
    }
    /** 
     * @param None
     * @return equality()
    */
    private Expr expression() {
        return assignment();
    }
    private Stmt declaration() {
        try {
            if (match(TokenType.FUN)) return function("function");
            if (match(TokenType.VAR)) return varDeclaration();
    
            return statement();
        } 
        catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    private Stmt statement() {
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }
    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
    
        Stmt initializer;
        if (match(TokenType.SEMICOLON)) { initializer = null; } 
        else if (match(TokenType.VAR)) { initializer = varDeclaration();} 
        else { initializer = expressionStatement(); }
        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) { condition = expression(); }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");
        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) { increment = expression(); }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();
        if (increment != null) { body = new Stmt.Block( Arrays.asList(body,new Stmt.Expression(increment))); }
        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);
        if (initializer != null) { body = new Stmt.Block(Arrays.asList(initializer, body));}
      
        return body;
    }
    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition."); 
    
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
          elseBranch = statement();
        }
    
        return new Stmt.If(condition, thenBranch, elseBranch);
    }
    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) { statements.add(declaration()); }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }
    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
    
        Expr initializer = null;
        if (match(TokenType.EQUAL)) { initializer = expression(); }
    
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }
    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();
    
        return new Stmt.While(condition, body);
    }
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }
    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
    
                parameters.add( consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }
    private Expr assignment() {
        Expr expr = or();
    
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } 
            error(equals, "Invalid assignment target."); 
        }
    
        return expr;
    }
    private Expr or() {
        Expr expr = and();
        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }
    private Expr and() {
        Expr expr = equality();
        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }
    /** 
     * @param None
     * @return equality()
    */
    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    /** 
     * @param None
     * @return equality()
    */
    private Expr comparison() {
        Expr expr = term();
    
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
          do {
            if (arguments.size() >= 255) { error(peek(), "Can't have more than 255 arguments."); }
            arguments.add(expression());
          } while (match(TokenType.COMMA));
        }
    
        Token paren = consume(TokenType.RIGHT_PAREN,"Expect ')' after arguments.");
    
        return new Expr.Call(callee, paren, arguments);
    }
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }
    private Expr call() {
        Expr expr = primary();
        while (true) { 
            if (match(LEFT_PAREN)) { expr = finishCall(expr); } 
            else { break; }
        }
        return expr;
    }
    
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);
        if (match(TokenType.NUMBER, TokenType.STRING)) { return new Expr.Literal(previous().literal); }
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
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