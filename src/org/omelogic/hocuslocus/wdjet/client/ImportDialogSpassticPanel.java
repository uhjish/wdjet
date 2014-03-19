
/*
 *      ImportDialogSpassticPanel.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.hocuslocus.wdjet.client;

import javax.swing.*;
import java.util.*;
import org.omelogic.hldbmanager.*;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;

public class ImportDialogSpassticPanel extends JPanel implements ImportDialogPanel{


	private SpassticSelectionPanel spassSelector;
	private OmeLogicView logicView;	
	private WdjetService service;
	private UUID session;
	private JTextArea idListTextArea;
	private JTextField importSetName;
	private JTextArea importSetDescription;
	private JCheckBox getAllLociBox;
	
	
	public ImportDialogSpassticPanel(OmeLogicView inLogicView, WdjetService svc, UUID ssn){
	
		super();

		
		logicView = inLogicView;
		service = svc;
		session = ssn;
		String spass = logicView.getLogic().getSpass();
		//make overall container
		JPanel dialogContents = new JPanel();
		dialogContents.setLayout(new BoxLayout(dialogContents, BoxLayout.PAGE_AXIS));
		dialogContents.add( new JLabel("Import SpAssTIC: ") );
		//dialogContents.setPreferredSize(new Dimension(600,400));


		try
		{
			spassSelector = new SpassticSelectionPanel(svc, spass, true);
		}catch(Exception e){
			System.out.println(e.toString());
			this.setVisible(false);
		}

		//JFileChooser importFileChooser = new JFileChooser();
		
		

		idListTextArea = new JTextArea();
		JScrollPane idListScroller = new JScrollPane(idListTextArea);
        idListScroller.setPreferredSize(new Dimension(200,300));
        
		importSetName = new JTextField();
		importSetDescription = new JTextArea(3, 3);
        
		JPanel selectorPanel = new JPanel();
		selectorPanel.setLayout(new BoxLayout( selectorPanel, BoxLayout.PAGE_AXIS));
		selectorPanel.add(spassSelector);
		//selectorPanel.add(geneInfoPanel);
		
		JPanel importPanel = new JPanel();
		importPanel.setLayout(new BoxLayout(importPanel, BoxLayout.LINE_AXIS));
		importPanel.add(selectorPanel);
		importPanel.add(idListScroller);
		
		getAllLociBox = new JCheckBox("Get All Loci");
		getAllLociBox.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (getAllLociBox.isSelected()){
					idListTextArea.setEnabled(false);
				}else{
					idListTextArea.setEnabled(true);
				}
			
			}
		});
		importPanel.add(getAllLociBox);
		
		
		dialogContents.add(importPanel);
		dialogContents.add(new JLabel("Set Name: "));
		dialogContents.add(importSetName);
		dialogContents.add(new JLabel("Set Desc: "));
		dialogContents.add(importSetDescription);
		this.add(dialogContents);
		
	
	}
	
	public void runImportProcess(WdjetService service, UUID session)
	{
		//open up the file
		//parse it
		//add it to the session
		
		//gather all the necessary data and run commodus
		ArrayList<TableDescriptor> tables = spassSelector.getSelectedTables();
		if (tables.size() ==0){
			JOptionPane.showMessageDialog(this,"Pick at least one table to interlocute!");
			return;
		}
		String[] parsedList = (String[]) idListTextArea.getText().split("\n");	
		HashSet<String> idList = new HashSet<String>();
		String curStr;
		for (int i = 0; i < parsedList.length; i++){
			curStr = parsedList[i].replaceAll("\\s","");
			if (!curStr.equals("")){
				idList.add(curStr);
			}
		}
		
		if (getAllLociBox.isSelected()){
			idList = new HashSet<String>();
		}
		
		String name = importSetName.getText();
		String desc = importSetDescription.getText();
		

		
		//run Commodus
		UUID locusSetNode = null;
		try{
			locusSetNode = service.importLocusSetSpasstic(session, name, desc, tables, idList);
		}catch(Exception xcep){
			JOptionPane.showMessageDialog(this,"ERROR Running Commodus!");
			System.out.println(xcep.toString());
		}

		System.out.println("import from DB ...");
		System.out.flush();
			
		
	}


}
