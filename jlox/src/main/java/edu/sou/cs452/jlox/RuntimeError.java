package edu.sou.cs452.jlox;

class RuntimeError extends RuntimeException {
  /** 
    * ....
    * @param token Is a Token type
    * @param message is a String type
    * @return None
  */
  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
  final Token token;
}