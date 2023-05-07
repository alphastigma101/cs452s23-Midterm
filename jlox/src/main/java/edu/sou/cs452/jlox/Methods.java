package edu.sou.cs452.jlox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Methods implements LoxCallable {
    private ArrayList<Object> arguments;
    protected Object callee;
    
    Methods() {}
    public Methods(Object callee) {
        this.callee = callee;
    }
    public Methods(LoxFunction methods, ArrayList<Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        int sum = 0;
        for (Object arg : this.arguments) {
            sum += (int) arg;
        }
        return sum;
    }
}