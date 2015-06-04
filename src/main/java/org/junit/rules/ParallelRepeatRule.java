package org.junit.rules;

import org.junit.Repeat;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ParallelRepeatRule implements TestRule {
    private final ExecutorService executorService;

    public ParallelRepeatRule(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Statement apply(final Statement base, Description description) {
        Repeat annotation = description.getAnnotation(Repeat.class);
        final int iterations = annotation == null ? 1 : annotation.value();
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                List<Future<?>> futures = new ArrayList<Future<?>>();
                for (int i = 0; i < iterations; i++) {
                    futures.add(executorService.submit(new Callable<Void>() {
                        public Void call() throws Exception {
                            try {
                                base.evaluate();
                                return null;
                            } catch (Exception ex) {
                                throw ex;
                            } catch (Throwable t) {
                                throw new RuntimeException(t);
                            }
                        }
                    }));
                }
                List<Throwable> throwables = new ArrayList<Throwable>();
                boolean interrupted = false;
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (ExecutionException ex) {
                        throwables.add(ex.getCause());
                    } catch (InterruptedException ex) {
                        interrupted = true;
                    }
                }
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                if (!throwables.isEmpty()) {
                    throw new MultipleFailureException(throwables);
                }
            }
        };
    }
}
