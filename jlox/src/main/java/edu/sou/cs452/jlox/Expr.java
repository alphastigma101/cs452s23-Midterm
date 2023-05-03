package edu.sou.cs452.jlox;

import java.util.List;
import javax.management.ValueExp;
abstract class Expr {
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitUnaryExpr(Unary expr);
  }
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
    public <R> R accept(Visitor<R> visitor) { return visitor.visitBinaryExpr(this); }
  }
  static class Grouping extends Expr {
    Grouping(Expr expression) { this.expression = expression; }
    final Expr expression;
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitGroupingExpr(this); }
  }
  /*  Extends means that it is a subclass of this class 
      So Literal is a subclass of Expr 
      LiterValue is an interface 

  */
  static class Literal extends Expr {
    Literal(LiteralValue value) { this.value = value; }
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitLiteralExpr(this); }
    final LiteralValue value;
  }
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }
    final Token operator;
    final Expr right;
    @Override
    public <R> R accept(Visitor<R> visitor) { return visitor.visitUnaryExpr(this); }
  }
  public abstract <R> R accept(Visitor<R> visitor);
}
