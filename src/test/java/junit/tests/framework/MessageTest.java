/*
 * Copyright (c) 2015 JUnit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    JUnit - initial API and implementation and/or initial documentation
 */
package junit.tests.framework;

import junit.framework.TestCase;
import org.junit.Assert;
import static org.junit.Message.message;

public class MessageTest extends TestCase {
    public void testMessageFormatSimple() {
        assertEquals("a 1 c", message("%s %d %s", "a", 1, 'c').toString());
    }
    
    public void testMessageFormatProcess() {
        try {
            Assert.assertEquals(message("z %s %d", "a", 1), "a", "b");
        } catch (AssertionError e) {
            assertTrue(e.getMessage()+" does not start with 'z a 1'", e.getMessage().startsWith("z a 1"));
        }
    }
}
