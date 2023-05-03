package edu.sou.cs452.jlox;

class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) { return expr.accept(this); }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) { return parenthesize(expr.operator.lexeme,expr.left, expr.right); }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) { return parenthesize("group", expr.expression); }
    @Override
    public String visitUnaryExpr(Expr.Unary expr) { return parenthesize(expr.operator.lexeme, expr.right); }
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) { return "nil"; }
        if( expr.value instanceof LiteralNumber ) { return expr.value.getter().toString(); }
        if( expr.value instanceof LiteralBoolean ) { return expr.value.getter().toString(); }
        if( expr.value instanceof LiteralString ) { return expr.value.getter().toString(); }
        if (expr.value instanceof LiteralNull) { return expr.value.getter();}
        return "You're Screwed";
    }
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
          builder.append(" ");
          builder.append(expr.accept(this));
        }
        builder.append(")");
    
        return builder.toString();
    }
}