package org.omelogic.hocuslocus.wdjet.client;

/*
 *      OmeLogicView.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.AbstractAction;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.Schema;
import prefuse.data.tuple.*;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;


import java.util.UUID;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.omelogic.hocuslocus.logic.*;

public class OmeLogicView extends JSplitPane {

	//protected OmeLogic logic;

	protected OmeLogicGraphView graphView;
	protected OmeLogicListView listView;
	protected Graph omeLogicGraph;
	protected OmeLogic logic;
    /** The label data field for menu items. */
    public static final String LABEL = "label";
    /** The OmeNode field for graph items   */
    public static final String OMENODE = "omenode";
    /** The action data field for menu items. */
    public static final String ACTION = "action";
    public static final String SELECTED = "selected";
    
    /**
     * This schema holds the data representation for internal storage of
     * menu items.
     */
    protected static final Schema OMENODE_SCHEMA = new Schema();
    static {
        OMENODE_SCHEMA.addColumn(OMENODE, OmeNode.class);
        OMENODE_SCHEMA.addColumn(LABEL, String.class);
        OMENODE_SCHEMA.addColumn(ACTION, ActionListener.class);
        OMENODE_SCHEMA.addColumn(SELECTED, boolean.class);
    }


	public OmeLogicView(){
		super(JSplitPane.HORIZONTAL_SPLIT);
        UILib.setPlatformLookAndFeel();
        this.setPreferredSize(new Dimension(800,500));
        omeLogicGraph = makeOmeLogicGraph( new OmeLogic());
        graphView = new OmeLogicGraphView(omeLogicGraph, LABEL);
        listView = new OmeLogicListView(omeLogicGraph.getNodeTable(), LABEL);
		graphView.setPreferredSize(new Dimension(700,500));
		listView.setPreferredSize(new Dimension(100,500));
		
		this.setLeftComponent(graphView);
		this.setRightComponent(listView);
        this.setDividerLocation( (double)(7/8));
	}
	
	public void update( WdjetService service, UUID session )
	{

		logic = null;
		
		try{
			logic = service.getLogic(session);
		}catch(Exception e){
			System.out.println("Wdjet.ActionMenu.ERROR: Could not get session " + session.toString() + " from service!");
			e.printStackTrace(System.out);
		}	
		
		update(logic);

        this.setDividerLocation( 0.875);

		
	}
	
	public OmeLogic getLogic()
	{
		return logic;
	}

	private  void update( OmeLogic inLogic )
	{
		omeLogicGraph = makeOmeLogicGraph( inLogic );
		graphView.setGraph( omeLogicGraph , LABEL);
		listView.update( omeLogicGraph.getNodeTable() );
	}




    private Graph makeOmeLogicGraph( OmeLogic logic )
    {
    	final OmeLogicView logicView = this;
    	//make new directed graph
    	Graph graph = new Graph((Table)OMENODE_SCHEMA.instantiate(), true);	
    	if (logic == null)
    	{
    		return graph;
    	}
    	
    	HashMap<UUID, Node> nodeMap = new HashMap<UUID, Node>();
    	
    	//iterate through the nodes
    	Iterator<OmeNode> nodeIter = logic.getIterator();
    	
    	while( nodeIter.hasNext() )
    	{
    		OmeNode omeNode = (OmeNode)nodeIter.next();
    		UUID id = omeNode.getID();
			Node graphNode;
    		//if graphNode already exists in nodeMap
    		//update the data there
    		if (nodeMap.containsKey(id))
    		{
    			graphNode = (Node)nodeMap.get(id);
    		}else{
    			graphNode = graph.addNode();
    			nodeMap.put(id, graphNode);
    		}
    		
    		graphNode.set(OMENODE, omeNode);
    		graphNode.set(LABEL, omeNode.getDisplayName());
    		graphNode.set(ACTION, new AbstractAction() {
    			public void actionPerformed( ActionEvent e )
    			{
    				VisualItem visualNode = (VisualItem)e.getSource();
                    System.out.println("clicked item: "+
                        visualNode.get(LABEL));
                    System.out.flush();
                    
                    logicView.select( visualNode );
    			}
    		});
    		graphNode.set(SELECTED, false);
    		
    		//add to nodeMap
    		nodeMap.put(id, graphNode);
    		
    		//parse through parents and add links
    		List<UUID> parents = omeNode.getParents();
    		if (parents != null)
    		{
				for (int i = 0; i < parents.size(); i++)
				{
					Node curParent;
					//if parent node isn't already made
					//make a dummy one to be filled in when it's read
					if (nodeMap.containsKey(parents.get(i)))
					{
						curParent = (Node)nodeMap.get(parents.get(i));
					}else{
						curParent = graph.addNode();
						nodeMap.put( parents.get(i), curParent );
					}
					graph.addEdge( curParent, graphNode );
				}
    			
    		}
    		
    		
    	}
    	
    	return graph;
    	
    }
    
    public List<OmeNode> getSelectedNodes()
    {
    	ArrayList<OmeNode> selected = new ArrayList<OmeNode>();
    	Iterator<Node> nodeIter = omeLogicGraph.nodes();
    	System.out.println("getSelectedNodes(): " + omeLogicGraph.getNodeCount());
    	while (nodeIter.hasNext())
    	{
    		Node curNode = (Node)nodeIter.next();
    		System.out.println("Node: "+ curNode.getBoolean(SELECTED) + " --> " + curNode.get(OMENODE));
    		if ( curNode.getBoolean(SELECTED) )
    		{
    			selected.add( (OmeNode)curNode.get(OMENODE) );
			}
		}
		
		return selected;
    	
    	
	}
    
    
    
    private void select( VisualItem selected )
    {
    	
    	
    }


}
