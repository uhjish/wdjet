/*
 *      ActionMenu.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com> for HocusLocus
 *
 */
 
package org.omelogic.hocuslocus.wdjet.client;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.logging.*;
import java.util.*;
import java.lang.Enum;

import javax.swing.*;

import org.omelogic.hocuslocus.logic.*;



public class ActionMenu extends JPanel implements ActionListener{
    
    private String iconImagePath;
    private WdjetService service;
    private UUID session;
    private OmeLogicView view;

	private static int ACTION_BUTTON_EDGE_LENGTH = 85;
    
    public ActionMenu(WdjetService svc, UUID ssn, OmeLogicView logicView, String argIconImagePath) 
    {

        
        service = svc;
        session = ssn;
        view = logicView;
        
       	this.iconImagePath = argIconImagePath;
          
        /** Menu Items ordered from Right to Left*/
        JButton exitButton = new JButton(createImageIcon(iconImagePath+"exit.gif", "exit the wdjet"));
        //exitButton.setContentAreaFilled(false);
        exitButton.setBorder(null);
		exitButton.setActionCommand("exit");
		exitButton.addActionListener(this);
		
		JButton saveButton = new JButton(createImageIcon(iconImagePath+"save.gif", "save this logic"));
        saveButton.setBorder(null);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);

		JButton loadButton = new JButton(createImageIcon(iconImagePath+"load.gif", "load saved logic"));
        loadButton.setBorder(null);
		loadButton.setActionCommand("load");
		loadButton.addActionListener(this);
        
        JButton newButton = new JButton(createImageIcon(iconImagePath+"new.gif", "create new logic"));
        newButton.setBorder(null);
		newButton.setActionCommand("new");
		newButton.addActionListener(this);
        
        JButton deleteButton = new JButton(createImageIcon(iconImagePath+"delete.jpg","view selected item"));
        deleteButton.setBorder(null);
		deleteButton.setActionCommand("delete");
		deleteButton.addActionListener(this);

        JButton viewButton = new JButton(createImageIcon(iconImagePath+"view.gif","view selected item"));
        viewButton.setBorder(null);
		viewButton.setActionCommand("view");
		viewButton.addActionListener(this);
		
		
		JButton interlocutorButton = new JButton(createImageIcon(iconImagePath+"commodus.gif", "word enrichment analysis"));
        interlocutorButton.setBorder(null);
		interlocutorButton.setActionCommand("interlocutor");
		interlocutorButton.addActionListener(this);
		
        
        JButton liaButton = new JButton(createImageIcon(iconImagePath+"lia.gif", "locus intersection analysis"));
        liaButton.setBorder(null);
		liaButton.setActionCommand("lia");
		liaButton.addActionListener(this);
		
