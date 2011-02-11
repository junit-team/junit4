package org.junit.internal;

import org.junit.Ignore;
import org.junit.RuntimeCondition;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author jerome@coffeebreaks.org
 * @since 2/11/11 2:18 PM
 */
public class IgnoreUtil {

  public static boolean isIgnored(FrameworkMethod method) {
    Ignore annotation = method.getAnnotation(Ignore.class);
    return annotation != null && isTrue(annotation.ifTrue(), method);
  }

  private static boolean isTrue(Class<? extends RuntimeCondition>[] conditions, FrameworkMethod method) {
    if (conditions == null || conditions.length == 0) {
      return true;
    }
    for(Class<? extends RuntimeCondition> condition : conditions){
      try{
        if (! condition.newInstance().isTrue(method)) {
          return false;
        }
      } catch(Exception e){
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  public static boolean isIgnored(Description description) {
    Ignore annotation = description.getAnnotation(Ignore.class);
    return annotation != null && isTrue(annotation.ifTrue(), description);
  }

  private static boolean isTrue(Class<? extends RuntimeCondition>[] conditions, Description description) {
    if (conditions == null || conditions.length == 0) {
      return true;
    }
    for(Class<? extends RuntimeCondition> condition : conditions){
      try{
        if (! condition.newInstance().isTrue(description)) {
          return false;
        }
      } catch(Exception e){
        throw new RuntimeException(e);
      }
    }
    return true;
  }

}
