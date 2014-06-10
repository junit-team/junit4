package org.junit.runner.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;

/**
 * A <code>Sorter</code> orders tests. In general you should specify the Sorter you
 * want using <code>SortWith</code> Annotation.
 * To use a <code>Sorter</code> directly, use {@link org.junit.runner.Request#sortWith(Comparator)}.
 * Be aware, as long as a Sorter is manually specified, all declarative sorting methods defined by
 * <code>SortWith</code> Annotation will be overridden.
 *
 * @since 4.0
 */
public class Sorter implements Comparator<Description> {
    /**
     * NULL is a <code>Sorter</code> that leaves elements in an undefined order
     */
    public static final Sorter NULL = new Sorter(new Comparator<Description>() {
        public int compare(Description o1, Description o2) {
            return 0;
        }
    });
    
    public static final Sorter ANNOTATED_SORTER=null;
    
    /**
     * DEFAULT sorting method. It compares hash codes of either names of two test methods or names of two test classes, 
     * depending on whether it is used for sorting execution order for test methods or sorting execution order for
     * test classes defined in a test suite. 
     */
    public static final Sorter DEFAULT=new Sorter(new Comparator<Description>(){

        public int compare(Description o1, Description o2) {
            String[] items = getComparisonItems(o1,o2);
            int hash1 = items[0].hashCode();
            int hash2 = items[1].hashCode();
            if (hash1 != hash2) {
                return hash1 < hash2 ? -1 : 1;
            }
            return NAME_ASCENDING.compare(o1, o2);
        }
        
    });
    
    /**
    * Sorting method based on name ascending order. It compares names of two test methods or names of two test classes, 
    * depending on whether it is used for sorting execution order for test methods or sorting execution order for
    * test classes defined in a test suite. 
    */
    public static final Sorter NAME_ASCENDING = new Sorter(new Comparator<Description>(){
        
        public int compare(Description o1, Description o2) {
            String[] items = getComparisonItems(o1,o2);
            final int comparison = items[0].compareTo(items[1]);
            if (comparison != 0) {
                return comparison;
            }
            return items[0].toString().compareTo(items[1].toString());
        }
        
    });

    /**
    * Random sorting method. It generates a random double value between 0 and 1 during runtime such that 
    * a set of test methods or a set of test classes will always run in a random order. 
    */
    public static final Sorter RANDOM = new Sorter(new Comparator<Description>(){

        public int compare(Description o1, Description o2) {
            return Math.random() - 0.5 >0 ? 1 : -1;
        }
        
    });
    
    //public static final Sorter JVM

    private final Comparator<Description> fComparator;

    /**
     * Creates a <code>Sorter</code> that uses <code>comparator</code>
     * to sort tests
     *
     * @param comparator the {@link Comparator} to use when sorting tests
     */
    public Sorter(Comparator<Description> comparator) {
        fComparator = comparator;
    }

    /**
     * Sorts the test in <code>runner</code> using <code>comparator</code>
     */
    public void apply(Object object) {
        if (object instanceof Sortable) {
            Sortable sortable = (Sortable) object;
            sortable.sort(this);
        }
    }

    public int compare(Description o1, Description o2) {
        return fComparator.compare(o1, o2);
    }
    
    private final static String[] getComparisonItems(Description o1,Description o2){
        if(o1.isSuite()){
            final String[] items={o1.getClassName(),o2.getClassName()};
            return items;
        }else{
            final String[] items={o1.getMethodName(),o2.getMethodName()};
            return items;
        }
    }
   
}
