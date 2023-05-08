package edu.sou.cs452.jlox;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
abstract class AbstractProcessor {
    static class ListBoundsChecker extends AbstractProcessor {
        static class Interval extends ListBoundsChecker {
            protected int lower;
            protected int upper;
            public Interval(int lower, int upper) {
                this.lower = lower;
                this.upper = upper;
            } 
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
        
        public static ListDomain getDomain(ArrayList<Object> list) {
            ListDomain domain = new ListDomain();
            if (list.size() == 0) {
                // Empty list has no valid indices.
                return domain;
            }
            domain.addInterval(new Interval(0, list.size() - 1));
            return domain;
        }
    
        public static boolean isIndexInBounds(ArrayList<Object> list, int index) {
            ListDomain domain = getDomain(list);
            return domain.contains(index);
        }
    }
}