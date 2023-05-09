package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
//import javax.tools.Diagnostic;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
@SupportedAnnotationTypes("edu.sou.cs452.jlox.Checker")
public class StaticBoundProcessor extends AbstractProcessor {
    protected ArrayList<Object> list = new ArrayList<>();
    protected int index = 0;
    static class ListBoundsChecker extends StaticBoundProcessor {
        static class Interval extends ListBoundsChecker {
            protected int lower;
            protected int upper;
            public Interval(int lower, int upper) {
                this.lower = lower;
                this.upper = upper;
            } 
            @Checker
            public boolean contains(int index) {
                if (index >= lower && index <= upper) { return index >= lower && index <= upper; }
                throw new IndexOutOfBoundsException("Index is out of bounds!");
            }   
            public Interval intersect(Interval other) {
                int newLower = Math.max(this.lower, other.lower);
                int newUpper = Math.min(this.upper, other.upper);
                return new Interval(newLower, newUpper);
            }
            public String toString() {
                return "[" + lower + ", " + upper + "]";
            }
        }
        static class ListDomain extends ListBoundsChecker {
            public Set<Interval> intervals;

            public ListDomain() { this.intervals = new HashSet<>(); }

            public void addInterval(Interval interval) { intervals.add(interval); }
            @Checker
            public ListDomain intersect(ListDomain other) {
                ListDomain result = new ListDomain();
                for (Interval i1 : intervals) {
                    for (Interval i2 : other.intervals) {
                        Interval intersection = i1.intersect(i2);
                        if (intersection != null) { result.addInterval(intersection); }
                    }
                }
                return result;
            }   
            @Checker
            public boolean contains(int index) {
                for (Interval i : intervals) {
                    if (i.contains(index)) {
                        return true;
                    }
                }
                return false;
            }
            @Checker
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                for (Interval i : intervals) {
                    sb.append(i.toString());
                    sb.append(",");
                }
                if (intervals.size() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("}");
                return sb.toString();
            }
        }
        @Checker
        public static ListDomain getDomain(ArrayList<Object> list) {
            ListDomain domain = new ListDomain();
            if (list.size() == 0) {
                // Empty list has no valid indices.
                return domain;
            }
            domain.addInterval(new Interval(0, list.size() - 1));
            return domain;
        }
        @Checker
        public static boolean isIndexInBounds(ArrayList<Object> list, int index) {
            ListDomain domain = getDomain(list);
            return domain.contains(index);
        }
        @Override
        public Set<String> getSupportedAnnotationTypes() {
            // Return a set of annotation types that this processor supports.
            // In this case, the processor supports the "Checker" annotation type.
            return Set.of(Checker.class.getName());
        }
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Checker.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                for (VariableElement variable : method.getParameters()) {
                    // Something isn't right here I am willing to bet 
                    if (variable.asType().getKind() == TypeKind.ARRAY) {
                        list = new ArrayList<>();
                        list.add(new Object());
                        ListBoundsChecker.isIndexInBounds(list, index);
                    }
                }
            } 
            else if (element.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) element;
                if (field.asType().getKind() == TypeKind.ARRAY) {
                    list = new ArrayList<>();
                    list.add(new Object());
                    ListBoundsChecker.isIndexInBounds(list, index);
                }
            }
        }
        return true;
    }    
}