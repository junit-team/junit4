package org.junit.tests.experimental.parallel;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.util.concurrent.CountDownLatch;

/**
 * @author Kristian Rosenvold
 */
public class ParallelRunNotifierTest
{

    @Test(timeout = 250)
    public void shouldNotBlockExcessively()
        throws InterruptedException
    {
        final RunNotifier runNotifier = new RunNotifier();
        BlockingListener listener = new BlockingListener();
        runNotifier.addListener( listener );

        startAThread( runNotifier, Description.createTestDescription( ParallelRunNotifierTest.class, "thread1" ) );
        startAThread( runNotifier, Description.createTestDescription( ParallelRunNotifierTest.class, "thread2" ) );
        startAThread( runNotifier, Description.createTestDescription( ParallelRunNotifierTest.class, "thread3" ) );

        listener.counter.await();

    }

    private void startAThread( final RunNotifier runNotifier, final Description description )
    {
        new Thread( ){
            @Override
            public void run()
            {
                runNotifier.fireTestStarted( description );
            }
        }.start();
    }

    class BlockingListener extends RunListener{
        public CountDownLatch counter = new CountDownLatch( 3  );
        @Override
        public void testStarted( Description description )
            throws Exception
        {
            Thread.sleep( 100 );
            counter.countDown();
        }
    }
}
