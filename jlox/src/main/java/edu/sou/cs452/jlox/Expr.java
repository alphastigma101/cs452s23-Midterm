package edu.sou.cs452.jlox;


abstract class Expr {
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
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
        public <R> R accept(Visitor<R> visitor) { return visitor.visitBinaryExpr(this); }
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    static class Grouping extends Expr {
        Grouping(Expr expression) { this.expression = expression; }
        final Expr expression;
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitGroupingExpr(this); }
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    static class Literal extends Expr {
        Literal(AbstractValue value) { this.value = value; }
        @Override
        public <R> R accept(Visitor<R> visitor) { return visitor.visitLiteralExpr(this); }
        final AbstractValue value;
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    public abstract <R> R accept(Visitor<R> visitor);
}
