package org.junit.runners;

import org.junit.runner.manipulation.Sorter;


/**
 * Sorters enumerator used for defining sorting
 * method for {@link SortWith} 
 * 
 */
public enum Sorters {

    /**
     * default sorting method, using hash code
     * of a method name o a class name to compare
     */
    DEFAULT(Sorter.DEFAULT),
    
    /**
     * random sorting method.
     */
    RANDOM(Sorter.RANDOM),
    
    /**
     * sorting method based on name ascending order
     */
    NAME_ASCENDING(Sorter.NAME_ASCENDING),
    
    /**
     * JVM sorting method
     */
    JVM(null);
    
    private final Sorter sorter;
    
    private Sorters(Sorter sorter){
        this.sorter=sorter;
    }
    
    public Sorter getSorter(){
        return sorter;
    }
    
}
