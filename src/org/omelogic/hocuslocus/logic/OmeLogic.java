package org.omelogic.hocuslocus.logic;

import java.io.Serializable;
import java.lang.*;
import java.util.*;


/**
OmeLogic: tracks logic flow and data holders for the current analysis
public class OmeLogic{

*/


public class OmeLogic implements Serializable{

	private HashMap<UUID, OmeNode> nodeList;

	private UUID id;
	private String spass;
	private String displayName;
	private String description;

	public OmeLogic(){
	
		nodeList = new HashMap();
		spass = null;
		id = null;
		description = null;
		//spass = null;

	}
	/*
	public OmeLogic( String myID, String myDispName, String myDesc )
	{
		this.id = myID
		this.displayName = myDispName;
		this.description = myDesc;
		nodeList = new HashMap();
	}
	*/
	
	public boolean hasChildren( UUID id )
	{
		//boolean isLeaf = true;
		Iterator iter = getIterator();
		OmeNode curNode;
		while( iter.hasNext() ){
			curNode = (OmeNode)iter.next();
			if (curNode.getParents().contains(id))
			{
				return true;
			}
		}

		return false;
		
	}
	
	public void removeNode( UUID id )
	{
		Iterator iter = getIterator();
		OmeNode curNode;
		while( iter.hasNext() )
		{
			curNode = (OmeNode)iter.next();
			if (curNode.getParents().contains(id))
			{
				curNode.getParents().remove(id);
			}
		}
		
		nodeList.remove(id);
	}		
	
	public HashMap getNodeList()
	{
		return nodeList;
	}
	
	public void setNodeList(HashMap list)
	{
		nodeList = list;
	}
	public String getSpass(){
		return this.spass;
	}
	
	public UUID getID(){
		return this.id;
	}
	
	public String getDisplayName(){
		return this.displayName;
	}
	
	public String toString(){
		return this.displayName;
	}
	
	public String getDescription(){
		return this.description;
	}
		
	public void setSpass( String newSpass )
	{
		this.spass = newSpass;
	}
	
	public void setID( UUID myID ){
		this.id = myID;
	}
	
	public void setDisplayName(String newName){
		this.displayName = newName;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	

	public void addNode(OmeNode newNode){

		UUID id = newNode.getID();
		nodeList.put( id, newNode);

	}
	
	public OmeNode getNode( UUID hashKey ){

		return (OmeNode)(nodeList.get( hashKey ));

	}
	
	public void clearSelections(){
		
		Iterator nodeIter = this.getIterator();
		
		while (nodeIter.hasNext())
		{
			((OmeNode)nodeIter.next()).setSelected(false);
		}

	}	
	
	
	public List<OmeNode> getNodesByType( int type ){
		Iterator iter = getIterator();
		int numNodes = 0;
		ArrayList<OmeNode> nodes = new ArrayList<OmeNode>();
		OmeNode curNode;
		while( iter.hasNext() ){
			curNode = (OmeNode)iter.next();
			if (curNode.getType() == type)
			{
				nodes.add(curNode);
			}
		}

		return nodes;
	}
			

 /**
   * node Iterator -- Iterator<OmeNode>
   * 
   * @gwt.typeArgs <org.OmeLogic.hocusLocus.client.OmeNode>
   */
	public Iterator getIterator(){
		return nodeList.values().iterator();
	}

	public String[] getAllKeys(){
		
		Set kSet = nodeList.keySet();
		String[] keyArr = new String[kSet.size()];
		int i=0;
		Iterator iter = kSet.iterator();
		while (iter.hasNext())
		{
			keyArr[i] = (String)iter.next();
			i++;
		}
		
		
		return keyArr;

	}

	public int getNumNodes(){
		return nodeList.size();
	}

	
}
