package edu.sou.cs452.jlox;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new LoxCallable() {
          @Override
          public int arity() { return 0; }
    
          @Override
          public Object call(Interpreter interpreter, List<Object> arguments) { return (double)System.currentTimeMillis() / 1000.0; }
    
          @Override
          public String toString() { return "<native fn>"; }
        });
    }
    /** 
     * ...
     * @param expression is List type
     * @return None
    */
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) { Lox.runtimeError(error); }
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
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    private void execute(Stmt stmt) { stmt.accept(this); }
    void resolve(Expr expr, int depth) { locals.put(expr, depth); }
    /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } 
        finally { this.environment = previous; }
    }
    /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
      executeBlock(stmt.statements, new Environment(environment));
      return null;
    }
    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        environment.define(stmt.name.lexeme, null);
        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            
            LoxFunction function = new LoxFunction(method, environment,
            method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }
        LoxClass klass = new LoxClass(stmt.name.lexeme, methods);
        environment.assign(stmt.name, klass);
        return null;
    }
    /** 
     * @param stmt is a Stmt.Expression type 
     * @return Returns null iif... 
    */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment,false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }
    /** 
     * @param stmt is a Stmt.Expression type 
     * @return Returns null iif... 
    */
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {  execute(stmt.thenBranch); } 
        else if (stmt.elseBranch != null) { execute(stmt.elseBranch); }
        return null;
    }
    /** 
     * @param stmt is a Stmt.Print type 
     * @return Returns null iif....
    */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) { value = evaluate(stmt.initializer); }
        environment.define(stmt.name.lexeme, value);
        return null;
    }
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) { execute(stmt.body); }
        return null;
    }
    /** 
     * @param stmt is a Stmt.Print type 
     * @return Returns null iif....
    */
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }
    @Override
    public Object visitLoxListExpr(Expr.LoxList expr) {
        List<Object> values = new ArrayList<>();
        for (Expr value : expr.elements) {
            values.add(evaluate(value));
        }
        return values;
    }
    
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); 
        switch (expr.operator.type) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) { return (double)left + (double)right; } 
                if (left instanceof String && right instanceof String) { return (String)left + (String)right; }
                break;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            default:
                break;
        }
        return null;
    }
    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {  arguments.add(evaluate(argument)); }
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren,"Can only call functions and classes.");
        }
        LoxCallable function = (LoxCallable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }
    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof LoxInstance) { return ((LoxInstance) object).get(expr.name); }
        throw new RuntimeError(expr.name, "Only instances have properties.");
    }
    /** 
     * @param Expr.Binary 
     * @return null if it is not reachable 
    */
    @Override
    public Object visitVariableExpr(Expr.Variable expr) { 
        return lookUpVariable(expr.name, expr);
        //return environment.get(expr.name); 
    }
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) { return environment.getAt(distance, name.lexeme); } 
        else { return globals.get(name); }
    }
    /** 
     * @param object is a Object Type
     * @return return false if object is null otherwise, cast object into a boolean and return true
    */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case MINUS:
                return -(double)right;
            default:
                break;
        }
        // Unreachable.
        return null;
    }
    /** 
     * @param expr is a Expr.Grouping type 
     * @return evaluate(expr.expression)
    */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
    
        return a.equals(b);
    }
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
    /** 
     * @param expr is a Expr.Grouping type 
     * @return evaluate(expr.expression)
    */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    /** 
     * @param expr is a Expr.Grouping type 
     * @return evaluate(expr.expression)
    */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
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

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
      Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }
    @Override
    public Object visitSetExpr(Expr.Set expr) {
      Object object = evaluate(expr.object);
      if (!(object instanceof LoxInstance)) {  throw new RuntimeError(expr.name,"Only instances have fields."); }
      Object value = evaluate(expr.value);
      ((LoxInstance)object).set(expr.name, value);
      return value;
    }
    @Override
    public Object visitThisExpr(Expr.This expr) { return lookUpVariable(expr.keyword, expr); }

    @Override
    public Object visitListGetExpr(Expr.ListGet expr) {
        Object object = evaluate(expr.identifier);
        if (!(object instanceof List)) {
            Token identifier = (Token) expr.identifier.accept(this);
            throw new RuntimeError(identifier, "Only lists have indexes");
        }
        Object index = evaluate(expr.index);
        if(!(index instanceof Double)) { throw new RuntimeError(expr.bracket, "Index must be a number"); }
        Integer idx = ((Double) index).intValue();
        List<Object> lst = (List<Object>) object;
        if (idx < 0 || idx >= lst.size()) { 
            throw new RuntimeError(expr.bracket, "Index out of bounds"); 
        }
        return ((List<Object>) object).get(idx);
    }
}
