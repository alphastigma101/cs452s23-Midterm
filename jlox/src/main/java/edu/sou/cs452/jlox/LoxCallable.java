package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.List;
interface LoxCallable {
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    int arity();
    /** 
        * @param stmt is a Stmt type
        * @return stmt.accecpt(this)
    */
    Object call(Interpreter interpreter, List<Object> arguments) throws IOException;
  
}