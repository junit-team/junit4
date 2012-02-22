package org.hamcrest;

/**
 * The ability of an object to describe itself.
 */
public interface SelfDescribing {
    /**
     * Generates a description of the object.  The description may be part of a
     * a description of a larger object of which this is just a component, so it 
     * should be worded appropriately.
     * 
     * @param description
     *     The description to be built or appended to.
     */
	void describeTo(Description description);
}