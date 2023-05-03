package edu.sou.cs452.jlox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitLoxListExpr(LoxList expr);
    R visitCallExpr(Call expr);
    R visitGetExpr(Get expr);
    R visitListGetExpr(ListGet expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr);
    R visitSetExpr(Set expr);
    R visitThisExpr(This expr);
    R visitUnaryExpr(Unary expr);
  }
  static class Assign extends Expr {
    Assign(Token name, Expr value) {

      this.name = name;
      this.value = value;
    }

    final Token name;
    final Expr value;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }
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
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }
  }
  static class LoxList extends Expr {
    LoxList(List<Expr> elements) {

      this.elements = elements;
    }

    final List<Expr> elements;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLoxListExpr(this);
    }
  }
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
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }
  }
  static class Get extends Expr {
    Get(Expr object, Token name) {

      this.object = object;
      this.name = name;
    }

    final Expr object;
    final Token name;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }
  }
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
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitListGetExpr(this);
    }
  }
  static class Grouping extends Expr {
    Grouping(Expr expression) {

      this.expression = expression;
    }

    final Expr expression;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }
  }
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
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }
  }
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
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }
  }
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
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {

      this.operator = operator;
      this.right = right;
    }

    final Token operator;
    final Expr right;
    @Override
    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
