package junit.awtui;

import java.awt.*;
import java.awt.event.*;

import junit.runner.Version;

class WarningDialog extends Dialog {
	private boolean fChoice;
	
	public WarningDialog(Frame parent, String message1, String message2) {
		super(parent);
		
		setResizable(false);
		setLayout(new GridBagLayout());
		setSize(450, 120);
		setTitle("Warning");
		setModal(true);
		setLocation(300, 300);

		Button yes= new Button("Yes");
		yes.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fChoice= true;
					dispose();
				}
			}
		);
		
		Button no= new Button("No");
		no.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fChoice= false;
					dispose();
				}
			}
		);

		Label label1= new Label(message1);		
		Label label2= new Label(message2);
		
		GridBagConstraints constraintsLabel1= new GridBagConstraints();
		constraintsLabel1.gridx= 0; constraintsLabel1.gridy = 0;
		constraintsLabel1.gridwidth= 1; constraintsLabel1.gridheight= 1;
		constraintsLabel1.anchor= GridBagConstraints.WEST;
		add(label1, constraintsLabel1);

		GridBagConstraints constraintsLabel2= new GridBagConstraints();
		constraintsLabel2.gridx= 0; constraintsLabel2.gridy= 1;
		constraintsLabel2.gridwidth= 2; constraintsLabel2.gridheight= 1;
		constraintsLabel2.anchor= GridBagConstraints.WEST;
		add(label2, constraintsLabel2);

		GridBagConstraints constraintsYes= new GridBagConstraints();
		constraintsYes.gridx= 0; constraintsYes.gridy= 2;
		constraintsYes.gridwidth= 1; constraintsYes.gridheight= 1;
		constraintsYes.anchor= GridBagConstraints.EAST;
		add(yes, constraintsYes);

		GridBagConstraints constraintsNo= new GridBagConstraints();
		constraintsNo.gridx= 1; constraintsNo.gridy= 2;
		constraintsNo.gridwidth= 1; constraintsNo.gridheight= 1;
		constraintsNo.anchor= GridBagConstraints.WEST;
		add(no, constraintsNo);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			}
		);
	}
	
	public boolean getChoice() {
		return fChoice;
	}	
}