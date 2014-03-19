package org.omelogic.hocuslocus.wdjet.client;
/*
 *      CommodusView.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
 *      
 */
import org.omelogic.locus.*;
import javax.swing.*;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;


public class LocusSetView extends JPanel
{
	private LocusSet data;
	private WdjetViewer parent;
	private WdjetService service;
	private UUID session;
	private UUID nodeID;

	public LocusSetView(WdjetViewer par, Object nodeData) throws Exception
	{
		super(new BorderLayout());
		parent = par;
		try
		{
			data = (LocusSet)nodeData;
		}catch(Exception xcep){
			throw new Exception("LocusSetView: Couldn't cast nodeData to LocusSet!\n"+xcep.toString());
		}
		
		JTable table = new JTable( );
		//table.setAutoCreateRowSorter(true);
		JScrollPane tableScroller = new JScrollPane(table);
		tableScroller.setPreferredSize(new Dimension(300,400));
		//this.add(new JLabel("Commodus Data:"));
		this.add(tableScroller, BorderLayout.CENTER);
		table.setModel(new LocusSetTableModel(data));
		
		if (data.getSize() > 0)
		{
			String type = data.getLocusByIndex(0).getType();
			System.out.println("Creating view for type: "+ type);
			if (type.equals(Locus.TYPE.GENE) || type.equals(Locus.TYPE.MRNA) || type.equals(Locus.TYPE.TRANSCRIPTIONAL_REGION))
			{
				this.add(geneToolsPanel(), BorderLayout.SOUTH);
			}
		}
		
		
	}

	private JPanel geneToolsPanel()
	{
	
		final JPanel geneInfoPanel = new JPanel();
		geneInfoPanel.setLayout(new BoxLayout(geneInfoPanel, BoxLayout.PAGE_AXIS));
		final JTextField upstreamBox = new JTextField(5);
		final JTextField downstreamBox = new JTextField(5);
		final JCheckBox addPromotersCheck = new JCheckBox("Add Promoters to Genes/mRNAs");
		addPromotersCheck.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if ( addPromotersCheck.isSelected() ){
					upstreamBox.setEditable(true);
					downstreamBox.setEditable(true);
				}else{
					upstreamBox.setEditable(false);
					downstreamBox.setEditable(false);
				}
			}
		});
		upstreamBox.setEditable(false);
		downstreamBox.setEditable(false);
		JButton update = new JButton("Update");
		geneInfoPanel.add(addPromotersCheck);
		geneInfoPanel.add(addPromotersCheck);
		geneInfoPanel.add(new JLabel("Upstream Bases:"));
		geneInfoPanel.add( upstreamBox );
		geneInfoPanel.add(new JLabel("Downstream Bases:"));
		geneInfoPanel.add( downstreamBox );
		geneInfoPanel.add(update);
		update.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{	
				boolean getGeneInfo = addPromotersCheck.isSelected();
				int upstream = 0;
				int downstream = 0;
				if (getGeneInfo){
					try{
						upstream = Integer.parseInt( upstreamBox.getText() );
					}catch(Exception xcep){
						JOptionPane.showMessageDialog(geneInfoPanel,"Ohmagod! Invalid upstream bases: "+ xcep.toString());
						return;
					}
					try{
						downstream = Integer.parseInt( downstreamBox.getText() );
					}catch(Exception xcep){
						JOptionPane.showMessageDialog(geneInfoPanel,"Ohmagod! Invalid downstream bases: "+ xcep.toString());
						return;
					}
					if (upstream < 0 || downstream < 0){
						JOptionPane.showMessageDialog(geneInfoPanel,"Both upstream and downstream must be positive integers, you fool!");
						return;
					}
					System.out.println("session:"+parent.session.toString() + "nodeID:"+parent.nodeID+"up:"+upstream+"dn:"+downstream);
					try{
						
						parent.service.addGenePromoters( parent.session, parent.nodeID, upstream, downstream );
					}catch(Exception xcep){
						JOptionPane.showMessageDialog(geneInfoPanel,"Failed to add promoters: "+xcep.toString());
						return;
					}
					
					parent.updateView();
					
				}
			}
		});
	
		return geneInfoPanel;
	}
}
