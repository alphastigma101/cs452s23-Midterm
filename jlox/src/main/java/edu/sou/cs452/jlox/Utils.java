package edu.sou.cs452.jlox;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Utils implements LoxCallable {
    @Override
    public int arity() { return 0; }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    @Override
    public String toString() { return "<native fn>"; }
   
}