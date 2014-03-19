package org.omelogic.hocuslocus.wdjet.client;

/*
 *      WdjetConnectDialog.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.event.KeyEvent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ComponentOrientation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class WdjetConnectDialog extends JDialog implements ActionListener{

	private String selectedHost;
	private JTextField remoteHostText;
	private boolean isRemote;
	public WdjetConnectDialog()
	{
		super();
		isRemote = false;
		this.setUndecorated(true);
		this.getRootPane().setBorder((Border)(BorderFactory.createTitledBorder("_. c o n n e c t ._")));
		//this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		this.setModal(true);
		this.setAlwaysOnTop(true);
		this.setLocation(200,200);
		this.setPreferredSize(new Dimension(500,200));
		this.setDefaultCloseOperation(
			JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				//setLabel("Thwarted user attempt to close window.");
			}
		});		
		
		//show the options
		JPanel choicesPanel = new JPanel(new GridLayout(0,1));
		choicesPanel.setPreferredSize(new Dimension(500,175));

		ButtonGroup hostChoices = new ButtonGroup();
		
		JRadioButton localButton = new JRadioButton("Localhost: 127.0.0.1");
		localButton.setSelected(true);
		localButton.setMnemonic(KeyEvent.VK_L);
		localButton.setActionCommand("connectLocal");
		localButton.addActionListener(this);
		
		JRadioButton remoteButton = new JRadioButton("Remote:");
		remoteButton.setMnemonic(KeyEvent.VK_R);
		remoteButton.setActionCommand("connectRemote");
		remoteButton.addActionListener(this);
		
		remoteHostText = new JTextField("hocuslocus");
		remoteHostText.setEnabled(false);
		
		hostChoices.add(localButton);
		hostChoices.add(remoteButton);
		
		//make button panel
		JPanel initButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
		initButtonPanel.setPreferredSize(new Dimension(500,25));
		initButtonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
		//add cancel button
		JButton cancelInitButton = new JButton("Cancel");
		cancelInitButton.setActionCommand("cancel");
		cancelInitButton.setMnemonic(KeyEvent.VK_C);
		cancelInitButton.addActionListener(this);

		//add accept button
		JButton acceptInitButton = new JButton("Accept");
		acceptInitButton.setActionCommand("accept");
		acceptInitButton.setMnemonic(KeyEvent.VK_A);
		acceptInitButton.addActionListener(this);
		
		choicesPanel.add(localButton);
		choicesPanel.add(remoteButton);
		choicesPanel.add(remoteHostText);

		initButtonPanel.add(cancelInitButton);
		initButtonPanel.add(acceptInitButton);
		
		JPanel overallPanel = new JPanel(new GridLayout(0,1));
		overallPanel.add(choicesPanel);
		overallPanel.add(initButtonPanel);

		this.getContentPane().add(overallPanel);
		
		
		
		//show ok/quit
		
	}
	
	public String getHostChoice()
	{
		this.pack();
		this.setVisible(true);
		return selectedHost;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("connectLocal".equals(e.getActionCommand()))
		{
			isRemote = false;
			remoteHostText.setEnabled(false);
		}

		if ("connectRemote".equals(e.getActionCommand()))
		{
			isRemote = true;
			remoteHostText.setEnabled(true);
		}
		
		
		
		if("cancel".equals(e.getActionCommand()))
		{
			System.exit(0);
		}
		
		if ("accept".equals(e.getActionCommand()))
		{

			if (isRemote){
				selectedHost = (String)remoteHostText.getText();
				//validate
			}else{
				selectedHost = "localhost";
			}
			this.setVisible(false);	
		}
		
		
	}



}
