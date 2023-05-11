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
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
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
            @Checker
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
    /**
     * Process does everything. It is what lets out the warning that the user is trying to access out of bounds!
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Checker.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                for (VariableElement variable : method.getParameters()) {
                    if (variable.asType().getKind() == TypeKind.ARRAY) {
                        TypeMirror arrayType = variable.asType();
                        Element enclosingElement = variable.getEnclosingElement();
                        if (enclosingElement instanceof ExecutableElement) {
                            // Parameter is defined in a method
                            ExecutableElement executableElement = (ExecutableElement) enclosingElement;
                            for (VariableElement param : executableElement.getParameters()) {
                                if (variable.equals(param)) {
                                    // Found the matching parameter
                                    ArrayType array = (ArrayType) arrayType;
                                    ListBoundsChecker.isIndexInBounds((ArrayList<Object>)array, index);
                                    break;
                                }
                            }
                        } 
                        else if (enclosingElement instanceof TypeElement) {
                            // Parameter is defined in a constructor or initializer
                            TypeElement typeElement = (TypeElement) enclosingElement;
                            for (ExecutableElement constructor : ElementFilter.constructorsIn(typeElement.getEnclosedElements())) {
                                for (VariableElement param : constructor.getParameters()) {
                                    if (variable.equals(param)) {
                                        // Found the matching parameter
                                        ArrayType array = (ArrayType) arrayType;
                                        ListBoundsChecker.isIndexInBounds((ArrayList<Object>)array, index);
                                        break;
                                    }
                                }
                            }
                            for (VariableElement field : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                                if (variable.equals(field)) {
                                    // Found the matching field
                                    ArrayType array = (ArrayType) arrayType;
                                    ListBoundsChecker.isIndexInBounds((ArrayList<Object>)array, index);
                                    break;
                                }
                            }
                        }
                    }
                }
            } 
            else if (element.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) element;
                if (field.asType().getKind() == TypeKind.ARRAY) {
                    TypeMirror arrayType = field.asType();
                    Element enclosingElement = field.getEnclosingElement();
                    // Field is defined in a class
                    for (Element enclosedElement : enclosingElement.getEnclosedElements()) {
                        if (enclosedElement.getKind() == ElementKind.FIELD) {
                            VariableElement enclosedField = (VariableElement) enclosedElement;
                            if (field.equals(enclosedField)) {
                                // Found the matching field
                                ArrayType array = (ArrayType) arrayType;
                                ListBoundsChecker.isIndexInBounds((ArrayList<Object>)array, index);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}