package org.junit.internal.requests;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Sorter;

public class SortingRequest extends Request {
    private final Request request;
    private final Sorter sorter;

    public SortingRequest(Request request, Comparator<Description> comparator) {
       this(request, new Sorter(comparator));
    }
    
    public SortingRequest(Request request, Sorter sorter) {
        this.request = request;
        this.sorter = sorter;
    }

    @Override
    public Runner getRunner() {
        Runner runner = request.getRunner();
        sorter.apply(runner);
        return runner;
    }
}
