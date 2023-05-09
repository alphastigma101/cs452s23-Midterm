package edu.sou.cs452.jlox;
import static edu.sou.cs452.jlox.StaticBoundProcessor.ListBoundsChecker;
import java.util.ArrayList;

public class Arrays {
    @Checker
   public static void main(String[] args) {
        ArrayList<Object> arr = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            int val = i * 2;
            arr.add(i, val);
        } 
        ListBoundsChecker.Interval interval = new ListBoundsChecker.Interval(0, (arr.size() - 1));
        @Checker boolean isInBounds = ListBoundsChecker.isIndexInBounds(arr,12);
        System.out.println("Not in bounds!: " + isInBounds);
    }
    
}



