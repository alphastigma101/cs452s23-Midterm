package edu.sou.cs452.jlox;
import java.util.List;
public class LoxPrintFunction implements LoxCallable {
    @Override 
    public int arity() { return 1; }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        for (Object argument : arguments) {
            System.out.print(String.valueOf(argument));
        }
        return null;
    }

}