package junit.swingui;

import java.awt.Component;
import javax.swing.JTextArea;
import java.io.*;

import junit.runner.*;
import junit.framework.*;

/**
 * A view that shows a stack trace of a failure
 */
class DefaultFailureDetailView implements FailureDetailView {
	JTextArea fTextArea;
	
	/**
	 * Returns the component used to present the trace
	 */
	public Component getComponent() {
		if (fTextArea == null) {
			fTextArea= new JTextArea();
			fTextArea.setRows(5);
			fTextArea.setTabSize(0);
			fTextArea.setEditable(false);
		}
		return fTextArea;
	}
	
	/**
	 * Shows a TestFailure
	 */
	public void showFailure(TestFailure failure) {
		fTextArea.setText(BaseTestRunner.getFilteredTrace(failure.thrownException()));
		fTextArea.select(0, 0);	
	}
	
	public void clear() {
		fTextArea.setText("");
	}
}