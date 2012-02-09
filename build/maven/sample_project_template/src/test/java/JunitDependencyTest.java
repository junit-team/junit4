import org.hamcrest.Matchers;
import org.junit.Test;

public final class JunitDependencyTest {
	/**
	 * JUnit dependency test.
	 * 
	 * This class has three dependencies. These can be on the classpath in
	 * different orders. Of the two orderings below, the first one will cause a
	 * NoSuchMethodError, while the second one allows the test to pass
	 * successfully. See the explanation below for more information.
	 * 
	 * Ordering 1: junit-4.9, hamcrest-core-1.2.1, hamcrest-library-1.2.1.
	 * Ordering 2: hamcrest-core-1.2.1, junit-4.9, hamcrest-library-1.2.1.
	 */
	@Test
	public void test() {
		/*
		 * Note that we call Matchers#anyOf(Matcher<T>, Matcher<? super T>).
		 * This method is provided by hamcrest-library-1.2.1. Said module is
		 * compiled against hamcrest-core-1.2.1. Matchers#anyOf calls
		 * AnyOf#anyOf(Matcher<T>, Matcher<? super T>). The latter method is
		 * provided by hamcrest-core-1.2.1, but *not* by hamcrest-core-1.1.
		 * 
		 * However, hamcrest-core-1.1 *does* contain a class called AnyOf. Now,
		 * since junit-4.9 incorporates hamcrest-core-1.1 we must make sure that
		 * hamcrest-core-1.2.1 is placed *before* junit-4.9 on the classpath.
		 * Failure to do so will cause the wrong AnyOf class to be used. The
		 * result is a NoSuchMethodError.
		 */
		Matchers.anyOf(Matchers.nullValue(), Matchers.notNullValue());
	}
}
