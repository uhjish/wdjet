package org.omelogic.hocuslocus.wdjet.client;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.UUID;

public class ActionDialog extends JDialog
{
	protected WdjetService service;
	protected UUID session;
	
 	public ActionDialog(OmeLogicView logicView, WdjetService svc, UUID ssn, String title)
 	{
		super();
		
		service = svc;
		session = ssn;
		this.setTitle(title);
		//this.setSize(400,400);
		//this.setUndecorated(true);
		//this.getRootPane().setBorder((Border)(BorderFactory.createTitledBorder(title)));
		//this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		this.setModal(true);
		this.setLocationRelativeTo(logicView);
		//this.setAlwaysOnTop(true);
		//this.setLocation(150,150);
		//this.setPreferredSize(new Dimension(500,500));
		//this.setDefaultCloseOperation(
		//	JDialog.DO_NOTHING_ON_CLOSE);
		//this.addWindowListener(new WindowAdapter() {
		//	public void windowClosing(WindowEvent we) {
				//setLabel("Thwarted user attempt to close window.");
		//	}
		//});
		
		
 	}


}

