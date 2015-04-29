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
package org.junit;

/**
 *
 * @author ruckc
 */
public class Message implements CharSequence {
    private String evaluated;
    private final String format;
    private final Object[] values;
    
    public Message(final String format, final Object ... values) {
        this.format = format;
        this.values = values;
    }
    
    private void evaluate() {
        if(evaluated==null) {
            evaluated = String.format(format, values);
        }
    }

    public int length() {
        evaluate();
        return evaluated.length();
    }

    public char charAt(int index) {
        evaluate();
        return evaluated.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        evaluate();
        return evaluated.subSequence(start, end);
    }
    
    @Override
    public String toString() {
        evaluate();
        return evaluated;
    }
    
    public static Message message(String format, Object ... arguments) {
        return new Message(format, arguments);
    }
    
    public static Message msg(String format, Object ... arguments) {
        return new Message(format, arguments);
    }
}
