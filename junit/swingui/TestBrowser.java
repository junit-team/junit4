package junit.swingui;

import java.io.*;
import java.net.URL;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.event.*;

import junit.framework.*;
import junit.runner.*;

/**
 * A tree browser for a test suite.
 */
class TestBrowser extends JFrame implements TestListener {
	private JButton fClose;
	private JButton fRun;
	private JButton fReload;
	private JTree fTree;
	private TestTreeModel fModel;
	private TestRunner fRunner;

	static class TestTreeCellRenderer extends DefaultTreeCellRenderer {
		private ImageIcon fErrorIcon;
		private ImageIcon fOkIcon;
		private ImageIcon fFailureIcon;
		
		TestTreeCellRenderer() {
	    	super();
	    	URL url= getClass().getResource("error.gif");
	    	fErrorIcon= new ImageIcon(url);
	    	url= getClass().getResource("ok.gif");
	    	fOkIcon= new ImageIcon(url);
	    	url= getClass().getResource("failure.gif");
	    	fFailureIcon= new ImageIcon(url);
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			
			Component c= super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	  		TreeModel model= tree.getModel();
	  		if (model instanceof TestTreeModel) {	
				TestTreeModel testModel= (TestTreeModel)model;
				Test t= (Test)value;
				String s= "";
	    		if (testModel.isFailure(t)) {
	    			setIcon(fFailureIcon);
	    			s= " - Failed";
	    		}
	    		else if (testModel.isError(t)) {
	    			setIcon(fErrorIcon);
	    			s= " - Error";
	    		}
	    		else if (testModel.wasRun(t)) {
	    			setIcon(fOkIcon);
	    			s= " - Passed";
	    		}
	    		if (c instanceof JComponent)
	    			((JComponent)c).setToolTipText(getText()+s);
	  		}
			return c;
		}
	}
	
	public TestBrowser(TestRunner runner) {
		fRunner= runner;
		getContentPane().setLayout(new GridBagLayout());
		setBackground(SystemColor.control);
		setSize(400, 300);
		setTitle("Test Browser");

		fTree= new JTree();
		fTree.setRowHeight(20);
		ToolTipManager.sharedInstance().registerComponent(fTree);	
		fTree.putClientProperty("JTree.lineStyle", "Angled");
		JScrollPane scrolledTree= new JScrollPane(fTree);

		fClose= new JButton("Close");
		fClose.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);

		fRun= new JButton("Run");
		fRun.setEnabled(false);
		fRun.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSelection();
				}
			}
		);
		fReload= new JButton("Reload");
		fReload.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reloadTestTree();
				}
			}
		);
	
		fTree.addTreeSelectionListener(
			new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					checkEnableRun(e);
				}
			}
		);
		
		GridBagConstraints treeConstraints = new GridBagConstraints();
		treeConstraints.gridx= 0; treeConstraints.gridy= 0;
		treeConstraints.gridwidth= 3; treeConstraints.gridheight= 1;
		treeConstraints.fill= GridBagConstraints.BOTH;
		treeConstraints.anchor= GridBagConstraints.CENTER;
		treeConstraints.weightx= 1.0;
		treeConstraints.weighty= 1.0;
		treeConstraints.insets= new Insets(8, 8, 8, 8);
		getContentPane().add(scrolledTree, treeConstraints);

		GridBagConstraints runConstraints = new GridBagConstraints();
		runConstraints.gridx= 0; runConstraints.gridy= 1;
		runConstraints.gridwidth= 1; runConstraints.gridheight= 1;
		runConstraints.anchor= java.awt.GridBagConstraints.EAST;
		runConstraints.weightx= 0.0;
		runConstraints.weighty= 0.0;
		runConstraints.insets= new Insets(0, 8, 8, 8);
		getContentPane().add(fRun, runConstraints);

		GridBagConstraints reloadConstraints = new GridBagConstraints();
		reloadConstraints.gridx= 1; reloadConstraints.gridy= 1;
		reloadConstraints.gridwidth= 1; reloadConstraints.gridheight= 1;
		reloadConstraints.anchor= java.awt.GridBagConstraints.EAST;
		reloadConstraints.weightx= 0.0;
		reloadConstraints.weighty= 0.0;
		reloadConstraints.insets= new Insets(0, 8, 8, 8);
		getContentPane().add(fReload, reloadConstraints);

		GridBagConstraints closeConstraints= new GridBagConstraints();
		closeConstraints.gridx= 2; closeConstraints.gridy= 1;
		closeConstraints.gridwidth= 1; closeConstraints.gridheight= 1;
		closeConstraints.anchor= java.awt.GridBagConstraints.EAST;
		closeConstraints.weightx= 0.0;
		closeConstraints.weighty= 0.0;
		closeConstraints.insets= new Insets(0, 8, 8, 8);
		getContentPane().add(fClose, closeConstraints);
		
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	}
	
	public void addError(final Test test, final Throwable t) {
 		fModel.addError(test);
		fireTestChanged(test, true);
	}
	
	public void addFailure(final Test test, final Throwable t) {
 		fModel.addFailure(test);
		fireTestChanged(test, true);
	}
	
	/**
 	 * A test ended.
 	 */
 	public void endTest(Test test) {
		fModel.addRunTest(test);
	 	fireTestChanged(test, false);
 	}

   /**
 	* A test started.
 	*/
 	public void startTest(Test test) {
 	} 	  
	
	/**
	 * Checks whether the run button should be enabled
	 */
	public void checkEnableRun(TreeSelectionEvent e) {
		// TODO should support a multiple selection
		fRun.setEnabled(fTree.getSelectionCount() == 1);
	}
    	  
	/**
	 * Returns the TestListener for the TestTreeFrame
	 */
	public TestListener getTestListener() {
		return this;
	}
	
	/**
	 * Runs the selected test
	 */
	public void runSelection() {
		fModel.resetResults();
		TreePath[] paths= fTree.getSelectionPaths();
		for (int i= 0; i < paths.length; i++) {
			TreePath path= paths[i];
			fRunner.runTest((Test)path.getLastPathComponent());
		}
	}
	
	/**
	 * Reloads test tree 
	 */
	public void reloadTestTree() {
		Test t= fRunner.getTest(fRunner.getSuiteText());
		showTestTree(t);
	}

	/**
	 * Shows the test hierarchy starting at the given test
	 */
	public void showTestTree(Test root) {
		fModel= new TestTreeModel(root);
		fTree.setModel(fModel);
		fTree.setCellRenderer(new TestTreeCellRenderer());
	}
	
	private void fireTestChanged(final Test test, final boolean expand) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					Vector vpath= new Vector();
					int index= fModel.findTest(test, (Test)fModel.getRoot(), vpath);
					if (index >= 0) {
						Object[] path= new Object[vpath.size()];
						vpath.copyInto(path);
						TreePath treePath= new TreePath(path);
						fModel.fireNodeChanged(treePath, index);
						if (expand) {
							Object child= fModel.getChild(treePath.getLastPathComponent(), index);
							Object[] fullPath= new Object[vpath.size()+1];
							vpath.copyInto(fullPath);
							fullPath[vpath.size()]= child;
							TreePath fullTreePath= new TreePath(fullPath);
							fTree.scrollPathToVisible(fullTreePath);
						}
					}
				}
			}
		);
	}
}