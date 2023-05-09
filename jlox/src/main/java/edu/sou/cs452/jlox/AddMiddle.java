package edu.sou.cs452.jlox;

import java.util.ArrayList;
import java.util.List;

class AddMiddle implements LoxCallable {
    protected ArrayList<Object> arr;
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * Resolver.java checks the method/variable means and decides exactly which method/variable is called
     * Environment.java gets the method/variable by searching for the token and allows .lox file to use the method/variable
    */
    AddMiddle() {}
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * @param arr is a ArrayList type. This public constructor get the array from the .lox file and assigns it with the protected arr. visitGetExpr has the array
    */
    public AddMiddle(ArrayList<Object> arr) {
        this.arr = arr;
    }
    /** 
     * This function controls the amount of arguments that can be used in the function addmiddle
     * @param None
     * @return 1
    */
    @Override
    public int arity() { return 1;}
    @Checker
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        double NewValue = (double)arguments.get(0);
        int size = arr.size();
        if (size % 2 == 0) {
            int index = (int)(arr.size() / 2);
            arr.add(index, NewValue);
        }
        else {
            int index = (int)(arr.size() / 2) - 1;
            arr.add(index, NewValue);
        }
        return arr;
    }   
}