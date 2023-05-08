package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  final Environment globals = new Environment();
  Environment environment = globals;
  private final Map<Expr, Integer> locals = new HashMap<>();
  Map<String, LoxFunction> methods = new HashMap<>();
  /** 
    * @param None
    * @return None
    * This is the default constructor for Interpreter. This is where the user can use methods or functions
  */
  Interpreter() {
    globals.define("print", new LoxPrintFunction());
    globals.define("clock", new LoxCallable() {
      @Override
      public int arity() { return 0; }
      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) { return (double)System.currentTimeMillis() / 1000.0; }
      @Override
      public String toString() { return "<native fn>"; }
    });
    globals.define("input", new Utils());
    globals.define("int", new Utils() {
      @Override
      public int arity() { return 1; } // this controls the amount of arguments you can pass into the function
      // The function is the variable that's surrounded in quotation marks and is used by globals.define
      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.get(0) instanceof String) { return Double.parseDouble((String) arguments.get(0)); }
        return null;
      }
    });
    globals.define("append", new Append());
    globals.define("at", new At());
    globals.define("pop", new Pop());
    globals.define("insert", new Insert());
    globals.define("addfront", new AddFront());
    globals.define("addmiddle", new AddMiddle());
    globals.define("clear", new Clear());
  }
  /** 
    * calls in the execute() function
    * @param statements is a List Stmt type
    * @return None
  */
  void interpret(List<Stmt> statements) throws IOException {
    try {
      for (Stmt statement : statements) { execute(statement); }
    } 
    catch (RuntimeError error) { Lox.runtimeError(error); }
  }
  /** 
    * @param expr is a Expr type
    * @return stmt.accecpt(this)
  */
  private Object evaluate(Expr expr) throws IOException { return expr.accept(this); }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  private void execute(Stmt stmt) throws IOException { stmt.accept(this); }
  /** 
    * @param expr is a Expr type
    * @param depth is a int type
    * @return None
    * Executes locals.put(expr,depth)
  */
  void resolve(Expr expr, int depth) { locals.put(expr, depth); }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  void executeBlock(List<Stmt> statements, Environment environment) throws IOException {
    Environment previous = this.environment;
    try { 
      this.environment = environment;
      for (Stmt statement : statements) { execute(statement); }
    } 
    finally { this.environment = previous; }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitBlockStmt(Stmt.Block stmt) throws IOException {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitClassStmt(Stmt.Class stmt) throws IOException {
    Object superclass = null;
    if (stmt.superclass != null) {
      superclass = evaluate(stmt.superclass);
      if (!(superclass instanceof LoxClass)) {
        throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
      }
    }
    environment.define(stmt.name.lexeme, null);
    if (stmt.superclass != null) {
      environment = new Environment(environment);
      environment.define("super", superclass);
    }
    Map<String, LoxFunction> methods = new HashMap<>();
    for (Stmt.Function method : stmt.methods) {
      LoxFunction function = new LoxFunction(method, environment, method.name.lexeme.equals("init"));
      methods.put(method.name.lexeme, function);
    }
    LoxClass klass = new LoxClass(stmt.name.lexeme, (LoxClass)superclass, methods);
    if (superclass != null) { environment = environment.enclosing; }
    environment.assign(stmt.name, klass);
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) throws IOException {
    evaluate(stmt.expression);
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    LoxFunction function = new LoxFunction(stmt, environment,false);
    environment.define(stmt.name.lexeme, function);
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitIfStmt(Stmt.If stmt) throws IOException {
    if (isTruthy(evaluate(stmt.condition))) { execute(stmt.thenBranch); } 
    else if (stmt.elseBranch != null) { execute(stmt.elseBranch); }
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitPrintStmt(Stmt.Print stmt) throws IOException {
    Object value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitReturnStmt(Stmt.Return stmt) throws IOException {
    Object value = null;
    if (stmt.value != null) value = evaluate(stmt.value);
    throw new Return(value);
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitVarStmt(Stmt.Var stmt) throws IOException {
    Object value = null;
    if (stmt.initializer != null) { value = evaluate(stmt.initializer); }
    environment.define(stmt.name.lexeme, value);
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Void visitWhileStmt(Stmt.While stmt) throws IOException {
    while (isTruthy(evaluate(stmt.condition))) { execute(stmt.body); }
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitAssignExpr(Expr.Assign expr) throws IOException {
    Object value = evaluate(expr.value);
    Integer distance = locals.get(expr);
    if (distance != null) { environment.assignAt(distance, expr.name, value);} 
    else { globals.assign(expr.name, value); }
    return value;
  }
  /** 
     * @param expr is a Expr.LoxList type
     * @return values is getting populated with evaluate(value)
  */
  @Override
  public Object visitLoxListExpr(Expr.LoxList expr) throws IOException {
    List<Object> values = new ArrayList<>();
    for (Expr value : expr.elements) { values.add(evaluate(value)); }
    return values;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitBinaryExpr(Expr.Binary expr) throws IOException {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right); // [left]

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
        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double)left / (double)right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double)left * (double)right;
      default:
        break;
    }
    // Unreachable.
    return null;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitCallExpr(Expr.Call expr) throws IOException {
    Object callee = evaluate(expr.callee);

    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) { 
      arguments.add(evaluate(argument)); 
    }
    if (!(callee instanceof LoxCallable)) { 
      throw new RuntimeError(expr.paren, "Can only call functions and classes.");
    } 
    LoxCallable function = (LoxCallable)callee;
    if (arguments.size() != function.arity()) {
      throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
    }
    return function.call(this, arguments);
  }
  /** 
    * @param expr is a Expr.Get type
    * @return throws an error if the variable is trying to use a method
    * This is where the magic happens. If you have a certain type, all you need to do is check what type it is and create another else if branch
    * and add code that deals with it. ArrayList is a class. It is why I added it here
  */
  @Override
  public Object visitGetExpr(Expr.Get expr) throws IOException {
    Object object = evaluate(expr.object);
    if (object instanceof LoxClass) { return ((LoxClass) object).get(expr.name); }
    else if (object instanceof List) {
      if (expr.name.type == TokenType.APPEND) { return new Append((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.AT) { return new At((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.POP) { return new Pop((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.INSERT) { return new Insert((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.ADDFRONT) { return new AddFront((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.ADDMIDDLE) { return new AddMiddle((ArrayList<Object>)object); }
      else if (expr.name.type == TokenType.CLEAR) { return new Clear((ArrayList<Object>)object); }
    }
    throw new RuntimeError(expr.name, "Only instances have properties.");
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) throws IOException { return evaluate(expr.expression); }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitLiteralExpr(Expr.Literal expr) { return expr.value; }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitLogicalExpr(Expr.Logical expr) throws IOException {
    Object left = evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {
      if (isTruthy(left)) return left;
    } 
    else { if (!isTruthy(left)) return left; }
    return evaluate(expr.right);
  }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object visitSetExpr(Expr.Set expr) throws IOException {
    Object object = evaluate(expr.object);
    
    if (!(object instanceof LoxClass)) { // [order]
      throw new RuntimeError(expr.name, "Only instances have fields.");
    }

    Object value = evaluate(expr.value);
    if (expr.name.type == TokenType.PROTO) { ((LoxClass) object).setSuperclass((LoxClass) value); } 
    else { ((LoxClass) object).set(expr.name, value); }
    ((LoxClass)object).set(expr.name, value);
    return value;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object visitSuperExpr(Expr.Super expr) {
    int distance = locals.get(expr);
    LoxClass superclass = (LoxClass)environment.getAt(distance, "super");
    LoxClass object = (LoxClass)environment.getAt(distance - 1, "this");
    LoxFunction method = superclass.findMethod(expr.method.lexeme);
    if (method == null) {
      throw new RuntimeError(expr.method,"Undefined property '" + expr.method.lexeme + "'.");
    }
    return method.bind(object);
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitThisExpr(Expr.This expr) { return lookUpVariable(expr.keyword, expr); }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitUnaryExpr(Expr.Unary expr) throws IOException {
    Object right = evaluate(expr.right);
    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double)right;
      default:
        break;
    }
    return null;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object visitVariableExpr(Expr.Variable expr) { return lookUpVariable(expr.name, expr); }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  private Object lookUpVariable(Token name, Expr expr) {
    Integer distance = locals.get(expr);
    if (distance != null) { return environment.getAt(distance, name.lexeme); } 
    else { return globals.get(name); }
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean)object;
    return true;
  }
  /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
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
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  @Override
  public Object visitListGetExpr(Expr.ListGet expr) throws IOException {
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
