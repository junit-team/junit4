package junit.swingui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * A simple progress bar showing the green/red status
 */
class ProgressBar extends JPanel {
	boolean fError= false;
	int fTotal= 0;
	int fProgress= 0;
	int fProgressX= 0;
	
	public ProgressBar() {
		super();
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	private Color getStatusColor() {
		if (fError)
			return Color.red;
		return Color.green;
	}
	
	public void paintBackground(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
	}
	
	public void paintComponent(Graphics g) {
		paintBackground(g);
		paintStatus(g);
	}
	
	public void paintStatus(Graphics g) {
		g.setColor(getStatusColor());
		Rectangle r= new Rectangle(0, 0, fProgressX, getBounds().height);
		g.fillRect(1, 1, r.width-1, r.height-2);
	}
	
	private void paintStep(int startX, int endX) {
		repaint(startX, 1, endX-startX, getBounds().height-2);
	}
	
	public void reset() {
		fProgressX= 1;
		fProgress= 0;
		fError= false;
		repaint();
	}
	
	public int scale(int value) {
		if (fTotal > 0)
			return Math.max(1, value*(getBounds().width-1)/fTotal);
		return value; 
	}
	
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		fProgressX= scale(fProgress);
	}
	
	public void start(int total) {
		fTotal= total;
		reset();
	}
	
	public void step(boolean successful) {
		fProgress++;
		int x= fProgressX;

		fProgressX= scale(fProgress);

		if (!fError && !successful) {
			fError= true;
			x= 1;
		}
		paintStep(x, fProgressX);
	}
}