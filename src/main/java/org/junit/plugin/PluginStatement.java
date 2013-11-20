package org.junit.plugin;

import java.lang.reflect.Method;

import org.junit.runners.model.Statement;

public class PluginStatement extends Statement {

    private final Plugin plugin;

    private final Object testInstance;

    private final Method testMethod;

    private final Statement nextStatement;

    public PluginStatement(Plugin plugin, Object testInstance,
            Method testMethod, Statement nextStatement) {
        this.plugin = plugin;
        this.testInstance = testInstance;
        this.testMethod = testMethod;
        this.nextStatement = nextStatement;
    }

    @Override
    public void evaluate() throws Throwable {

        plugin.prepareTest(testInstance, testMethod);
        nextStatement.evaluate();
    }
}
