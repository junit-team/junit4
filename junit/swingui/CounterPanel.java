package junit.swingui;

import java.awt.GridLayout;

import javax.swing.*;

/**
 * A panel with test run counters
 */
public class CounterPanel extends JPanel {
	private JTextField fNumberOfErrors;
	private JTextField fNumberOfFailures;
	private JTextField fNumberOfRuns;
	private Icon fFailureIcon= TestRunner.getIconResource(getClass(), "icons/failure.gif");
	private Icon fErrorIcon= TestRunner.getIconResource(getClass(), "icons/error.gif");

	private int fTotal;
	
	public CounterPanel() {
		super(new GridLayout(1, 6)); 	
		fNumberOfErrors= createOutputField(8);
		fNumberOfFailures= createOutputField(5);
		fNumberOfRuns= createOutputField(5); 
		add(new JLabel("Runs:"));		
		add(fNumberOfRuns);
		add(new JLabel("Errors:", fErrorIcon, SwingConstants.LEFT));	
		add(fNumberOfErrors);
		add(new JLabel("Failures: ", fFailureIcon, SwingConstants.LEFT));	
		add(fNumberOfFailures);
	} 
	
	private JTextField createOutputField(int width) {
		JTextField field= new JTextField("0", width);
		field.setHorizontalAlignment(JTextField.LEFT);
		field.setFont(StatusLine.BOLD_FONT);
		field.setEditable(false);
		field.setBorder(BorderFactory.createEmptyBorder());
		return field;
	}
	
	public void reset() {
		setLabelValue(fNumberOfErrors, 0);
		setLabelValue(fNumberOfFailures, 0);
		setLabelValue(fNumberOfRuns, 0);
		fTotal= 0;
	}
	
	public void setTotal(int value) {
		fTotal= value;
	}
	
	public void setRunValue(int value) {
		fNumberOfRuns.setText(Integer.toString(value) + "/" + fTotal);
	}
	
	public void setErrorValue(int value) {
		setLabelValue(fNumberOfErrors, value);
	}
	
	public void setFailureValue(int value) {
		setLabelValue(fNumberOfFailures, value);
	}
	
	private String asString(int value) {
		return Integer.toString(value);
	}
	
	private void setLabelValue(JTextField label, int value) {
		label.setText(Integer.toString(value));
	}
}