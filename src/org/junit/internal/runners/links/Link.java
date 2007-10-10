/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.experimental.theories.FailureListener;


public abstract class Link {
	public abstract void run(FailureListener listener);
}