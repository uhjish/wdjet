package org.omelogic.hocuslocus.wdjet.client;
/*
 *      CommodusView.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
import org.omelogic.interlocutor.*;
import org.omelogic.interlocutor.data.*;
import org.omelogic.crosseyed.*;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.UUID;


public class InterlocutionViewChooser extends JPanel implements ActionListener
{
	private Interlocution data; 
	
	public InterlocutionViewChooser(Object nodeData) throws Exception
	{
		super(new BorderLayout());
		try
		{
			data = (Interlocution)nodeData;
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			JTextField detailsField = new JTextField();
			detailsField.setEditable(false);
			detailsField.setText("hasTrackPrefix: " + data.hasTrackPrefix() + "\nparsedQueryAnnos: " + data.parsedQueryAnnos() + "\nquerySize: " + data.getQuerySize() +"\nQueriedTracks: " + data.getQueriedTracks().toString());
			JButton commodusButton = new JButton("commodus");
			commodusButton.setActionCommand("commodus");
			commodusButton.addActionListener(this);
			JButton connexusButton = new JButton("connexus");
			connexusButton.setActionCommand("connexus");
			connexusButton.addActionListener(this);
			JButton cancelButton = new JButton("cancel");
			cancelButton.setActionCommand("cancel");
			cancelButton.addActionListener(this);
			this.add(detailsField);
			this.add(commodusButton);
			this.add(connexusButton);
			this.add(cancelButton);
			
		}catch(Exception xcep){
			throw new Exception("CommodusView: Couldn't cast nodeData to CommodusData!\n"+xcep.toString());
		}
		
	}

	public void actionPerformed(ActionEvent e)
	{
		
		if("cancel".equals(e.getActionCommand()))
		{
		//	this.setVisible(false);
		}
		
		JFrame viewerFrame = new JFrame();
		JPanel viewPanel;
		
		if ("commodus".equals(e.getActionCommand()))
		{
			viewPanel = new Commodus( data );
			viewerFrame.add(viewPanel);
		}
		
		if ("connexus".equals(e.getActionCommand()))
		{
			viewPanel = new Connexus( data );
			viewerFrame.add(viewPanel);
		}
		
		viewerFrame.pack();
		viewerFrame.setVisible(true);
	}
		
}
