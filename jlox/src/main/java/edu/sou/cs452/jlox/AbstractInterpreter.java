package edu.sou.cs452.jlox;

abstract class AbstractInterpreter implements Expr.Visitor<Object> { 
    /** 
        * This is a concrete class that is a subclass of the AbstractLatice class
        * No code is needed to be implemented inside of it as the code inside of AbstractLattice has final mechinisim on it
        * @param None
        * @param None
        * @return None
    */
    static class Operation extends AbstractLattice { }
    /** 
        * ...
        * @param expression is Expr type
        * @return None
    */
    void interpret(Expr expression) { 
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } 
        catch (RuntimeError error) { Lox.runtimeError(error); }
    }
    /** 
        * .....
        * @param object is a Object type
        * @return Returns a string
    */
    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) { text = text.substring(0, text.length() - 2); }
            return text;
        }
        return object.toString();
    }
    /** 
        * This function is a helper which simply sends back the expression
        * @param expr 
        * @return expr.accecpt(this)
    */
    private Object evaluate(Expr expr) { return expr.accept(this); }
    /** 
        * @param Expr.Binary 
        * @return null if it is not reachable 
    */
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); 
        switch (expr.operator.type) {
            case MINUS:
                if (left instanceof AbstractValue && right instanceof AbstractValue) {  return Operation.minus((AbstractValue)left, (AbstractValue) right); }
                throw new RuntimeError(expr.operator, "Something happened while subtracting");
            case PLUS:
                if (left instanceof AbstractValue && right instanceof AbstractValue) { return Operation.plus((AbstractValue)left,(AbstractValue)right); } 
                throw new RuntimeError(expr.operator, "Something happened while adding");
            case SLASH:
                if (left instanceof AbstractValue && right instanceof AbstractValue) { return Operation.divide((AbstractValue) left, (AbstractValue)right); }
                throw new RuntimeError(expr.operator, "Something happened while dividing");
            case STAR:
                if (left instanceof AbstractValue && right instanceof AbstractValue) { return Operation.multiply((AbstractValue)left, (AbstractValue)right); }
                throw new RuntimeError(expr.operator, "Something happened while multiplying");
            default:
                break;
        }
        return null;
    }
    /** 
        * @param expr is a Expr.Grouping type 
        * @return evaluate(expr.expression)
    */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) { return evaluate(expr.expression); }
    /** 
        * @param expr is a Expr.Literal type  
        * @return evaluate(expr.expression)
    */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) { return expr.value; }
}