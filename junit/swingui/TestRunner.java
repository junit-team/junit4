package junit.swingui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import junit.framework.*;
import junit.runner.*;

/**
 * A Swing based user interface to run tests.
 * Enter the name of a class which either provides a static
 * suite method or is a subclass of TestCase.
 * <pre>
 * Synopsis: java junit.swingui.TestRunner [-noloading] [TestCase]
 * </pre>
 * TestRunner takes as an optional argument the name of the testcase class to be run.
 */
public class TestRunner extends BaseTestRunner implements TestRunContext {
	private static final int GAP= 4;
	private static final int HISTORY_LENGTH= 5;

	protected JFrame fFrame;
	private Thread fRunner;
	private TestResult fTestResult;

	private JComboBox fSuiteCombo;
	private ProgressBar fProgressIndicator;
	private DefaultListModel fFailures;
	private JLabel fLogo;
	private CounterPanel fCounterPanel;
	private JButton fRun;
	private JButton fQuitButton;
	private JButton fRerunButton;
	private StatusLine fStatusLine;
	private FailureDetailView fFailureView;
	private JTabbedPane fTestViewTab;
	private JCheckBox fUseLoadingRunner;
	private Vector fTestRunViews= new Vector(); // view associated with tab in tabbed pane

	private static final String TESTCOLLECTOR_KEY= "TestCollectorClass";
	private static final String FAILUREDETAILVIEW_KEY= "FailureViewClass";

	public TestRunner() {
	}

	public static void main(String[] args) {
		new TestRunner().start(args);
	}

	public static void run(Class test) {
		String args[]= { test.getName() };
		main(args);
	}

