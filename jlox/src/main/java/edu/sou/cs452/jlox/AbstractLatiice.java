package edu.sou.cs452.jlox;
import java.util.HashMap; // import the HashMap class
import static edu.sou.cs452.Lab5.AbstractValue.*;
abstract class AbstractLattice {
    /** 
     * @param leftValue is a AbstractValue type 
     * @param rightValue is a AbstractValue type
     * @return null if it is not reachable 
    */
    public final static AbstractValue plus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;
        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, POSITIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);
    
        // left -
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, NEGATIVE);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, BOTTOM);
        lookup.put(BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, TOP);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(TOP, left);
    
        return lookup.get(leftValue).get(rightValue);
    }
    /** 
     * This function should be triggered by the SLASH token 
     * @param leftValue is a AbstractValue 
     * @param rightValue is a AbstractValue
    */
    public final static AbstractValue divide(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
        HashMap<AbstractValue, AbstractValue> left;

        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE); 
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);
    
        // left -
        left = new HashMap<>();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, ZERO);
        left.put(NEGATIVE, ZERO);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, ZERO);
        lookup.put(ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, BOTTOM);
        lookup.put(BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, BOTTOM); 
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(TOP, left);
        return lookup.get(leftValue).get(rightValue);
    }
    /** 
     * This function should be triggered by the SLASH token 
     * @param leftValue is a AbstractValue 
     * @param rightValue is a AbstractValue
    */
    public final static AbstractValue multiply(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
        HashMap<AbstractValue, AbstractValue> left;
        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE);
        left.put(NEGATIVE, NEGATIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(POSITIVE, left);

        // left -
        left = new HashMap<>();
        left.put(POSITIVE, NEGATIVE);
        left.put(NEGATIVE, POSITIVE);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, ZERO);
        left.put(NEGATIVE, ZERO);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, ZERO);
        lookup.put(ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM);
        left.put(NEGATIVE, BOTTOM);
        left.put(ZERO, BOTTOM);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, BOTTOM);
        lookup.put(BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(POSITIVE, TOP);
        left.put(NEGATIVE, TOP);
        left.put(ZERO, ZERO);
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(TOP, left);
        return lookup.get(leftValue).get(rightValue);
    }
    /** 
     * This function should be triggered by the SLASH token 
     * @param leftValue is a AbstractValue 
     * @param rightValue is a AbstractValue
    */
    public final static AbstractValue minus(AbstractValue leftValue, AbstractValue rightValue) {
        return plus(leftValue, invert(rightValue));
    }
    /** 
     * This function should be triggered by the SLASH token 
     * @param leftValue is a AbstractValue 
     * @param rightValue is a AbstractValue
    */
    public final static AbstractValue invert(AbstractValue rightValue) {
        HashMap<AbstractValue, AbstractValue> lookup = new HashMap<>();
        // right +
        lookup.put(POSITIVE, NEGATIVE);
        lookup.put(NEGATIVE, POSITIVE);
        lookup.put(ZERO, ZERO);
        lookup.put(BOTTOM, TOP);
        lookup.put(TOP, BOTTOM);   
        return lookup.get(rightValue);
    }
}