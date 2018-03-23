package org.junit.runner.manipulation;

import java.util.List;

public interface GlobalRuleRunnable {

    void setGlobalRules(List<Class<?>> rules);

}
