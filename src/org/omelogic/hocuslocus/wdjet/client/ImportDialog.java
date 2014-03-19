/*
 *      ImportDialog.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */
package org.omelogic.hocuslocus.wdjet.client;


import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.UUID;

public class ImportDialog extends ActionDialog implements ActionListener
{

	JTabbedPane importOptionTabs;

	public ImportDialog(OmeLogicView logicView, WdjetService svc, UUID ssn){
	
		super(logicView, svc, ssn, "--import--");
		//this.setUndecorated(true);

		
		//make overall container
		JPanel importDialogContents = new JPanel();
		importDialogContents.setLayout(new BoxLayout(importDialogContents, BoxLayout.PAGE_AXIS));
		//importDialogContents.setPreferredSize(new Dimension(400,400));
		//make import options tabs holder
		importOptionTabs = new JTabbedPane();
		//importOptionTabs.setPreferredSize(new Dimension(400,400));
		//file import panel
		JPanel importFilePanel = new ImportDialogFilePanel();
		//db import panel
		JPanel importSpassticPanel = new ImportDialogSpassticPanel(logicView, svc, ssn);
		
		//add them to the tabs holder
		importOptionTabs.addTab("File", importFilePanel);
		importOptionTabs.addTab("SpAssTIC", importSpassticPanel);
		
		//make button panel
		JPanel importButtonPanel = new JPanel(new FlowLayout());
		importButtonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
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
		
		importButtonPanel.add(cancelImportButton);
		importButtonPanel.add(acceptImportButton);
		
		//add the tab holder to the container
		importDialogContents.add(importOptionTabs);
		//add the button holder to the container
		importDialogContents.add(importButtonPanel);
		
		
		this.getContentPane().add(importDialogContents);
		
		
		this.pack();
		this.setVisible(true);		
	
	}


	
	public void actionPerformed(ActionEvent e)
	{
		
		if("cancel".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
		
		if ("accept".equals(e.getActionCommand()))
		{
			((ImportDialogPanel)importOptionTabs.getSelectedComponent()).runImportProcess(service, session);
			this.setVisible(false);
		}
		

		
	}
	
	
	

}
