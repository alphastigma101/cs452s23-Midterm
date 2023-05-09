package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;
class AddFront implements LoxCallable {
    protected ArrayList<Object> arr;
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * Resolver.java checks the method/variable means and decides exactly which method/variable is called
     * Environment.java gets the method/variable by searching for the token and allows .lox file to use the method/variable
    */
    AddFront() {}
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * @param arr is a ArrayList type. This public constructor get the array from the .lox file and assigns it with the protected arr. visitGetExpr has the array
    */
    public AddFront(ArrayList<Object> arr) { this.arr = arr; }
    /** 
     * This function controls the amount of arguments that can be used in the function addfront
     * @param None
     * @return 1
    */
    @Override
    public int arity() { return 1; }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        int index = arr.size() - arr.size();
        @Checker double Value = (double)arguments.get(0);
        arr.add(index, Value);
        return arr;
        
    }
}