/*
 *      ImportFileFilterBED.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.hocuslocus.wdjet.client;

import org.omelogic.utils.locussetio.*;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class ImportLocusSetFileFilter extends FileFilter{

	private final int formatCode;
	
	public ImportLocusSetFileFilter( int argFormat )
	{
		formatCode = argFormat;
	}
	
	public boolean accept(File f) {
		
		if (f.isDirectory()){
			return true;
		}
		
		int fFormat = LocusSetIO.getTranslatorCode( f.getName() );
		
		if (fFormat == formatCode){
			return true;
		}
		
		return false;
	}
	
	public int getFormat(){
		return formatCode;
	}
	
	public String getDescription(){
		return LocusSetIO.FORMAT_DESCRIPTIONS[formatCode];
	}	
		
	
		
    
	
}
