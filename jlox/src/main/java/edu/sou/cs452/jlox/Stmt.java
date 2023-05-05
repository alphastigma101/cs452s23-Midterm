package edu.sou.cs452.jlox;

import java.io.IOException;
import java.util.List;

abstract class Stmt {
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  interface Visitor<R> {
    R visitBlockStmt(Block stmt) throws IOException;
    R visitClassStmt(Class stmt) throws IOException;
    R visitIfStmt(If stmt) throws IOException;
    R visitExpressionStmt(Expression stmt) throws IOException;
    R visitFunctionStmt(Function stmt) throws IOException;
    R visitPrintStmt(Print stmt) throws IOException;
    R visitReturnStmt(Return stmt) throws IOException;
    R visitVarStmt(Var stmt) throws IOException;
    R visitWhileStmt(While stmt) throws IOException;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  static class Block extends Stmt {
    Block(List<Stmt> statements) { this.statements = statements; }
    final List<Stmt> statements;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException { return visitor.visitBlockStmt(this); }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Class extends Stmt {
    Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    } 
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException { return visitor.visitClassStmt(this); }
    final Token name;
    final Expr.Variable superclass;
    final List<Stmt.Function> methods;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {

      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitIfStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Expression extends Stmt {
    Expression(Expr expression) {

      this.expression = expression;
    }

    final Expr expression;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitExpressionStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Function extends Stmt {
    Function(Token name, List<Token> params, List<Stmt> body) {

      this.name = name;
      this.params = params;
      this.body = body;
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitFunctionStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Print extends Stmt {
    Print(Expr expression) {

      this.expression = expression;
    }

    final Expr expression;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitPrintStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Return extends Stmt {
    Return(Token keyword, Expr value) {

      this.keyword = keyword;
      this.value = value;
    }

    final Token keyword;
    final Expr value;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitReturnStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class Var extends Stmt {
    Var(Token name, Expr initializer) {

      this.name = name;
      this.initializer = initializer;
    }

    final Token name;
    final Expr initializer;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitVarStmt(this);
    }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  static class While extends Stmt {
    While(Expr condition, Stmt body) {

      this.condition = condition;
      this.body = body;
    }
    final Expr condition;
    final Stmt body;
    @Override
    public <R> R accept(Visitor<R> visitor) throws IOException {
      return visitor.visitWhileStmt(this);
    }
  }
  public abstract <R> R accept(Visitor<R> visitor) throws IOException;
}
