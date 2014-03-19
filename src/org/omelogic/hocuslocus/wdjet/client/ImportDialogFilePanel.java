
/*
 *      ImportDialogFilePanel.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */

package org.omelogic.hocuslocus.wdjet.client;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Component;
import java.io.File;
import javax.swing.BoxLayout;
import java.util.UUID;
import java.lang.Exception;

import org.omelogic.utils.locussetio.*;
import org.omelogic.utils.*;
public class ImportDialogFilePanel extends JPanel implements ImportDialogPanel{

	JFileChooser importFileChooser;
	JTextField importFileName;
	JTextArea importFileDescription;
	
	public ImportDialogFilePanel(){
	
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//acceptImportButton.addActionListener(this);
		JLabel chooseLabel = new JLabel("Choose File:", JLabel.LEFT);
		chooseLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chooseLabel);		
		importFileChooser = new JFileChooser();
		//importFileChooser.setPreferredSize(new Dimension(400,250));
		importFileChooser.setAcceptAllFileFilterUsed(false);
		importFileChooser.addChoosableFileFilter(new ImportLocusSetFileFilter(LocusSetIO.BED));
		importFileChooser.addChoosableFileFilter(new ImportLocusSetFileFilter(LocusSetIO.GFF));
		importFileChooser.addChoosableFileFilter(new ImportLocusSetFileFilter(LocusSetIO.GTF));
		importFileChooser.setControlButtonsAreShown(false);
		this.add(importFileChooser);
		JLabel nameLabel = new JLabel("Name:", JLabel.LEFT);
		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(nameLabel);
		importFileName = new JTextField();
		//importFileName.setPreferredSize(new Dimension(300,15));
		this.add(importFileName);
		JLabel descLabel = new JLabel("Description:", JLabel.LEFT);
		descLabel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(descLabel);
		importFileDescription = new JTextArea(3, 3);
		//importFileDescription.setPreferredSize(new Dimension(300,30));
		this.add(importFileDescription);
		
	}
	
	public void runImportProcess(WdjetService service, UUID session)
	{
		boolean compressed = true;
		File file = importFileChooser.getSelectedFile();
		byte[] data = null;
		try{
			data = Compressor.compress( file );
		}catch (Exception e){
			JOptionPane.showMessageDialog(this,"ERROR Compressing File for upload!");
			System.out.println(e.toString());
		}
		int format = ((ImportLocusSetFileFilter)importFileChooser.getFileFilter()).getFormat();
		String name = importFileName.getText();
		
		String desc = importFileDescription.getText();
		UUID newNode = null;
		try{
			newNode = service.importLocusSetFile(session, name, desc, data, format, compressed);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,"ERROR Importing Selected File!");
			System.out.println(e.toString());
		}
		System.out.println("uploaded file: " + newNode.toString());
		System.out.flush();
			
		
	}

}
