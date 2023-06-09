package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;
import static edu.sou.cs452.jlox.TokenType.*;
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    /**
     * This function scanToken() it parses the string and adds tokens 
     * @param None
     * @return None 
    */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ']': addToken(RIGHT_BRACKET); break;
            case '[': addToken(LEFT_BRACKET); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-':  addToken(MINUS);  break;
            case '+':  addToken(PLUS); break;
            case '/':  addToken(SLASH); break;
            case ';': addToken(SEMICOLON); break;
            case '*':  addToken(STAR); break; 
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case 'o':
                if (match('r')) { addToken(OR); }
                break;
            case ' ':
            case '\r':
            case '\t': source.replace(" ", ""); // remove whitespace
              break;
            case '\n':
              line++;
              break;
            default: 
                if (isDigit(c)) { number(); }
                //else if (isAlpha(c)) { identifier(); }
                //Lox.error(line, "Unexpected character.");
                break;
        }
    }
    /**
     * The function peek() peeks at the current character in the string
     * @param None
     * @return The current character back
    */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    /**
     * This function number() Checks to see if the numbers are either negative or positive and will assign a abstractvalue to it
     * @param None
     * @return None 
    */
    private void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
          // Consume the "."
          advance();
    
          while (isDigit(peek())) advance();
        }
        // Create varaible value that parses doubles and place it into addToken
        //addToken(NUMBER, value);
    }
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
          if (peek() == '\n') line++;
          advance();
        }
    
        if (isAtEnd()) {
          Lox.error(line, "Unterminated string.");
          return;
        }
    
        // The closing ".
        advance();
    
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        //addToken(STRING, (Object)value);
    }
    /**
     * This function peekNext() it peeks at the next character
     * @param None
     * @return Returns source.charAt(current + 1)
    */
    private char peekNext() {
        if ( current + 1 >= source.length() ) return '\0';
        return source.charAt(current + 1);
    }
    /**
     * This function is match()
     * @param expected is a char type
     * @return returns true and increment current if a match was found otherwise it will return false
    */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    /**
     * This function isDigit() it checks and sees if 
     * @param None
     * @return None 
    */
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    /**
     * This function Scanner() is the default constructor
     * @param source is a String type
     * @return None. But it assigns the private variable source with the source which is the string
    */
    Scanner(String source) { this.source = source; }
    /**
     * This function scanTokens()
     * @param None
     * @return returns the tokens 
    */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
    /**
     * This function isAtEnd() checks to see if the string is at the end
     * @param None
     * @return Returns current >= source.length();
    */
    private boolean isAtEnd() { return current >= source.length(); }
    /**
     * This function advance() it advances the source by incrementing it
     * @param None
     * @return Returns source.charAt(current++);
    */
    private char advance() { return source.charAt(current++); }
    /**
     * This function addToken() puts the tokens by calling addToken
     * @param type is a TokenType 
     * @param null
     * @return None
    */
    private void addToken(TokenType type) { 
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, null, line)); 
    }
    
}
