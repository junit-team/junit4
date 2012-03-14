package junit.framework;

/**
 * A Listener for test progress
 */
public interface TestListener {
	/**
 	 * An error occurred.
 	 */
	public void addError(Test test, Throwable t);
	/**
 	 * A failure occurred.
 	 */
 	public void addFailure(Test test, AssertionFailedError t);  
	/**
	 * A failure occurred due required conditions were not met.
	 */
	public void addBlocked(Test test, BlockedException t);
	/**
	 * A test ended.
	 */
 	public void endTest(Test test); 
	/**
	 * A test started.
	 */
	public void startTest(Test test);
}