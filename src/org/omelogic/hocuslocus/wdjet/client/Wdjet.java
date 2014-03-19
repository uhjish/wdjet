package org.omelogic.hocuslocus.wdjet.client;


import org.omelogic.hocuslocus.logic.*;

import java.awt.Dimension;

import javax.swing.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.visual.VisualItem;

import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.net.MalformedURLException; 
import java.rmi.NotBoundException; 
import java.util.UUID;


public class Wdjet {


	private static WdjetService service;
	private UUID session;
	//private WdjetService service;
	
	private static final String actionMenuIconImagePath = "resources/img/ActionMenu/menuItems/";

	public Wdjet() throws Exception
	{
		session = null;
		
		//show entry dialog
		WdjetConnectDialog connectDialog = new WdjetConnectDialog();
		String chosenHost = connectDialog.getHostChoice();
		
		String hostURL = "rmi://" + chosenHost + "/WdjetService";
		
		System.out.println(new String("Connecting: " + hostURL));
		System.out.flush();
		//choose remote or local
		// -- rmi://laskd.dkek.org
		//dummy with just local for now
		try 
		{
			service = (WdjetService) Naming.lookup(hostURL);
        } 
        catch (Exception excep) { 
        	System.out.println("ERROR Caught!");
        	System.out.flush();
        	String errMsg ="";
            if (excep instanceof MalformedURLException){
            	errMsg = "MarlformedURLException";
			}
            if (excep instanceof RemoteException){
            	errMsg = "RemoteException";
			}
            if (excep instanceof NotBoundException){
            	errMsg = "NotBoundException";
			}
			
			errMsg += ": " + excep.toString();
	
			JOptionPane.showMessageDialog(null, errMsg, "_. w d j e t | e R R o R ._",JOptionPane.ERROR_MESSAGE);
			
			throw new Exception("failed to init wdjet");

        } 

		JFrame frame = new JFrame("_. h o c u s l o c u s   |   w d j e t ._");
		//frame.setMinimumSize(new Dimension(800,800));
		//frame.setMaximumSize(new Dimension(800,800));
		frame.setResizable(false);
		//frame.setMaximizable(false);

		OmeLogicView logicPane = new OmeLogicView( );
		ActionMenu actionMenuPane = new ActionMenu(service, session, logicPane, actionMenuIconImagePath);
		
		//actionMenuPane.setSize(new Dimension(800,100));
		actionMenuPane.setPreferredSize(new Dimension(800,100));
		JSplitPane hsplitMain = new JSplitPane( JSplitPane.VERTICAL_SPLIT, logicPane, actionMenuPane);
		// create a new window to hold the visualization
		logicPane.setMinimumSize(new Dimension(800,700));
		actionMenuPane.setMinimumSize(new Dimension(800,100));
		frame.setLocation(100,100);
		// ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().add(hsplitMain);
		//frame.setUndecorated(true);
		//frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window
		


		
	}

    public static void main(String[] argv) {
        try 
        {
        	Wdjet mainWindow = new Wdjet();
		}catch(Exception e){
			main(argv);
		}
    }

    
}
