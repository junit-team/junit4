package junit.swingui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * A panel with test run counters
 */
public class CounterPanel extends Panel {
	private JTextField fNumberOfErrors;
	private JTextField fNumberOfFailures;
	private JTextField fNumberOfRuns;
	private int fTotal;
	
	public CounterPanel() {
		super(new GridLayout(2, 3));	
		add(new JLabel("Runs:"));		
		add(new JLabel("Errors:"));	
		add(new JLabel("Failures: "));	
		fNumberOfErrors= createOutputField();
		fNumberOfFailures= createOutputField();
		fNumberOfRuns= createOutputField();
		add(fNumberOfRuns);
		add(fNumberOfErrors);
		add(fNumberOfFailures);
	} 
	
	private JTextField createOutputField() {
		JTextField field= new JTextField("0", 4);
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