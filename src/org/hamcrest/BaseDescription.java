package org.hamcrest;

import static java.lang.String.valueOf;

import java.util.Arrays;
import java.util.Iterator;

import org.hamcrest.internal.ArrayIterator;
import org.hamcrest.internal.SelfDescribingValueIterator;

/**
 * A {@link Description} that is stored as a string.
 */
public abstract class BaseDescription implements Description {
    public Description appendText(String text) {
        append(text);
        return this;
    }
    
    public Description appendDescriptionOf(SelfDescribing value) {
    	value.describeTo(this);
    	return this;
    }
    
    public Description appendValue(Object value) {
        if (value == null) {
            append("null");
        } else if (value instanceof String) {
            toJavaSyntax((String) value);
        } else if (value instanceof Character) {
            append('"');
            toJavaSyntax((Character) value);
            append('"');
        } else if (value instanceof Short) {
            append('<');
            append(valueOf(value));
            append("s>");
        } else if (value instanceof Long) {
            append('<');
            append(valueOf(value));
            append("L>");
        } else if (value instanceof Float) {
            append('<');
            append(valueOf(value));
            append("F>");
        } else if (value.getClass().isArray()) {
        	appendValueList("[",", ","]", new ArrayIterator(value));
        } else {
            append('<');
            append(valueOf(value));
            append('>');
        }
        return this;
    }
    
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return appendValueList(start, separator, end, Arrays.asList(values));
	}
    
	public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
		return appendValueList(start, separator, end, values.iterator());
	}
	
	private <T> Description appendValueList(String start, String separator, String end, Iterator<T> values) {
		return appendList(start, separator, end, new SelfDescribingValueIterator<T>(values));
	}
	
    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return appendList(start, separator, end, values.iterator());
    }

    private Description appendList(String start, String separator, String end, Iterator<? extends SelfDescribing> i) {
        boolean separate = false;
        
        append(start);
        while (i.hasNext()) {
            if (separate) append(separator);
            appendDescriptionOf(i.next());
            separate = true;
        }
        append(end);
        
        return this;
    }


    /** Append the String <var>str</var> to the description.  
     *  The default implementation passes every character to {@link #append(char)}.  
     *  Override in subclasses to provide an efficient implementation.
     */
    protected void append(String str) {
    	for (int i = 0; i < str.length(); i++) {
    		append(str.charAt(i));
    	}
    }
    
    /** Append the char <var>c</var> to the description.  
     */
    protected abstract void append(char c);

    private void toJavaSyntax(String unformatted) {
        append('"');
        for (int i = 0; i < unformatted.length(); i++) {
            toJavaSyntax(unformatted.charAt(i));
        }
        append('"');
    }

    private void toJavaSyntax(char ch) {
        switch (ch) {
            case '"':
                append("\\\"");
                break;
            case '\n':
                append("\\n");
                break;
            case '\r':
                append("\\r");
                break;
            case '\t':
                append("\\t");
                break;
            default:
                append(ch);
        }
    }
}
