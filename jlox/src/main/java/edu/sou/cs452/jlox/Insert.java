package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;

class Insert implements LoxCallable {
    protected ArrayList<Object> arr;
    protected Double d;
    Insert() {}

    public Insert(ArrayList<Object> arr) { this.arr = arr; }
    @Override
    public int arity() { return 2; }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        d = (double)arguments.get(0);
        int index = d.intValue();
        d = (double)arguments.get(1);
        int NewValue = d.intValue();
        for (int i = 0; i < arr.size(); i++) {
            if (i == index) {
                arr.add(index, (double)NewValue);
                return arr;
            }
        }
        throw new RuntimeError(TokenType.IDENTIFIER, "Index does not exist!");
    }
}
