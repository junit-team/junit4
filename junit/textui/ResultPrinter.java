
package junit.textui;

import java.io.PrintStream;

public class ResultPrinter {
	PrintStream fWriter;
	int fColumn= 0;
	
	public ResultPrinter(PrintStream writer) {
		fWriter= writer;
	}

	public void testStarted(String testName) {
		getWriter().print(".");
		if (fColumn++ >= 40) {
			getWriter().println();
			fColumn= 0;
		}
	}

	protected PrintStream getWriter() {
		return fWriter;
	}
}
