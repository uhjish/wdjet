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
import org.omelogic.hocuslocus.logic.*;


public class LoadDialog extends ActionDialog implements ActionListener {

	//private WdjetService service;
	private UUID newSession;
	private JComboBox logicField;
	private OmeLogic[] gotLogics;
	private boolean actuallyLoad;
		
	public LoadDialog(OmeLogicView logicView, WdjetService svc, UUID ssn)
	{
		super(logicView, svc, ssn, "_. l o a d ._");
		
		JPanel newPanelTop = new JPanel(new GridLayout(0,1));
		//newPanelTop.setPreferredSize(new Dimension(400,150));
		newPanelTop.add(new JLabel("Session: "));
		
		try
		{
			gotLogics = svc.getSavedLogics();
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,"WdjetService.getLogics() failed while LoadDialog-ing\n"+e.toString());
			this.setVisible(false);
			//return;
		}
		
		logicField = new JComboBox(gotLogics);
		newPanelTop.add(logicField);
		final JTextArea textArea = new JTextArea(5, 20);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);
		newPanelTop.add(scrollPane);
		textArea.setText("SpAss: "+gotLogics[0].getSpass()+"\n"+gotLogics[0].getDescription());
		logicField.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int i = logicField.getSelectedIndex();
				textArea.setText("SpAss: "+gotLogics[i].getSpass()+"\n"+gotLogics[i].getDescription());
				return;
			}
		});
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
	
	public UUID loadSession(){
		this.pack();
		this.setVisible(true);
		int i = logicField.getSelectedIndex();
		newSession = gotLogics[i].getID();
		if (actuallyLoad){
			return newSession;
		}else{
			return null;
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("cancel".equals(e.getActionCommand()))
		{
			actuallyLoad = false;
			this.setVisible(false);
		}
		if ("accept".equals(e.getActionCommand()))
		{
			actuallyLoad = true;
			this.setVisible(false);
		}
		
		
		
	}


}
