package org.omelogic.hocuslocus.wdjet.client;
/*
 *      WdjetViever.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */

import org.omelogic.hocuslocus.logic.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.UUID;

import org.omelogic.utils.Compressor;

public class WdjetViewer extends JFrame
{

	protected WdjetService service;
	protected UUID session;
	protected UUID nodeID;
	protected OmeNode omeNode;
	protected Object nodeData;

 	public WdjetViewer(OmeNode node, WdjetService svc, UUID ssn, UUID id, String title)
 	{ 
		service = svc;
		session = ssn;
		nodeID = id;
		omeNode = node;
		//this.setUndecorated(true);
		//this.setLocationRelativeTo(owner);
		this.setLocation(150,150);
		
		updateView();
		
 	}
 	
 	protected void updateView()
 	{
 		this.getContentPane().removeAll();
		JPanel contents = new JPanel( new BorderLayout());
		//contents.add(new JLabel("where's the beef?"));
		contents.add( fetchView() , BorderLayout.CENTER);
		this.getContentPane().add(contents);
		this.pack();
		this.setVisible(true);
	}
 	
 	private JPanel fetchView()
 	{
 		/*byte[] nodeDataBytes = new byte[0];
		try{
			nodeDataBytes = service.getCompressedOmeNodeData(session, nodeID);
		}catch(Exception e){
			//System.out.println("Wdjet.ActionMenu.ERROR: Could not get session " + nodeID.toString() + " from service!" + e.toString());
			JOptionPane.showMessageDialog(null, "Could not get omeNodeData!"+e.toString(), "Error",JOptionPane.ERROR_MESSAGE);				
			System.out.println(e.toString());
			e.printStackTrace(System.out);
			//System.out.flush();
		}	
		*/
		try{
			nodeData = service.getOmeNodeData(session, nodeID);
			//nodeData = Compressor.decompressObject( nodeDataBytes );
		}catch(Exception e){
			//System.out.println("Wdjet.ActionMenu.ERROR: Could not get session " + nodeID.toString() + " from service!" + e.toString());
			JOptionPane.showMessageDialog(null, "Could not cast bytes to object!"+e.toString(), "Error",JOptionPane.ERROR_MESSAGE);				
			e.printStackTrace(System.out);
			//System.out.flush();
		}	
		
		int nodeType = omeNode.getType();
		String parsedType = "unknown";		
		try
		{
			if (nodeType == OmeNode.TYPES.LOCUS_SET)
			{
				parsedType = "LocusSet";
				return new LocusSetView(this, nodeData);
			}			
			if (nodeType == OmeNode.TYPES.INTERLOCUTOR)
			{
				parsedType = "Interlocution";
				return new InterlocutionViewChooser(nodeData);
			}
			
		}catch(Exception e){
			System.out.println("Failed to make WdjetViewer for " + parsedType + " - " + nodeType + nodeData.toString() + e.toString());
		}
 		
 		return null;
	}


}
