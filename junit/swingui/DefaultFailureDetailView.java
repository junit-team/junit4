package junit.swingui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import junit.framework.TestFailure;
import junit.runner.*;

/**
 * A view that shows a stack trace of a failure
 */
public class DefaultFailureDetailView implements FailureDetailView {
	JList fList;  
	
	/**
	 * A ListModel representing the scanned failure stack trace.
	 */
	static class StackTraceListModel extends AbstractListModel {
		private Vector fLines= new Vector(20);
		
		public Object getElementAt(int index) {
			return fLines.elementAt(index);
		}

		public int getSize() {
			return fLines.size();
		}
		
		public void setTrace(String trace) {
			scan(trace);
			fireContentsChanged(this, 0, fLines.size());
		}
		
		public void clear() {
			fLines.removeAllElements();
			fireContentsChanged(this, 0, fLines.size());
		}
		
		private void scan(String trace) {
			fLines.removeAllElements();
     		StringTokenizer st= new StringTokenizer(trace, "\n\r", false);
	    	while (st.hasMoreTokens()) 
 				fLines.add(st.nextToken());
		}
	}
	
	/**
	 * Renderer for stack entries
	 */
	static class StackEntryRenderer extends DefaultListCellRenderer {
						
		public Component getListCellRendererComponent(
				JList list, Object value, int modelIndex, 
				boolean isSelected, boolean cellHasFocus) {
			String text= ((String)value).replace('\t', ' ');
		    Component c= super.getListCellRendererComponent(list, text, modelIndex, isSelected, cellHasFocus);
			setText(text);
			setToolTipText(text);
			return c;
		}
	}
	
	/**
	 * Returns the component used to present the trace
	 */
	public Component getComponent() {
		if (fList == null) {
			fList= new JList(new StackTraceListModel());
			fList.setFont(new Font("Dialog", Font.PLAIN, 12));
			fList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fList.setVisibleRowCount(5);
			fList.setCellRenderer(new StackEntryRenderer());
		}
		return fList;
	}
	
	/**
	 * Shows a TestFailure
	 */
	public void showFailure(TestFailure failure) {
		getModel().setTrace(BaseTestRunner.getFilteredTrace(failure.trace()));
	}
	/**
	 * Clears the output.
	 */
	public void clear() {
		getModel().clear();
	}
	
	private StackTraceListModel getModel() {
		return 	(StackTraceListModel)fList.getModel();
	}
}