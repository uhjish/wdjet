package org.omelogic.hocuslocus.logic.HLDBTableSummary;

import java.lang.String;
import java.lang.Object;
import java.lang.Integer;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;


public class HLDBTableSummary
{
	String tableName;
	String tableDescription;
	String [] typesInTable;

	public HLDBTableSummary(){
		tableName = null;
		tableDescription = null;
		typesInTable = null;
	}

	public HLDBTableSummary(String name, String desc, String[] argTypes){
		tableName = name;
		tableDescription = desc;
		typesInTable = new String[argTypes.length + 1];
		typesInTable[0] = "ALL";
		for (int i = 1; i < argTypes.length; i++)
		{
			typesInTable[i] = argTypes[i];
		}
		
	}

	public String getName(){
		return this.tableName;
	}

	public String getDescription(){
		return this.tableDescription;
	}

	public String[] getTypes(){
		return typesInTable;
	}

	
}
