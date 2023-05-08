package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;

class Insert implements LoxCallable {
    protected ArrayList<Object> arr;
    protected Double d;
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * Resolver.java checks the method/variable means and decides exactly which method/variable is called
     * Environment.java gets the method/variable by searching for the token and allows .lox file to use the method/variable
    */
    Insert() {}
    /** 
     * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
     * @param arr is a ArrayList type. This public constructor get the array from the .lox file and assigns it with the protected arr. visitGetExpr has the array
    */
    public Insert(ArrayList<Object> arr) { this.arr = arr; }
    /** 
     * This function controls the amount of arguments that can be used in the function insert. the functions can be used in a .lox file
     * @param None
     * @return 2
    */
    @Override
    public int arity() { return 2; }
    /** 
     * This function call takes in the arguments that were given and uses the function .add that comes with importing ArrayList
     * It inserts a value at a certain position. This value is stored in the arguments list and is accessed by using the .get
     * @param interpreter is a Interpreter type
     * @param arguments is a List<Object> type
     * @return the modified array that the public constructor got from visitGetExpr function located in Interpreter.java
    */
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