		JButton importButton = new JButton(createImageIcon(iconImagePath+"import.gif","import a locusset"));
        importButton.setBorder(null);
		importButton.setActionCommand("import");
		importButton.addActionListener(this);
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(importButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(liaButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(interlocutorButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(viewButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(deleteButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(newButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(saveButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(loadButton);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(exitButton);
		buttonPanel.add(Box.createGlue());

		JScrollPane buttonPanelScroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		buttonPanelScroller.getViewport().add(buttonPanel);
		this.add(buttonPanelScroller);
		
        
    }
    
    /**
     * Creates an ImageIcon if the path is valid.
     * @param String - resource path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path,
            String description) {
        //java.net.URL imgURL = getClass().getResource(path);
        //if (imgURL != null) {
            ImageIcon pre = new ImageIcon(path, description);
            //return pre;
            return new ImageIcon( getScaledImage(pre.getImage(), ACTION_BUTTON_EDGE_LENGTH,ACTION_BUTTON_EDGE_LENGTH), description );
        //} else {
        //    System.err.println("Couldn't find file: " + path);
        //    return null;
        //}
    }
    
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    // ------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		
		String action = e.getActionCommand();
		
		System.out.println("clicked item: "+
			action);
		System.out.flush();

		if ( action.equals("exit") )
		{
			/** Prepare to exit, then do it.*/
			
			System.exit(0);
		}	
    
		if ( action.equals("save") )
		{
		
			if (session == null)
			{
				JOptionPane.showMessageDialog(view,"Session is not defined. Create or load a session first.");
				return;
			}
			/** Show Save Dialog */
			try
			{
				service.saveLogic(session);
			}catch(Exception xcep){
				System.out.println("Failed to save: " +xcep.toString());
			}
		
		}
			
		if ( action.equals("load") )
		{
			/** Show Load Dialog */
			LoadDialog loadSessionDialog = new LoadDialog(view, service, session);
			UUID newSession = loadSessionDialog.loadSession();
			if (newSession != null)
			{
				session = newSession;
				System.out.println("loaded session: "+ session.toString());
				view.update(service, session);
			}					
		
		}	

		if ( action.equals("new") )
		{
			/** Show New Dialog */
			
			NewDialog newSessionDialog = new NewDialog(view, service, session);
			UUID newSession = newSessionDialog.getNewSession();
			if (newSession != null)
			{
				session = newSession;
				System.out.println("new session: "+ session.toString());
				view.update(service, session);
			}
		
		
		}	
		
		if ( action.equals("delete") )
		{
			if (session == null)
			{
				JOptionPane.showMessageDialog(view,"Session is not defined. Create or load a session first.");
				return;
			}

			java.util.List<OmeNode> selected = view.getSelectedNodes();
			if (selected.size() != 1)
			{//ensure only one node selected
				System.out.println(selected);
				JOptionPane.showMessageDialog(view,"Select only one OmeNode!");
				return;
			}					
			UUID selID = ((OmeNode)selected.get(0)).getID();
			System.out.println("removing: "+ selID + " from " + session);
			try{
				service.removeOmeNode( session, selID );
			}catch(Exception excep){
				JOptionPane.showMessageDialog(view,excep.toString());
				excep.printStackTrace(System.out);
			}
				
			view.update(service, session);
		
		}	

		if ( action.equals("view") )
		{
			if (session == null)
			{
				JOptionPane.showMessageDialog(view,"Session is not defined. Create or load a session first.");
				return;
			}
			java.util.List<OmeNode> selected = view.getSelectedNodes();
			if (selected.size() != 1)
			{//ensure only one node selected
				System.out.println(selected);
				JOptionPane.showMessageDialog(view,"Select only one OmeNode!");
				return;
			}					
			OmeNode selNode = selected.get(0);
			JFrame frame = new WdjetViewer(selNode, service, session, selNode.getID(), "Viewey");
		
		}	

		if ( action.equals("interlocutor") ){
		
			if (session == null)
			{//ensure valid session
				JOptionPane.showMessageDialog(view,"Session is not defined. Create or load a session first.");
				return;
			}
			java.util.List<OmeNode> selected = view.getSelectedNodes();
			if (selected.size() != 1)
			{//ensure only one node selected
				System.out.println(selected);
				JOptionPane.showMessageDialog(view,"Select only one OmeNode!");
				return;
			}					
			OmeNode selNode = selected.get(0);
			if (selNode.getType() != OmeNode.TYPES.LOCUS_SET)
			{//ensure selected node is a LocusSet
				JOptionPane.showMessageDialog(view,"interlocutor: Selected node must be a LocusSet!");
				return;
			}	
				
			/** Show Commodus Dialog */
			InterlocutorDialog importer = new InterlocutorDialog(view, service, session, selNode);
			view.update(service, session);      					
		}	
		
		if (action.equals("lia")){
			if (session == null)
			{
				JOptionPane.showMessageDialog(view,"Session is not defined. Create or load a session first.");
				return;
			}
			/** Show LIA Dialog */
		
		}
		
		if (action.equals("import")){
			if (session == null)
			{
				JOptionPane.showMessageDialog(view, "Session is not defined. Create or load a session first.");
				return;
			}
			
			/** Show Import Dialog */
			
			ImportDialog importer = new ImportDialog(view, service, session);
			view.update(service, session);
			
		}
    }
} // end of class ActionMenu
