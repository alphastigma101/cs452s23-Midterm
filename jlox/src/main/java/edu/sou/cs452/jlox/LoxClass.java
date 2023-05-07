package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
  final String name;
  private LoxClass superclass;
  private final Map<String, LoxFunction> methods;
  private final Map<String, Object> fields = new HashMap<>();
  /** 
    * @param name is a String type
    * @param superclass is a LoxClass type. From the book craftinginterpeters, they use klass
    * @param methods Is a Map type. the keywords are String type and the values are LoxFunction type
    * @return None
  */
  LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  public void setSuperclass(LoxClass superclass) {
    this.superclass = superclass;
  }
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  Object get(Token name) {
    if (fields.containsKey(name.lexeme)) { return fields.get(name.lexeme); }
    LoxFunction method = findMethod(name.lexeme);
    if (method != null) return method.bind(this);
    if(superclass !=null && superclass.name != null){ return superclass.get(name); }
    throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
  }
  /** 
     * @param name Is a String type
     * @return null if there is not a match in the map
  */
  LoxFunction findMethod(String name) { if (methods.containsKey(name)) { return methods.get(name); }
    if (superclass != null) { return superclass.findMethod(name); }
    return null;
  }
   /** 
     * @param name is a Token type 
     * @param value is a Object type
     * @return None 
     * It puts the name of the class or function into the fields. Which is a map 
  */
  void set(Token name, Object value) { fields.put(name.lexeme, value); }
  /** 
     * @param None
     * @return name 
  */
  @Override
  public String toString() { return name; }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) throws IOException {
    LoxClass instance = new LoxClass("", this, new HashMap<String, LoxFunction>());
    LoxFunction initializer = findMethod("init");
    if (initializer != null) { initializer.bind(instance).call(interpreter, arguments); }
    return instance;
  }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public int arity() {
    LoxFunction initializer = findMethod("init");
    if (initializer == null) return 0;
    return initializer.arity();
  }
}
