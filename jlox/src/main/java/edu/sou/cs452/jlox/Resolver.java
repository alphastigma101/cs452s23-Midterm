package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    Resolver(Interpreter interpreter) throws IOException { this.interpreter = interpreter; }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }
    private ClassType currentClass = ClassType.NONE;
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    void resolve(List<Stmt> statements) throws IOException {
        for (Stmt statement : statements) { resolve(statement); }
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) throws IOException {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitClassStmt(Stmt.Class stmt) throws IOException {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.name);
        define(stmt.name);
        if (stmt.superclass != null && stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) { Lox.error(stmt.superclass.name, "A class can't inherit from itself."); }
        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }
        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super", true);
        }
        beginScope();
        scopes.peek().put("this", true);
        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) { declaration = FunctionType.INITIALIZER; }
            resolveFunction(method, declaration); // [local]
        }
        endScope();
        if (stmt.superclass != null) endScope();
        currentClass = enclosingClass;
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) throws IOException {
        resolve(stmt.expression);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) throws IOException {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitIfStmt(Stmt.If stmt) throws IOException {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) throws IOException {
        resolve(stmt.expression);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) throws IOException {
        if (currentFunction == FunctionType.NONE) { Lox.error(stmt.keyword, "Can't return from top-level code."); }
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) { Lox.error(stmt.keyword,"Can't return a value from an initializer."); }
            resolve(stmt.value);
        }
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) throws IOException {
        declare(stmt.name);
        if (stmt.initializer != null) {
        resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitListGetExpr(Expr.ListGet expr) throws IOException {
        resolve(expr.identifier);
        resolve(expr.identifier);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitWhileStmt(Stmt.While stmt) throws IOException {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitAssignExpr(Expr.Assign expr) throws IOException {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) throws IOException {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitCallExpr(Expr.Call expr) throws IOException {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) { resolve(argument); }
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitGetExpr(Expr.Get expr) throws IOException {
        resolve(expr.object);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitLoxListExpr(Expr.LoxList expr) throws IOException {
        for (Expr element : expr.elements) { resolve(element); }
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) throws IOException {
        resolve(expr.expression);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitLogicalExpr(Expr.Logical expr) throws IOException {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitSetExpr(Expr.Set expr) throws IOException {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) { Lox.error(expr.keyword,"Can't use 'super' outside of a class.");} 
        else if (currentClass != ClassType.SUBCLASS) { Lox.error(expr.keyword,"Can't use 'super' in a class with no superclass."); }
        resolveLocal(expr, expr.keyword);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
        Lox.error(expr.keyword,"Can't use 'this' outside of a class.");
        return null;
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitUnaryExpr(Expr.Unary expr) throws IOException {
        resolve(expr.right);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
        Lox.error(expr.name,"Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name);
        return null;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void resolve(Stmt stmt) throws IOException { stmt.accept(this); }

    private void resolve(Expr expr) throws IOException { expr.accept(this); }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void resolveFunction( Stmt.Function function, FunctionType type) throws IOException {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
        declare(param);
        define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */    
    private void beginScope() { scopes.push(new HashMap<String, Boolean>()); }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void endScope() { scopes.pop(); }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) { Lox.error(name,"Already a variable with this name in this scope."); }
        scope.put(name.lexeme, false);
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
        if (scopes.get(i).containsKey(name.lexeme)) {
            interpreter.resolve(expr, scopes.size() - 1 - i);
            return;
        }
        }
    }
}
