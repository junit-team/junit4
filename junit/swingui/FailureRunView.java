package junit.swingui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import junit.framework.*;
import junit.runner.BaseTestRunner;


/**
 * A view presenting the test failures as a list.
 */
public class FailureRunView implements TestRunView {
	JList fFailureList;
	TestRunContext fRunContext;
	
	/**
	 * Renders TestFailures in a JList
	 */
	static class FailureListCellRenderer extends DefaultListCellRenderer {
		private Icon fFailureIcon;
		private Icon fErrorIcon;
		
		FailureListCellRenderer() {
	    		super();
	    		loadIcons();
		}
	
		void loadIcons() {
			fFailureIcon= TestRunner.getIconResource(getClass(), "icons/failure.gif");
			fErrorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");		
		}
						
		public Component getListCellRendererComponent(
			JList list, Object value, int modelIndex, 
			boolean isSelected, boolean cellHasFocus) {
	
		    Component c= super.getListCellRendererComponent(list, value, modelIndex, isSelected, cellHasFocus);
			TestFailure failure= (TestFailure)value;
			String text= failure.failedTest().toString();
			String msg= failure.exceptionMessage();
			if (msg != null) 
				text+= ":" + BaseTestRunner.truncate(msg); 
	 
			if (failure.isFailure()) { 
				if (fFailureIcon != null)
		    		setIcon(fFailureIcon);
			} else {
		    	if (fErrorIcon != null)
		    		setIcon(fErrorIcon);
		    }
			setText(text);
			setToolTipText(text);
			return c;
		}
	}
	
	public FailureRunView(TestRunContext context) {
		fRunContext= context;
		fFailureList= new JList(fRunContext.getFailures());
		fFailureList.setFont(new Font("Dialog", Font.PLAIN, 12));
 
		fFailureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fFailureList.setCellRenderer(new FailureListCellRenderer());
		fFailureList.setVisibleRowCount(5);

		fFailureList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					testSelected();
				}
			}
		);
	}
	
	public Test getSelectedTest() {
		int index= fFailureList.getSelectedIndex();
		if (index == -1)
			return null;
			
		ListModel model= fFailureList.getModel();
		TestFailure failure= (TestFailure)model.getElementAt(index);
		return failure.failedTest();
	}
	
	public void activate() {
		testSelected();
	}
	
	public void addTab(JTabbedPane pane) {
		JScrollPane scrollPane= new JScrollPane(fFailureList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		Icon errorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");
		pane.addTab("Failures", errorIcon, scrollPane, "The list of failed tests");
	}
		
	public void revealFailure(Test failure) {
		fFailureList.setSelectedIndex(0);
	}
	
	public void aboutToStart(Test suite, TestResult result) {
	}

	public void runFinished(Test suite, TestResult result) {
	}

	protected void testSelected() {
		fRunContext.handleTestSelected(getSelectedTest());
	}
}
