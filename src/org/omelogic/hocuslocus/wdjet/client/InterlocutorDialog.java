package org.omelogic.hocuslocus.wdjet.client;

import org.omelogic.locus.*;
import org.omelogic.locus.filter.*;
import org.omelogic.hocuslocus.logic.*;
import org.omelogic.hldbmanager.*;
import org.omelogic.interlocutor.*;


import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class InterlocutorDialog extends ActionDialog implements ActionListener
{

	JTabbedPane importOptionTabs;
	JTextField nameField;
	SpassticSelectionPanel spassSelector;
	JCheckBox parseQueryAnnosBox;
	JCheckBox addTrackPrefixBox;
	JList controlChoiceList;
	JComboBox controlTypeBox;
	OmeNode queryNode;
	OmeLogicView logicView;
	//ArrayList<UUID> controlNodes;
	
	boolean controlIsPopulation;
	
	String[] ctrlTypeChoices = { "None", "Population", "Sampling" };
	String[] suppTypeChoices = { "Percent (0-100)", "Number (0-numLoci)" };

	public InterlocutorDialog(OmeLogicView inlogicView, WdjetService svc, UUID ssn, OmeNode selected)
	{
	
		super(inlogicView, svc, ssn, "--commodus--");
		//this.setUndecorated(true);
		
		logicView = inlogicView;
		service = svc;
		session = ssn;
		queryNode = selected;
		String spass = logicView.getLogic().getSpass();
		System.out.println(queryNode.toString());
		UUID qid = selected.getID();
		System.out.println(qid.toString());
		queryNode = logicView.getLogic().getNode( qid );
		System.out.println(queryNode.toString());
		//make overall container
		JPanel dialogContents = new JPanel();
		dialogContents.setLayout(new BoxLayout(dialogContents, BoxLayout.PAGE_AXIS));
		//dialogContents.setPreferredSize(new Dimension(600,400));

		JTextArea description = new JTextArea(2,20);
		description.setEditable(false);
		description.setLineWrap(true);
		description.setBorder(new TitledBorder(new EtchedBorder(), 
			"Query LocusSet:") );
		description.setText(queryNode.getDetails());

		try
		{
			spassSelector = new SpassticSelectionPanel(svc, spass, false);
		}catch(Exception e){
			System.out.println(e.toString());
			this.setVisible(false);
		}

		JPanel paramsPanel = new JPanel(new FlowLayout());
		nameField = new JTextField(10);
		parseQueryAnnosBox = new JCheckBox("Parse Query Annos");
		addTrackPrefixBox = new JCheckBox("Add Track Prefixes");
		paramsPanel.add(new JLabel("Name:"));
		paramsPanel.add(nameField);
		paramsPanel.add(parseQueryAnnosBox);
		paramsPanel.add(addTrackPrefixBox);
		paramsPanel.setBorder(BorderFactory.createTitledBorder("Params"));
		//minSupportPanel.add(new JLabel("Num Controls: "));
		//numControls = new JTextField(5);
		//minSupportPanel.add(numControls);


		JPanel controlsPanel = new JPanel(new FlowLayout());
		//controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.PAGE_AXIS));

		java.util.List<OmeNode> controlChoices = logicView.getLogic().getNodesByType(OmeNode.TYPES.LOCUS_SET);
		controlChoices.remove(queryNode);
		controlChoiceList = new JList(controlChoices.toArray());
		controlChoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		controlChoiceList.setLayoutOrientation(JList.VERTICAL);
		controlChoiceList.setVisibleRowCount(4);
		controlChoiceList.setEnabled(false);
		controlTypeBox = new JComboBox(ctrlTypeChoices);
		controlTypeBox.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int selType = controlTypeBox.getSelectedIndex();
				if (selType == 0){
					//no control
					controlChoiceList.setEnabled(false);
					controlChoiceList.clearSelection();
					return;
				}
				if (selType == 1){
					//population control, only one control
					controlIsPopulation = true;
					controlChoiceList.setEnabled(true);
					controlChoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					controlChoiceList.clearSelection();
					return;
				}
				if (selType == 2){
					//sampling control, multiple controlsets
					controlIsPopulation = false;
					controlChoiceList.setEnabled(true);
					controlChoiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					controlChoiceList.clearSelection();
					return;
				}
			}
		});
		controlTypeBox.setEnabled(false);
		if (controlChoices.size() > 0){
			controlTypeBox.setEnabled(true);
		}
		controlTypeBox.setBorder(BorderFactory.createTitledBorder("Type"));
		
		JScrollPane controlListScroll = new JScrollPane(controlChoiceList);
		controlListScroll.setPreferredSize(new Dimension(150,100));
		controlListScroll.setBorder(BorderFactory.createTitledBorder("Sets"));
		
		//controlsPanel.add(new JLabel("Type"));
		controlsPanel.add(controlTypeBox);
		//controlsPanel.add(new JLabel("Sets"));
		controlsPanel.add(controlListScroll);
		controlsPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
				
		JPanel commodusPanel = new JPanel();
		commodusPanel.setLayout(new BoxLayout(commodusPanel, BoxLayout.PAGE_AXIS));
		commodusPanel.add(description);
		commodusPanel.add(paramsPanel);
		commodusPanel.add(controlsPanel);
		commodusPanel.add(spassSelector);

		dialogContents.add(commodusPanel);
		//make button panel
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				
		//add cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(this);
		//add accept button
		JButton acceptButton = new JButton("Accept");
		acceptButton.setActionCommand("accept");
		acceptButton.setMnemonic(KeyEvent.VK_A);
		acceptButton.addActionListener(this);
		
		buttonPanel.add(cancelButton);
		buttonPanel.add(acceptButton);
		
		//add the button holder to the container
		dialogContents.add(buttonPanel);
		
		
		this.getContentPane().add(dialogContents);
		
		
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
			//gather all the necessary data and run commodus
			ArrayList<TableDescriptor> tables = spassSelector.getSelectedTables();
			if (tables.size() ==0){
				JOptionPane.showMessageDialog(this,"Pick at least one table to interlocute!");
				return;
			}
			
			ArrayList<Locution> locutions = spassSelector.getSelectedLocutions();
			System.out.println("tables.size(): " +tables.size() + "\ntables.get(0): " + tables.get(0).getFriendlyName());
			
			
			
			ArrayList<UUID> controls = new ArrayList<UUID>();
			Object[] selected = controlChoiceList.getSelectedValues();
			for ( int i = 0; i < selected.length; i++){
				controls.add( ((OmeNode)selected[i]).getID());
			}
			
			if (controlTypeBox.getSelectedIndex() != 0)
			{
				if (controls.size() == 0){
					JOptionPane.showMessageDialog(this,"You must pick at least one set to use as control!");
					return;
				}
			}
					
			
			//run Commodus
			UUID commodusNode = null;
			try{
				commodusNode = service.runInterlocutor(session, queryNode.getID(),nameField.getText(), parseQueryAnnosBox.isSelected(), addTrackPrefixBox.isSelected(),tables, locutions, controlIsPopulation, controls);
			}catch(Exception xcep){
				JOptionPane.showMessageDialog(this,"ERROR Running Commodus!");
				System.out.println(xcep.toString());
			}
			System.out.println("commodus node: " + commodusNode.toString());
			System.out.flush();
			
			logicView.update(service, session);
			
			JFrame frame = new WdjetViewer(logicView.getLogic().getNode(commodusNode),service, session, commodusNode, "CommoText");
			
			this.setVisible(false);
		}
		
	}
	
	
	

}
