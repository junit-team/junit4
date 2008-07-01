package org.junit.runners;

import org.junit.internal.runners.model.EachTestNotifier;

public abstract class Notifier {

	public abstract void run(EachTestNotifier context);

}
