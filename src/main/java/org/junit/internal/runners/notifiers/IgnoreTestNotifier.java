/**
 * 
 */
package org.junit.internal.runners.notifiers;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runners.Notifier;

public class IgnoreTestNotifier extends Notifier {
	@Override
	public void run(EachTestNotifier context) {
		context.fireTestIgnored();
	}
}