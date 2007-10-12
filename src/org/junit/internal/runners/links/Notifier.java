package org.junit.internal.runners.links;

import org.junit.internal.runners.model.EachTestNotifier;

public abstract class Notifier {

	public abstract void run(EachTestNotifier context) throws Throwable;

}
