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

public class WdjetFileFilter extends FileFilter{

	public static String extension = "wdjet";
	private static String description = "OmeLogic Wdjet Session";

	public boolean accept(File f) {
		
		if (f.isDirectory()){
			return true;
		}
		
		if (extension.equals( getExtension(f) ))
		{
			return true;
		}
		
		return false;
	}
	
	/*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

	public String getDescription(){
		return description;
	}	
		
    
	
}
