package edu.sou.cs452.jlox;

class RuntimeError extends RuntimeException {
  final Token token;
  final TokenType type;
  /** 
    * ....
    * @param token Is a Token type
    * @param message is a String type
    * @return None
  */
  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
    this.type = null;
  }
  public RuntimeError(TokenType brackets, String message) {
    super(message);
    this.type = brackets;
    this.token = null;
  }
}