package org.hamcrest.internal;

import java.util.Iterator;

import org.hamcrest.SelfDescribing;

public class SelfDescribingValueIterator<T> implements Iterator<SelfDescribing> {
    private Iterator<T> values;
    
    public SelfDescribingValueIterator(Iterator<T> values) {
        this.values = values;
    }
    
    public boolean hasNext() {
        return values.hasNext();
    }

    public SelfDescribing next() {
        return new SelfDescribingValue<T>(values.next());
    }

    public void remove() {
        values.remove();
    }
}
