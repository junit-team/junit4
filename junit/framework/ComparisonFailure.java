package junit.framework;

/**
 * Thrown when an assert equals failed.
 * 
 * Inspired by a patch from Alex Chaffee mailto:alex@purpletech.com
 */
public class ComparisonFailure extends AssertionFailedError {
	private String fExpected;
	private String fActual;

	public ComparisonFailure () {
	}
	
	/**
	 * Constructs a comparison failure.
	 * @param message the identifying message or null
	 * @param expected the expected string value
	 * @param actual the actual string value
	 */
	public ComparisonFailure (String message, String expected, String actual) {
		super (message);
		fExpected= expected;
		fActual= actual;
	}
	
	/**
	 * Returns ... in place of common prefix and ... in
	 * place of common suffix.
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		String formatted= "";
		String message= super.getMessage();
		
		int end= Math.min(fExpected.length(), fActual.length());
		
		int i= 0;
		for(; i < end; i++) {
			if (fExpected.charAt(i) != fActual.charAt(i))
				break;
		}
		int j= fExpected.length()-1;
		int k= fActual.length()-1;
		for (; k >= 0 && j >= 0; k--,j--) {
			if (fExpected.charAt(j) != fActual.charAt(k))
				break;
		}
		String actual, expected;
		
		// equal strings
		if (j == -1 && k == -1) {
			expected= fExpected;
			actual= fActual;
		} 
		// handle overlapping matches
		else if (j+1 < i || k+1 < i) {
			expected= "..."+fExpected.substring(i, j+2)+"...";
			actual= "..."+fActual.substring(i, k+2)+"...";
		} 
		// otherwise replace common prefix and suffix
		else {
			expected= fExpected.substring(i, j+1);
			actual= fActual.substring(i, k+1);
			if (i <= end && i > 0) {
				expected= "..."+expected;
				actual= "..."+actual;
			}
			
			if (j < fExpected.length()-1)
				expected= expected+"...";
			if (k < fActual.length()-1)
				actual= actual+"...";
		}	
		if (message != null)
			formatted= message+": ";
				
		return formatted+"expected:<"+expected+"> but was:<"+actual+">";
	}

}