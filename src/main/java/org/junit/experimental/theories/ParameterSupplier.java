package org.junit.experimental.theories;

import java.util.List;

/**
 * Abstract parent class for suppliers of input data points for theories. Extend this class to customize how {@link
 * org.junit.experimental.theories.Theories Theories} runner
 * finds accepted data points. Then use your class together with <b>&#064;ParametersSuppliedBy</b> on input
 * parameters for theories.
 *
 * <p>
 * For example, here is a supplier for values between two integers, and an annotation that references it:
 *
 * <pre>
 *     &#064;Retention(RetentionPolicy.RUNTIME)
 *     <b>&#064;ParametersSuppliedBy</b>(BetweenSupplier.class)
 *     public @interface Between {
 *         int first();
 *
 *         int last();
 *     }
 *
 *     public static class BetweenSupplier extends <b>ParameterSupplier</b> {
 *         &#064;Override
 *         public List&lt;<b>PotentialAssignment</b>&gt; getValueSources(<b>ParameterSignature</b> sig) {
 *             List&lt;<b>PotentialAssignment</b>&gt; list = new ArrayList&lt;PotentialAssignment&gt;();
 *             Between annotation = (Between) sig.getSupplierAnnotation();
 *
 *             for (int i = annotation.first(); i &lt;= annotation.last(); i++)
 *                 list.add(<b>PotentialAssignment</b>.forValue("ints", i));
 *             return list;
 *         }
 *     }
 * </pre>
 * </p>
 *
 * @see org.junit.experimental.theories.ParametersSuppliedBy
 * @see org.junit.experimental.theories.Theories
 * @see org.junit.experimental.theories.FromDataPoints
 */
public abstract class ParameterSupplier {
    public abstract List<PotentialAssignment> getValueSources(ParameterSignature sig) throws Throwable;
}
