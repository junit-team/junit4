package junit.swingui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A tree model for a Test.
 */
class TestTreeModel implements TreeModel {
	private Test fRoot;
	private Vector fModelListeners= new Vector();
	private Hashtable fFailures= new Hashtable();
	private Hashtable fErrors= new Hashtable();
	private Hashtable fRunTests= new Hashtable();
	
	/**
	 * Constructs a tree model with the given test as its root.
	 */
	public TestTreeModel(Test root) {
		super();
		fRoot= root;
	}
	
	/**
	 * adds a TreeModelListener
	 */
	public void addTreeModelListener(TreeModelListener l) {
		if (!fModelListeners.contains(l))
			fModelListeners.addElement(l);
	}
	/**
	 * Removes a TestModelListener
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		fModelListeners.removeElement(l);
	}
	/**
	 * Finds the path to a test. Returns the index of the test in its
	 * parent test suite.
	 */
	public int findTest(Test target, Test node, Vector path) {
		if (target.equals(node)) 
			return 0;
			
		TestSuite suite= isTestSuite(node);
		for (int i= 0; i < getChildCount(node); i++) {
			Test t= suite.testAt(i); 
			int index= findTest(target, t, path);
			if (index >= 0) {
				path.insertElementAt(node, 0);
				if (path.size() == 1)
					return i;
				return index;
			}
		}
		return -1;
	}
	/**
	 * Fires a node changed event
	 */
	public void fireNodeChanged(TreePath path, int index) {
		int[] indices= {index};
		Object[] changedChildren= {getChild(path.getLastPathComponent(), index)};
		TreeModelEvent event= new TreeModelEvent(this, path, indices, changedChildren);
		
		Enumeration e= fModelListeners.elements();
		while (e.hasMoreElements()) { 
			TreeModelListener l= (TreeModelListener) e.nextElement();
			l.treeNodesChanged(event);
		}	
	}
	/**
	 * Gets the test at the given index
	 */
	public Object getChild(Object parent, int index) {
		TestSuite suite= isTestSuite(parent);
		if (suite != null) 
			return suite.testAt(index);
		return null; 
	}
	/**
	 * Gets the number of tests.
	 */
	public int getChildCount(Object parent) {
		TestSuite suite= isTestSuite(parent);
		if (suite != null) 
			return suite.testCount();
		return 0;
	}
	/**
	 * Gets the index of a test in a test suite
	 */
	public int getIndexOfChild(Object parent, Object child) {
		TestSuite suite= isTestSuite(parent);
		if (suite != null) {
			int i= 0;
			for (Enumeration e= suite.tests(); e.hasMoreElements(); i++) {
				if (child.equals(e.nextElement()))
					return i;
			}
		}
		return -1; 
	}
	/**
	 * Returns the root of the tree
	 */
	public Object getRoot() {
		return fRoot;
	}
	/**
	 * Tests if the test is a leaf.
	 */
	public boolean isLeaf(Object node) {
		return isTestSuite(node) == null;
	}
	/**
	 * Tests if the node is a TestSuite.
	 */
	TestSuite isTestSuite(Object node) {
		if (node instanceof TestSuite) 
			return (TestSuite)node;
		if (node instanceof TestDecorator) { 
			Test baseTest= ((TestDecorator)node).getTest(); 
			return isTestSuite(baseTest); 
		} 
		return null;
	}
	
	/**
	 * Called when the value of the model object was changed in the view
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// we don't support direct editing of the model
		System.out.println("TreeModel.valueForPathChanged: not implemented");
	}
	/**
	 * Remembers a test failure
	 */
	void addFailure(Test t) {
		fFailures.put(t, t);
	}
	/**
	 * Remembers a test error
	 */
	void addError(Test t) {
		fErrors.put(t, t);
	}
	/**
	 * Remembers that a test was run
	 */
	void addRunTest(Test t) {
		fRunTests.put(t, t);
	}
	/**
	 * Returns whether a test was run
	 */
	boolean wasRun(Test t) {
		return fRunTests.get(t) != null;
	}
	/**
	 * Tests whether a test was an error
	 */
	boolean isError(Test t) {
		return (fErrors != null) && fErrors.get(t) != null;
	}
	/**
	 * Tests whether a test was a failure
	 */
	boolean isFailure(Test t) {
		return (fFailures != null) && fFailures.get(t) != null;
	}
	/**
	 * Resets the test results
	 */
	void resetResults() {
		fFailures= new Hashtable();
		fRunTests= new Hashtable();
		fErrors= new Hashtable();
	}
}