	public void testFailed(final int status, final Test test, final Throwable t) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					switch (status) {
						case TestRunListener.STATUS_ERROR:
							fCounterPanel.setErrorValue(fTestResult.errorCount());
							appendFailure(test, t);
							break;
						case TestRunListener.STATUS_FAILURE:
							fCounterPanel.setFailureValue(fTestResult.failureCount());
							appendFailure(test, t);
							break;
					}
				}
			}
		);
	}

	public void testStarted(String testName) {
		postInfo("Running: "+testName);
	}

	public void testEnded(String stringName) {
		synchUI();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					if (fTestResult != null) {
						fCounterPanel.setRunValue(fTestResult.runCount());
						fProgressIndicator.step(fTestResult.runCount(), fTestResult.wasSuccessful());
					}
				}
			}
		);
	}

	public void setSuite(String suiteName) {
		fSuiteCombo.getEditor().setItem(suiteName);
	}

	private void addToHistory(final String suite) {
		for (int i= 0; i < fSuiteCombo.getItemCount(); i++) {
			if (suite.equals(fSuiteCombo.getItemAt(i))) {
				fSuiteCombo.removeItemAt(i);
				fSuiteCombo.insertItemAt(suite, 0);
				fSuiteCombo.setSelectedIndex(0);
				return;
			}
		}
		fSuiteCombo.insertItemAt(suite, 0);
		fSuiteCombo.setSelectedIndex(0);
		pruneHistory();
	}

	private void pruneHistory() {
		int historyLength= getPreference("maxhistory", HISTORY_LENGTH);
		if (historyLength < 1)
			historyLength= 1;
		for (int i= fSuiteCombo.getItemCount()-1; i > historyLength-1; i--)
			fSuiteCombo.removeItemAt(i);
	}

	private void appendFailure(Test test, Throwable t) {
		fFailures.addElement(new TestFailure(test, t));
		if (fFailures.size() == 1)
			revealFailure(test);
	}

	private void revealFailure(Test test) {
		for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
			TestRunView v= (TestRunView) e.nextElement();
			v.revealFailure(test);
		}
	}

	protected void aboutToStart(final Test testSuite) {
		for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
			TestRunView v= (TestRunView) e.nextElement();
			v.aboutToStart(testSuite, fTestResult);
		}
	}

	protected void runFinished(final Test testSuite) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
						TestRunView v= (TestRunView) e.nextElement();
						v.runFinished(testSuite, fTestResult);
					}
				}
			}
		);
	}

	protected CounterPanel createCounterPanel() {
		return new CounterPanel();
	}

	protected JPanel createFailedPanel() {
		JPanel failedPanel= new JPanel(new GridLayout(0, 1, 0, 2));
		fRerunButton= new JButton("Run");
		fRerunButton.setEnabled(false);
		fRerunButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rerun();
				}
			}
		);
		failedPanel.add(fRerunButton);
		return failedPanel;
	}

	protected FailureDetailView createFailureDetailView() {
		String className= BaseTestRunner.getPreference(FAILUREDETAILVIEW_KEY);
		if (className != null) {
			Class viewClass= null;
			try {
				viewClass= Class.forName(className);
				return (FailureDetailView)viewClass.newInstance();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(fFrame, "Could not create Failure DetailView - using default view");
			}
		}
		return new DefaultFailureDetailView();
	}

	/**
	 * Creates the JUnit menu. Clients override this
	 * method to add additional menu items.
	 */
	protected JMenu createJUnitMenu() {
		JMenu menu= new JMenu("JUnit");
		menu.setMnemonic('J');
		JMenuItem mi1= new JMenuItem("About...");
		mi1.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            about();
		        }
		    }
		);
		mi1.setMnemonic('A');
		menu.add(mi1);

		menu.addSeparator();
		JMenuItem mi2= new JMenuItem(" Exit ");
		mi2.addActionListener(
		    new ActionListener() {
		        public void actionPerformed(ActionEvent event) {
		            terminate();
		        }
		    }
		);
		mi2.setMnemonic('x');
		menu.add(mi2);

		return menu;
	}

	protected JFrame createFrame() {
		JFrame frame= new JFrame("JUnit");
		Image icon= loadFrameIcon();
		if (icon != null)
			frame.setIconImage(icon);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					terminate();
				}
			}
		);
		return frame;
	}

	protected JLabel createLogo() {
		JLabel label;
		Icon icon= getIconResource(BaseTestRunner.class, "logo.gif");
		if (icon != null)
			label= new JLabel(icon);
		else
			label= new JLabel("JV");
		label.setToolTipText("JUnit Version "+Version.id());
		return label;
	}

	protected void createMenus(JMenuBar mb) {
		mb.add(createJUnitMenu());
	}

	protected JCheckBox createUseLoaderCheckBox() {
		boolean useLoader= useReloadingTestSuiteLoader();
		JCheckBox box= new JCheckBox("Reload classes every run", useLoader);
		box.setToolTipText("Use a custom class loader to reload the classes for every run");
		if (inVAJava())
			box.setVisible(false);
		return box;
	}

	protected JButton createQuitButton() {
		 // spaces required to avoid layout flicker
		 // Exit is shorter than Stop that shows in the same column
		JButton quit= new JButton(" Exit ");
		quit.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					terminate();
				}
			}
		);
		return quit;
	}

	protected JButton createRunButton() {
		JButton run= new JButton("Run");
		run.setEnabled(true);
		run.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					runSuite();
				}
			}
		);
		return run;
	}

	protected Component createBrowseButton() {
		JButton browse= new JButton("...");
		browse.setToolTipText("Select a Test class");
		browse.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					browseTestClasses();
				}
			}
		);
		return browse;
	}

	protected StatusLine createStatusLine() {
		return new StatusLine(380);
	}

	protected JComboBox createSuiteCombo() {
		JComboBox combo= new JComboBox();
		combo.setEditable(true);
		combo.setLightWeightPopupEnabled(false);

		combo.getEditor().getEditorComponent().addKeyListener(
			new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					textChanged();
					if (e.getKeyChar() == KeyEvent.VK_ENTER)
						runSuite();
				}
			}
		);
		try {
			loadHistory(combo);
		} catch (IOException e) {
			// fails the first time
		}
		combo.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
						textChanged();
					}
				}
			}
		);
		return combo;
	}

	protected JTabbedPane createTestRunViews() {
		JTabbedPane pane= new JTabbedPane(JTabbedPane.BOTTOM);

		FailureRunView lv= new FailureRunView(this);
		fTestRunViews.addElement(lv);
		lv.addTab(pane);

		TestHierarchyRunView tv= new TestHierarchyRunView(this);
		fTestRunViews.addElement(tv);
		tv.addTab(pane);

		pane.addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					testViewChanged();
				}
			}
		);
		return pane;
	}

	public void testViewChanged() {
		TestRunView view= (TestRunView)fTestRunViews.elementAt(fTestViewTab.getSelectedIndex());
		view.activate();
	}

	protected TestResult createTestResult() {
		return new TestResult();
	}

	protected JFrame createUI(String suiteName) {
		JFrame frame= createFrame();
		JMenuBar mb= new JMenuBar();
		createMenus(mb);
		frame.setJMenuBar(mb);

		JLabel suiteLabel= new JLabel("Test class name:");
		fSuiteCombo= createSuiteCombo();
		fRun= createRunButton();
		frame.getRootPane().setDefaultButton(fRun);
		Component browseButton= createBrowseButton();

		fUseLoadingRunner= createUseLoaderCheckBox();
		fProgressIndicator= new ProgressBar();
		fCounterPanel= createCounterPanel();

		fFailures= new DefaultListModel();

		fTestViewTab= createTestRunViews();
		JPanel failedPanel= createFailedPanel();

		fFailureView= createFailureDetailView();
		JScrollPane tracePane= new JScrollPane(fFailureView.getComponent(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		fStatusLine= createStatusLine();
		fQuitButton= createQuitButton();
		fLogo= createLogo();

		JPanel panel= new JPanel(new GridBagLayout());

		addGrid(panel, suiteLabel,	0, 0, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, fSuiteCombo, 	0, 1, 1, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, browseButton, 	1, 1, 1, GridBagConstraints.NONE, 			0.0, GridBagConstraints.WEST);
		addGrid(panel, fRun, 		2, 1, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);

		addGrid(panel, fUseLoadingRunner,  	0, 2, 3, GridBagConstraints.NONE, 1.0, GridBagConstraints.WEST);
		//addGrid(panel, new JSeparator(), 	0, 3, 3, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);


		addGrid(panel, fProgressIndicator, 	0, 3, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
		addGrid(panel, fLogo, 			2, 3, 1, GridBagConstraints.NONE, 			0.0, GridBagConstraints.NORTH);

		addGrid(panel, fCounterPanel,	 0, 4, 2, GridBagConstraints.NONE, 			0.0, GridBagConstraints.WEST);
		addGrid(panel, new JSeparator(), 	0, 5, 2, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);
		addGrid(panel, new JLabel("Results:"),	0, 6, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);

		JSplitPane splitter= new JSplitPane(JSplitPane.VERTICAL_SPLIT, fTestViewTab, tracePane);
		addGrid(panel, splitter, 	 0, 7, 2, GridBagConstraints.BOTH, 			1.0, GridBagConstraints.WEST);

		addGrid(panel, failedPanel, 	 2, 7, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.NORTH/*CENTER*/);

		addGrid(panel, fStatusLine, 	 0, 9, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.CENTER);
		addGrid(panel, fQuitButton, 	 2, 9, 1, GridBagConstraints.HORIZONTAL, 	0.0, GridBagConstraints.CENTER);

		frame.setContentPane(panel);
		frame.pack();
		frame.setLocation(200, 200);
		return frame;
	}

	private void addGrid(JPanel p, Component co, int x, int y, int w, int fill, double wx, int anchor) {
		GridBagConstraints c= new GridBagConstraints();
		c.gridx= x; c.gridy= y;
		c.gridwidth= w;
		c.anchor= anchor;
		c.weightx= wx;
		c.fill= fill;
		if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL)
			c.weighty= 1.0;
		c.insets= new Insets(y == 0 ? 10 : 0, x == 0 ? 10 : GAP, GAP, GAP); 
		p.add(co, c);
	}

	protected String getSuiteText() {
		if (fSuiteCombo == null)
			return "";
		return (String)fSuiteCombo.getEditor().getItem();
	}

	public ListModel getFailures() {
		return fFailures;
	}

	public void insertUpdate(DocumentEvent event) {
		textChanged();
	}

	public void browseTestClasses() {
		TestCollector collector= createTestCollector();
		TestSelector selector= new TestSelector(fFrame, collector);
		if (selector.isEmpty()) {
			JOptionPane.showMessageDialog(fFrame, "No Test Cases found.\nCheck that the configured \'TestCollector\' is supported on this platform.");
			return;
		}
		selector.show();
		String className= selector.getSelectedItem();
		if (className != null)
			setSuite(className);
	}

	TestCollector createTestCollector() {
		String className= BaseTestRunner.getPreference(TESTCOLLECTOR_KEY);
		if (className != null) {
			Class collectorClass= null;
			try {
				collectorClass= Class.forName(className);
				return (TestCollector)collectorClass.newInstance();
			} catch(Exception e) {
				JOptionPane.showMessageDialog(fFrame, "Could not create TestCollector - using default collector");
			}
		}
		return new SimpleTestCollector();
	}

	private Image loadFrameIcon() {
		ImageIcon icon= (ImageIcon)getIconResource(BaseTestRunner.class, "smalllogo.gif");
		if (icon != null)
			return icon.getImage();
		return null;
	}

	private void loadHistory(JComboBox combo) throws IOException {
		BufferedReader br= new BufferedReader(new FileReader(getSettingsFile()));
		int itemCount= 0;
		try {
			String line;
			while ((line= br.readLine()) != null) {
				combo.addItem(line);
				itemCount++;
			}
			if (itemCount > 0)
				combo.setSelectedIndex(0);

		} finally {
			br.close();
		}
	}

	private File getSettingsFile() {
	 	String home= System.getProperty("user.home");
 		return new File(home,".junitsession");
 	}

	private void postInfo(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					showInfo(message);
				}
			}
		);
	}

	private void postStatus(final String status) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					showStatus(status);
				}
			}
		);
	}

	public void removeUpdate(DocumentEvent event) {
		textChanged();
	}

	private void rerun() {
		TestRunView view= (TestRunView)fTestRunViews.elementAt(fTestViewTab.getSelectedIndex());
		Test rerunTest= view.getSelectedTest();
		if (rerunTest != null)
			rerunTest(rerunTest);
	}

	private void rerunTest(Test test) {
		if (!(test instanceof TestCase)) {
			showInfo("Could not reload "+ test.toString());
			return;
		}
		Test reloadedTest= null;
		TestCase rerunTest= (TestCase)test;

		try {
			Class reloadedTestClass= getLoader().reload(test.getClass()); 
			reloadedTest= TestSuite.createTest(reloadedTestClass, rerunTest.getName());
		} catch(Exception e) {
			showInfo("Could not reload "+ test.toString());
			return;
		}
		TestResult result= new TestResult();
		reloadedTest.run(result);

		String message= reloadedTest.toString();
		if(result.wasSuccessful())
			showInfo(message+" was successful");
		else if (result.errorCount() == 1)
			showStatus(message+" had an error");
		else
			showStatus(message+" had a failure");
	}

	protected void reset() {
		fCounterPanel.reset();
		fProgressIndicator.reset();
		fRerunButton.setEnabled(false);
		fFailureView.clear();
		fFailures.clear();
	}

	protected void runFailed(String message) {
		showStatus(message);
		fRun.setText("Run");
		fRunner= null;
	}

	synchronized public void runSuite() {
		if (fRunner != null) {
			fTestResult.stop();
		} else {
			setLoading(shouldReload());
			reset();
			showInfo("Load Test Case...");
			final String suiteName= getSuiteText();
			final Test testSuite= getTest(suiteName);
			if (testSuite != null) {
				addToHistory(suiteName);
				doRunTest(testSuite);
			}
		}
	}

	private boolean shouldReload() {
		return !inVAJava() && fUseLoadingRunner.isSelected();
	}


	synchronized protected void runTest(final Test testSuite) {
		if (fRunner != null) {
			fTestResult.stop();
		} else {
			reset();
			if (testSuite != null) {
				doRunTest(testSuite);
			}
		}
	}

	private void doRunTest(final Test testSuite) {
		setButtonLabel(fRun, "Stop");
		fRunner= new Thread("TestRunner-Thread") {
			public void run() {
				TestRunner.this.start(testSuite);
				postInfo("Running...");

				long startTime= System.currentTimeMillis();
				testSuite.run(fTestResult);

				if (fTestResult.shouldStop()) {
					postStatus("Stopped");
				} else {
					long endTime= System.currentTimeMillis();
					long runTime= endTime-startTime;
					postInfo("Finished: " + elapsedTimeAsString(runTime) + " seconds");
				}
				runFinished(testSuite);
				setButtonLabel(fRun, "Run");
				fRunner= null;
				System.gc();
			}
		};
		// make sure that the test result is created before we start the
		// test runner thread so that listeners can register for it.
		fTestResult= createTestResult();
		fTestResult.addListener(TestRunner.this);
		aboutToStart(testSuite);

		fRunner.start();
	}

	private void saveHistory() throws IOException {
		BufferedWriter bw= new BufferedWriter(new FileWriter(getSettingsFile()));
		try {
			for (int i= 0; i < fSuiteCombo.getItemCount(); i++) {
				String testsuite= fSuiteCombo.getItemAt(i).toString();
				bw.write(testsuite, 0, testsuite.length());
				bw.newLine();
			}
		} finally {
			bw.close();
		}
	}

	private void setButtonLabel(final JButton button, final String label) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					button.setText(label);
				}
			}
		);
	}

	public void handleTestSelected(Test test) {
		fRerunButton.setEnabled(test != null && (test instanceof TestCase));
		showFailureDetail(test);
	}

	private void showFailureDetail(Test test) {
		if (test != null) {
			ListModel failures= getFailures();
			for (int i= 0; i < failures.getSize(); i++) {
				TestFailure failure= (TestFailure)failures.getElementAt(i);
				if (failure.failedTest() == test) {
					fFailureView.showFailure(failure);
					return;
				}
			}
		}
		fFailureView.clear();
	}

	private void showInfo(String message) {
		fStatusLine.showInfo(message);
	}

	private void showStatus(String status) {
		fStatusLine.showError(status);
	}

	/**
	 * Starts the TestRunner
	 */
	public void start(String[] args) {
		String suiteName= processArguments(args);
		fFrame= createUI(suiteName);
		fFrame.pack();
		fFrame.setVisible(true);

		if (suiteName != null) {
			setSuite(suiteName);
			runSuite();
		}
	}

	private void start(final Test test) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					int total= test.countTestCases();
					fProgressIndicator.start(total);
					fCounterPanel.setTotal(total);
				}
			}
		);
	}

	/**
	 * Wait until all the events are processed in the event thread
	 */
	private void synchUI() {
		try {
			SwingUtilities.invokeAndWait(
				new Runnable() {
					public void run() {}
				}
			);
		}
		catch (Exception e) {
		}
	}

	/**
	 * Terminates the TestRunner
	 */
	public void terminate() {
		fFrame.dispose();
		try {
			saveHistory();
		} catch (IOException e) {
			System.out.println("Couldn't save test run history");
		}
		System.exit(0);
	}

	public void textChanged() {
		fRun.setEnabled(getSuiteText().length() > 0);
		clearStatus();
	}

	protected void clearStatus() {
		fStatusLine.clear();
	}

	public static Icon getIconResource(Class clazz, String name) {
		URL url= clazz.getResource(name);
		if (url == null) {
			System.err.println("Warning: could not load \""+name+"\" icon");
			return null;
		}
		return new ImageIcon(url);
	}

	private void about() {
		AboutDialog about= new AboutDialog(fFrame);
		about.show();
	}
}