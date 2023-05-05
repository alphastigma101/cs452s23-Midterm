package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;
  /** 
    * @param stmt is a Stmt type
    * @return stmt.accecpt(this)
  */
  LoxClass(String name, LoxClass superclass,Map<String, LoxFunction> methods) {
    this.superclass = superclass;
    this.name = name;
    this.methods = methods;
  }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  LoxFunction findMethod(String name) { if (methods.containsKey(name)) { return methods.get(name); }
    if (superclass != null) { return superclass.findMethod(name); }
    return null;
  }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public String toString() { return name; }
   /** 
     * @param stmt is a Stmt type
     * @return stmt.accecpt(this)
  */
  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) throws IOException {
    LoxInstance instance = new LoxInstance(this);
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
