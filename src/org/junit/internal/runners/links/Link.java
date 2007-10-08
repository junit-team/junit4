/**
 * 
 */
package org.junit.internal.runners.links;

import org.junit.internal.runners.model.Roadie;

public abstract class Link {
	public abstract void run(Roadie context) throws Throwable;
}