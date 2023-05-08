package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.List;

class Append implements LoxCallable {
  protected ArrayList<Object> arr;
  /** 
    * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
    * Resolver.java checks the method/variable means and decides exactly which method/variable is called
    * Environment.java gets the method/variable by searching for the token and allows .lox file to use the method/variable
  */
  Append() {};
  /** 
    * This is the default constructor. It is used in the Interpreter.java. The Interpreter constuctor calls it in.
    * @param arr is a ArrayList type. This public constructor get the array from the .lox file and assigns it with the protected arr. visitGetExpr has the array
  */
  public Append(ArrayList<Object> arr) { this.arr = arr; }
  /** 
    * This function controls the amount of arguments that can be used in the function addfront
    * @param None
    * @return 1
  */
  @Override
  public int arity() { return 1; }
  /** 
    * This function controls the amount of arguments that can be used in the function addfront
    * @param interpreter is a Interpreter type
    * @param arguments is a List Object type
    * @return returns the modified list
  */
  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Object Value = arguments.get(0);
    arr.add(Value);
    return arr;
  }
}