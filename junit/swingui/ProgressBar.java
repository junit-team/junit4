package junit.swingui;

import java.awt.Color;

import javax.swing.JProgressBar;

/**
 * A progress bar showing the green/red status
 */
class ProgressBar extends JProgressBar {
	boolean fError= false;
	
	public ProgressBar() {
		super(); 
		setForeground(getStatusColor());
	}
	
	protected Color getStatusColor() {
		if (fError)
			return Color.red;
		return Color.green;
	}
		
	public void reset() {
		fError= false;
		updateBarColor();
		setValue(0);
	}
	
	public void start(int total) {
		setMaximum(total);
		reset();
	}
	
	public void step(int value, boolean successful) {
		setValue(value);
		if (!fError && !successful) {
			fError= true;
			updateBarColor();
		}
		}
	
	protected void updateBarColor() {
		setForeground(getStatusColor());
	}
}
