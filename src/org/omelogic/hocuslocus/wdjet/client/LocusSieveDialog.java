/*
 *      LocutionFilterDialog.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
package org.omelogic.hocuslocus.wdjet.client;

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
import java.util.regex.*;
public class LocusSieveDialog extends JDialog implements ActionListener
{
	
	LocusSieve returnSieve;
	
	//JComboBox whichSetBox;
	JComboBox filterTypeBox;
	JComboBox inclusionBox;
	JTextField filterAnnoBox;
	JTextField stringFilterValueBox;
	JTextField numericFilterLowerBox;
	JTextField numericFilterUpperBox;
	
	//String[] whichSetChoices = {"query","target"};
	String[] filterTypeChoices = {"string", "numeric"};
	String[] inclusionChoices = {"inclusion","exclusion"};

	public LocusSieveDialog()
	{
		
		super();
		
		returnSieve = null;
		this.setTitle("_.LocusSieve._");
		//this.setUndecorated(true);
		//this.getRootPane().setBorder((Border)(BorderFactory.createTitledBorder("_. add locution filter ._")));
		this.setModal(true);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout( mainPanel, BoxLayout.PAGE_AXIS));
		
		//mainPanel.add(new JLabel("Which Set"));
		//whichSetBox = new JComboBox(whichSetChoices);
				
		//mainPanel.add(whichSetBox);
		
		filterTypeBox = new JComboBox(filterTypeChoices);
		filterTypeBox.setBorder( BorderFactory.createTitledBorder("Filter Type"));
		mainPanel.add(filterTypeBox);
		
		filterAnnoBox = new JTextField(10);
		filterAnnoBox.setBorder( BorderFactory.createTitledBorder("Filter Field"));
		mainPanel.add(filterAnnoBox);
		
		inclusionBox = new JComboBox(inclusionChoices);
		inclusionBox.setBorder( BorderFactory.createTitledBorder("Filter Action"));
		mainPanel.add(inclusionBox);
		
		final JPanel dependentPanel = new JPanel();
		final CardLayout dependentPanelLayout = new CardLayout();
		dependentPanel.setLayout(dependentPanelLayout);
		
		JPanel stringFilterPanel = new JPanel();
		stringFilterPanel.setLayout(new BoxLayout( stringFilterPanel, BoxLayout.PAGE_AXIS));
		stringFilterPanel.add( new JLabel("Value Regex") );
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
		dependentPanel.setBorder( BorderFactory.createTitledBorder("Filter Value"));
		
		filterTypeBox.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent evt ){
				dependentPanelLayout.show( dependentPanel, (String)evt.getItem() );
			}
		});


		mainPanel.add( dependentPanel );
		
		JPanel buttonPanel = new JPanel();
		JButton acceptButton = new JButton("Accept");
		acceptButton.setActionCommand("accept");
		acceptButton.addActionListener(this);
		buttonPanel.add( acceptButton );
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add( cancelButton );
		
		mainPanel.add( buttonPanel );

		this.getContentPane().add(mainPanel);
		
	}
	
	public LocusSieve getSieve()
	{
		this.pack();
		this.setVisible(true);		
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
			String anno = filterAnnoBox.getText();
			boolean inclusion = false;
			if (inclusionBox.getSelectedIndex()==0)	{
				inclusion = true;
			}
			System.out.println("Selected Filter: "+filterTypeBox.getSelectedIndex());
			if (filterTypeBox.getSelectedIndex() == 0){
				//string with regex filter
				System.out.println("Making string filter.");
				Pattern patt;
				try{
					patt = Pattern.compile(stringFilterValueBox.getText());
				}catch(Exception regexcep){
					JOptionPane.showMessageDialog(this,"Cannot parse the value regular expression!"+ regexcep.toString());
					return;
				}
				returnSieve = new LocusStringSieve( anno, patt, inclusion );
				System.out.println("Made: "+returnSieve.toString());
			}
			if (filterTypeBox.getSelectedIndex() == 1){
				//numeric with lower and upper bound
				System.out.println("Making string filter.");
				double lower;
				double upper;
				try{
					lower = Double.parseDouble( numericFilterLowerBox.getText() );
				}catch(Exception lowxcep){
					JOptionPane.showMessageDialog(this,"Cannot parse your lower bound as a double!"+ lowxcep.toString());
					return;
				}
				try{
					upper = Double.parseDouble( numericFilterUpperBox.getText() );
				}catch(Exception upxcep){
					JOptionPane.showMessageDialog(this,"Cannot parse your upper bound as a double!"+ upxcep.toString());
					return;
				}
				try{
					returnSieve = new LocusNumericSieve( anno, lower, upper, inclusion );
				}catch (Exception newxcep){
					JOptionPane.showMessageDialog(this,"Error creating LocusNumericSieve"+ newxcep.toString());
					return;
				}
				System.out.println("Made: "+returnSieve.toString());

			}
		
			this.setVisible(false);	
		}
	}

}
