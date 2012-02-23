package org.mearvk;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Max Rupplin
 * Date: 2/22/12
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyTestClass3
{
    @Test
    public void test1()
    {
        System.err.println("MyTestClass3.test1() called...");
        assertTrue("Oops...", true);
    }

    @Test
    public void test2()
    {
        System.err.println("MyTestClass3.test2() called...");
        assertTrue("Oops...", false);
    }
}
