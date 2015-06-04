package org.junit.tests.experimental.rules;

import org.junit.Assert;
import org.junit.Repeat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ParallelRepeatRule;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepeatRuleTest {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Random R = new Random();

    @Rule public ParallelRepeatRule parallelRepeatRule = new ParallelRepeatRule(executorService);

    @Test
    @Repeat(1000)
    public void asdf() {
        byte[] buf = new byte[1048576];
        R.nextBytes(buf);
    }

    @Test
    @Repeat(10)
    public void asdf2() {
        Assert.assertTrue(false);
    }
}
