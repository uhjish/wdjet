/*
 *      LocutionFilterDialog.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */

import org.omelogic.locus.*;
import org.omelogic.locus.filter.*;
import org.omelogic.hocuslocus.logic.*;
import org.omelogic.hldbmanager.*;


import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class LocusSieveDialog extends JDialog
{
	
	LocusSieve returnSieve;
	
	JComboBox whichSetBox;
	JComboBox filterTypeBox;
	JTextField filterAnnoBox;
	JTextField stringFilterValueBox;
	JTextField numericFilterLowerBox;
	JTextField numericFilterUpperBox;
	
	String[] whichSetChoices = {"query","target"};
	String[] filterTypeChoices = {"string", "numeric"};
	String[] inclusionChoices = {"inclusion","exclusion"};

	public LocusSieveDialog()
	{
		
		super();
		
		returnSieve = null;
		
		this.setUndecorated(true);
		this.getRootPane().setBorder((Border)(BorderFactory.createTitledBorder("_. add locution filter ._")));
		this.setModal(true);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout( mainPanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(new JLabel("Which Set"));
		whichSetBox = new JComboBox(whichSetChoices);
				
		mainPanel.add(whichSetBox);
		
		mainPanel.add(new JLabel("Filter Type"));
		filterTypeBox = new JComboBox(filterTypeChoices);
		mainPanel.add(filterTypeBox);
		
		mainPanel.add(new JLabel("Filter Field"));
		filterAnnoBox = new JTextField(10);
		mainPanel.add(filterAnnoBox);
		
		final JPanel dependentPanel = new JPanel();
		final CardLayout dependentPanelLayout = new CardLayout();
		dependentPanel.setLayout(dependentPanelLayout);
		
		JPanel stringFilterPanel = new JPanel();
		stringFilterPanel.setLayout(new BoxLayout( stringFilterPanel, BoxLayout.PAGE_AXIS));
		stringFilterPanel.add( new JLabel("Filter Value") );
		stringFilterValueBox = new JTextField(10);
		stringFilterPanel.add(stringFilterValueBox);
		
		JPanel numericFilterPanel = new JPanel();
		numericFilterPanel.setLayout(new BoxLayout( numericFilterPanel, BoxLayout.PAGE_AXIS));
		numericFilterPanel.add( new JLabel("Lower Bound") );
		numericFilterLowerBox = new JTextField(10);
		numericFilterPanel.add( numericFilterLowerBox );
		numericFilterPanel.add( new JLabel("Upper Bound") );
		numericFilterUpperBox = new JTextField(10);
		numericFilterPanel.add( numericFilterUpperBox );
		
		dependentPanel.add( stringFilterPanel, "string" );
		dependentPanel.add( numericFilterPanel, "numeric" );
		
		whichSetBox.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent evt ){
				dependentPanelLayout.show( dependentPanel, (String)evt.getItem() );
			}
		});


		mainPanel.add( dependentPanel );
		
		JPanel buttonPanel = new JPanel();
		JButton acceptButton = new JButton("Accept");
		acceptButton.setActionCommand("accept");
		buttonPanel.add( acceptButton );
		JButton cancelButton = new JButton("Cancel");
		acceptButton.setActionCommand("cancel");
		buttonPanel.add( cancelButton );
		
		mainPanel.add( buttonPanel );

		this.getContentPane().add(mainPanel);
		this.pack();
		this.setVisible(true);
		
	}
	
	public LocusSieve getSieve()
	{
	
		return returnSieve;
	
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		
		if("cancel".equals(e.getActionCommand()))
		{
			this.setVisible(false);
		}
		
		if ("accept".equals(e.getActionCommand()))
		{
			
		
			this.setVisible(false);	
		}
	}

}
