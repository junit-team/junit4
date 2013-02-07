package org.junit.experimental.annotations;

import java.lang.annotation.*;

/**
 * Implementation is guaranteed to be free of race conditions
 * when accessed by multiple threads simultaneously.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
}
