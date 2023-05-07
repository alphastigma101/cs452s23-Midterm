package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;

class Append implements LoxCallable {
    protected ArrayList<Object> arr;
    Append() {};
    public Append(ArrayList<Object> arr) { this.arr = arr; }
    @Override
    public int arity() { return 1; }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      Object Value = arguments.get(0);
      arr.add(Value);
      return arr;
    }
}