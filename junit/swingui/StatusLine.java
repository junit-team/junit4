package junit.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * A status line component.
 */
public class StatusLine extends JTextField {
	public static final Font PLAIN_FONT= new Font("dialog", Font.PLAIN, 12);
	public static final Font BOLD_FONT= new Font("dialog", Font.BOLD, 12);

	public StatusLine(int preferredWidth) {
		super();
		setFont(BOLD_FONT);
		setEditable(false);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		Dimension d= getPreferredSize();
		d.width= preferredWidth;
		setPreferredSize(d);
	}
	
	public void showInfo(String message) {
		setFont(PLAIN_FONT);
		setForeground(Color.black);
		setText(message);
	}
	
	public void showError(String status) {
		setFont(BOLD_FONT);
		setForeground(Color.red);
		setText(status);
		setToolTipText(status);
	}
	
	public void clear() {
		setText("");
		setToolTipText(null);
	}
}