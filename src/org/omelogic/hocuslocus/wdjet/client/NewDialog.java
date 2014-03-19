package org.omelogic.hocuslocus.wdjet.client;

/*
 *      NewDialog.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.UUID;

import org.omelogic.hldbmanager.*;


public class NewDialog extends ActionDialog implements ActionListener {

	//private WdjetService service;
	private UUID newSession;
	private DatabaseDescriptor[] spassDBs;
	private JTextField nameField;
	private JTextArea descrField;
	private JComboBox spassField;

	public NewDialog(OmeLogicView logicView, WdjetService svc, UUID ssn)
	{
		super(logicView, svc, ssn, "_. n e w ._");
		
		JPanel newPanelTop = new JPanel(new GridLayout(0,1));
		newPanelTop.setPreferredSize(new Dimension(400,150));
		newPanelTop.add(new JLabel("SpAss: "));
		
		String[] spassIDs = null;
		try
		{
			spassDBs = svc.getSpAssDescriptions();
			spassIDs = new String[spassDBs.length];
			for (int i = 0; i < spassDBs.length; i++)
			{
				spassIDs[i] = spassDBs[i].getFriendlyName();
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,"WdjetService.getSpAssDescriptors() failed while making NewSessionDialog!");
			System.out.println("WdjetService.getSpAssDescriptors() failed while making NewSessionDialog!\n"+e.toString());
			this.setVisible(false);
		}
		
		spassField = new JComboBox(spassIDs);
		newPanelTop.add(spassField);
		newPanelTop.add(new JLabel("Name: "));
		nameField = new JTextField();
		newPanelTop.add(nameField);
		//name = nameField.getText();
		newPanelTop.add(new JLabel("Description:"));
		descrField = new JTextArea(10,20);
		newPanelTop.add(descrField);
		//description = descrField.getText();
		
		JPanel newPanelBottom = new JPanel(new FlowLayout());
		newPanelBottom.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		newPanelBottom.setPreferredSize(new Dimension(400,100));
		//add cancel button
		JButton cancelImportButton = new JButton("Cancel");
		cancelImportButton.setActionCommand("cancel");
		cancelImportButton.setMnemonic(KeyEvent.VK_C);
		cancelImportButton.addActionListener(this);
		//add accept button
		JButton acceptImportButton = new JButton("Accept");
		acceptImportButton.setActionCommand("accept");
		acceptImportButton.setMnemonic(KeyEvent.VK_A);
		acceptImportButton.addActionListener(this);
		
		newPanelBottom.add(cancelImportButton);
		newPanelBottom.add(acceptImportButton);
		
		JPanel newPanel = new JPanel(new GridLayout(0,1));
		newPanel.add(newPanelTop);
		newPanel.add(newPanelBottom);
		
		this.getContentPane().add(newPanel);

	}
	
	public UUID getNewSession(){
		this.pack();
		this.setVisible(true);
		return newSession;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("cancel".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
		if ("accept".equals(e.getActionCommand()))
		{
			System.out.println("DEBUG:" + service.toString() );
			System.out.flush(); 
			try
			{
				String spass = spassDBs[ spassField.getSelectedIndex() ].getDatabaseName();
				System.out.println("Creating new session : " + spass +"\n");
				newSession = (UUID)service.getNewLogic(spass,  nameField.getText(), descrField.getText());
				//System.out.println("new session: "+ newSession.toString());
			}catch(Exception xception){
				System.out.println(xception.toString());
			}
			
			this.setVisible(false);
		}
		
		
		
	}


}
