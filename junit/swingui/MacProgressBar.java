package junit.swingui;

import javax.swing.JTextField;

/**
	http://java.sun.com/developer/technicalArticles/JavaLP/JavaToMac2/
*/
public class MacProgressBar extends ProgressBar {

	private JTextField component;

	public MacProgressBar(JTextField component) {
		super();
		this.component= component;
	 }

	protected void updateBarColor() {
		component.setBackground(getStatusColor());
	}
}
