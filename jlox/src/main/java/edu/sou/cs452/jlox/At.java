package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;

class At implements LoxCallable {
    protected ArrayList<Object> arr;
    At() {};
    public At(ArrayList<Object> arr) {
        this.arr = arr;
    }
    @Override
    public int arity() { return 1; }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      Object Value = arguments.get(0);
      int counter = 0;
        for (Object item : arr) {
            if ((double)item == (double)Value) {
                return item;
            } 
            counter += 1;
        }
        if (counter == arr.size()) { 
            throw new RuntimeError(TokenType.IDENTIFIER, "Error, could not find number in array!"); 
        }
        return arr;
    }
}