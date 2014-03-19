/*
 *      TomeTableModel.java
 *      
 *      Copyright 2007 Ajish George <ajish@hocuslocus.com>
 *      
 */
package org.omelogic.hocuslocus.wdjet.client;


import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.omelogic.locus.*;

public class LocusSetTableModel extends AbstractTableModel
{
	private LocusSet data;
	private ArrayList<String> colnames;
	
	public LocusSetTableModel( LocusSet inData)
	{
		data = inData;
		colnames = new ArrayList<String>();
		colnames.add("ID");
		colnames.add("Chromsome");
		colnames.add("Strand");
		colnames.add("Start");
		colnames.add("End");
		//for now just get string annos in first locus
		Locus locus1 = data.getLocusByIndex(0);
		Iterator<String> annoKeys = locus1.getAnnotationKeys();
		
		//add all key-value pairs from here
		while( annoKeys.hasNext() ){
			String iterKey = annoKeys.next();
			Object iterValObj = locus1.getAnnotation(iterKey);
			String iterVal;
			try {
				iterVal = (String) iterValObj;
				colnames.add(iterKey);
			}catch(Exception e){
				return;
			}
		}
	}
	
	public int getRowCount()
	{
		return data.getSize();
	}
	
	public int getColumnCount()
	{
		return colnames.size();
	}
	
	public String getColumnName( int column )
	{
		return colnames.get(column);
	}
	
	public Object getValueAt( int row, int column )
	{
		if (column == 0)
		{ //id
			return data.getLocusByIndex(row).getID();
		}
		if (column == 1)
		{ //chromosome
			return data.getLocusByIndex(row).getChromosome();
		}
		if (column == 2)
		{ //strand
			return data.getLocusByIndex(row).getStrandShortString();
		}
		if (column == 3)
		{ //start
			return data.getLocusByIndex(row).getStart();
		}
		if (column == 4)
		{ //end
			return data.getLocusByIndex(row).getEnd();
		}
		
		return data.getLocusByIndex(row).getAnnotation( colnames.get(column) );
	}

	public boolean isCellEditable(int row, int column )
	{
		return false;
	}
	

}
