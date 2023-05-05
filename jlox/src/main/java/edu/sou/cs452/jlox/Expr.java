package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.List;

abstract class Expr {
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  interface Visitor<R> {
    R visitAssignExpr(Assign expr) throws IOException;
    R visitBinaryExpr(Binary expr) throws IOException;
    R visitCallExpr(Call expr) throws IOException;
    R visitGetExpr(Get expr) throws IOException;
    R visitGroupingExpr(Grouping expr) throws IOException;
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr) throws IOException;
    R visitSetExpr(Set expr) throws IOException;
    R visitSuperExpr(Super expr);
    R visitThisExpr(This expr);
    R visitUnaryExpr(Unary expr) throws IOException;
    R visitVariableExpr(Variable expr);
    R visitLoxListExpr(LoxList expr) throws IOException;
    R visitListGetExpr(ListGet expr) throws IOException;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Assign extends Expr {
    Assign(Token name, Expr value) {

      this.name = name;
      this.value = value;
    }

    final Token name;
    final Expr value;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitAssignExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {

      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitBinaryExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class LoxList extends Expr {
    LoxList(List<Expr> elements) {

      this.elements = elements;
    }

    final List<Expr> elements;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitLoxListExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Call extends Expr {
    Call(Expr callee, Token paren, List<Expr> arguments) {

      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    final Expr callee;
    final Token paren;
    final List<Expr> arguments;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitCallExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Get extends Expr {
    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    final Expr object;
    final Token name;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitGetExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class ListGet extends Expr {
    ListGet(Expr identifier, Token bracket, Expr index) {

      this.identifier = identifier;
      this.bracket = bracket;
      this.index = index;
    }

    final Expr identifier;
    final Token bracket;
    final Expr index;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitListGetExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Grouping extends Expr {
    Grouping(Expr expression) {

      this.expression = expression;
    }

    final Expr expression;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitGroupingExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Literal extends Expr {
    Literal(Object value) {

      this.value = value;
    }

    final Object value;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Logical extends Expr {
    Logical(Expr left, Token operator, Expr right) {

      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    final Expr left;
    final Token operator;
    final Expr right;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitLogicalExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Set extends Expr {
    Set(Expr object, Token name, Expr value) {

      this.object = object;
      this.name = name;
      this.value = value;
    }

    final Expr object;
    final Token name;
    final Expr value;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitSetExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Super extends Expr {
    Super(Token keyword, Token method) {
      this.keyword = keyword;
      this.method = method;
    }

    @Override
    public
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSuperExpr(this);
    }

    final Token keyword;
    final Token method;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class This extends Expr {
    This(Token keyword) {

      this.keyword = keyword;
    }

    final Token keyword;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitThisExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {

      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitUnaryExpr(this);
    }
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Variable extends Expr {
    Variable(Token name) {

      this.name = name;
    }

    final Token name;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }
  }

  public abstract <R> R accept(Visitor<R> visitor) throws IOException;
}
