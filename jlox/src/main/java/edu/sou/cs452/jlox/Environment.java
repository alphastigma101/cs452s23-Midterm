package edu.sou.cs452.jlox;
import java.util.HashMap;
import java.util.Map;
class Environment {
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;
    /** 
     * This is the default constructor for environment
     * @param None 
     * @return None
    */
    Environment() { enclosing = null; }
    /** 
     * @param ecnlosing is a Environment type
     * @return None
    */
    Environment(Environment enclosing) { this.enclosing = enclosing; }
    /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) { return values.get(name.lexeme); }
        if (enclosing != null) return enclosing.get(name);
        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }
    /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }
    /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
    */
    void define(String name, Object value) { values.put(name, value); }
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
          environment = environment.enclosing; 
        }
    
        return environment;
    }
    Object getAt(int distance, String name) { return ancestor(distance).values.get(name); }
    void assignAt(int distance, Token name, Object value) { ancestor(distance).values.put(name.lexeme, value); }
    
}