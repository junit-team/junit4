package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.plugin.Plug;
import org.junit.plugin.Plugin;

@Plug({ PluginTest.Plugin1.class, PluginTest.Plugin2.class })
public class PluginTest {

    private List<Class<? extends Plugin>> invokedPlugins = new ArrayList<Class<? extends Plugin>>();

    public static class Plugin1 implements Plugin {

        public void prepareTest(Object testInstance, Method testMethod) {

            ((PluginTest) testInstance).invokedPlugins.add(getClass());
        }
    }

    public static class Plugin2 implements Plugin {

        public void prepareTest(Object testInstance, Method testMethod) {

            ((PluginTest) testInstance).invokedPlugins.add(getClass());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void success() {

        assertThat(invokedPlugins,
                is(Arrays.asList(Plugin1.class, Plugin2.class)));
    }
}
