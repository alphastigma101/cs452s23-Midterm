package edu.sou.cs452.jlox;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Utils implements LoxCallable {
    /** 
     * This function controls the amount of arguments that can be used in the function addfront
     * @param None
     * @return 0
    */
    @Override
    public int arity() { return 0; }
    /** 
     * This function call grabs the user input that was typed from the .lox file
     * @param interpreter is a Interpreter type.
     * @param arguments is a List Object type
     * @return reads the user input
    */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    @Override
    public String toString() { return "<native fn>"; }
   
}