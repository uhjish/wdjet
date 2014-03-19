/*
 *      OmeNode.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.hocuslocus.logic;

import java.io.Serializable;
import java.util.*;


public class OmeNode implements Serializable{


	public final class TYPES
	{

		private TYPES(){}
		/**
		 * Undefined
		 */
		public static final int UNDEFINED = 0;
		/**
		Locus Set
		*/
		public static final int LOCUS_SET = 1;
		/**
		LIA instance
		*/
		public static final int LIA_PROCESS = 2;
		/**
		InterLocutor instance
		*/
		public static final int INTERLOCUTOR = 3;
		

	}

	//private String spass;

	private int type;
	private UUID id;
	private String display;
	private String details;

	private List<UUID> parents;

	private boolean selected;
	private boolean hasData;

	public OmeNode(){

		this.type = TYPES.UNDEFINED;
		this.id = null;
		this.display = "<undefinedID>";
		this.details = "";
		this.parents = new ArrayList<UUID>();

	}
	
	public OmeNode(  int typ, UUID nodeID, String display,  String detls, List<UUID> prnts, boolean hasDat){

		this.type = typ;
		this.id = nodeID;
		this.display = display;
		this.details = detls;
		if (prnts == null){
			prnts = new ArrayList<UUID>();
		}
		this.parents = prnts;
		this.hasData = hasDat;

	}

	public boolean isDataNode(){
		return hasData;
	}

	public UUID getID(){
		return id;
	}
	public String getDisplayName(){
		return display;
	}
	public String toString(){
		return display;
	}
	public int getType(){
		return this.type;
	}
	public String getDetails(){
		return this.details;
	}
	
	public List<UUID> getParents(){
		return this.parents;
	}
	
	public boolean getSelected(){
		return this.selected;
	}
	
	public String getTypeString(){
		switch (this.type)
		{
			case OmeNode.TYPES.LOCUS_SET:
				return "LocusSet";

			case OmeNode.TYPES.LIA_PROCESS:
				return "LIA Intersections";

			default: 
				return "UNDEFINED";
		}
	}

	/*public void setType( int typ ){
		this.type = typ;
	}*/
	public void setDisplay( String displayName ){
		this.display = displayName;
	}
	
	public void setDetails( String newDetails ){
		this.details = newDetails;
	}
	public void setSelected(boolean setting){
		this.selected = setting;
	}
}


