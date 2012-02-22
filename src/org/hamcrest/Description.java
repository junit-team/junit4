package org.hamcrest;

/**
 * A description of a Matcher. A Matcher will describe itself to a description
 * which can later be used for reporting.
 *
 * @see Matcher#describeTo(Description)
 */
public interface Description {

    /**
     * Appends some plain text to the description.
     */
    Description appendText(String text);
    
    /**
     * Appends the description of a {@link SelfDescribing} value to this description.
     */
    Description appendDescriptionOf(SelfDescribing value);
    
    /**
     * Appends an arbitary value to the description.
     */
    Description appendValue(Object value);
    
    /**
     * Appends a list of values to the description.
     */
    <T> Description appendValueList(String start, String separator, String end,
    							    T... values);

    /**
     * Appends a list of values to the description.
     */
    <T> Description appendValueList(String start, String separator, String end,
    							    Iterable<T> values);
    
    /** 
     * Appends a list of {@link org.hamcrest.SelfDescribing} objects
     * to the description. 
     */
    Description appendList(String start, String separator, String end, 
                           Iterable<? extends SelfDescribing> values);
}
