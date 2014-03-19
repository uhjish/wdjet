/*
 *      WdjetService.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.omelogic.hocuslocus.wdjet.server;

import org.omelogic.hocuslocus.wdjet.client.WdjetService;
import org.omelogic.hldbmanager.*;
import org.omelogic.interlocutor.*;
import org.omelogic.interlocutor.data.*;

import org.omelogic.locus.*;
import org.omelogic.locus.gene.*;
import org.omelogic.hocuslocus.logic.*;
import org.omelogic.utils.*;
import org.omelogic.utils.locussetio.*;

import prefuse.data.Graph;

import java.sql.SQLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.UUID;
import java.io.*;
import java.util.*;

public class WdjetServiceServer  extends UnicastRemoteObject implements WdjetService{

	//private static final String spassticConnection = "sunfire1.eastcampus.albany.edu";
	private HLDBManager spasstic;
	private String spassticHost;
	private String spassticUserName;
	private String spassticPassword;
	//private String currentDB;
	private static String saveDirectory = "save/";
	private PrintWriter debug;
	private static int port = 42024;
	
	/*public class WdjetServiceServerException extends Exception
	{
		public WdjetServiceServerException( String trace ){
			super( "WdjetServiceServerException: "+ trace);
		}
	}*/
	
	private HashMap<UUID, Object> wdjetItemStack;
	
	public WdjetServiceServer() throws Exception
	{
		super(42024);
		wdjetItemStack = new HashMap<UUID, Object>();
		spasstic = new HLDBManager();
		//connect();	
		debugOut();
	
	}
	
	private void debugOut() throws Exception
	{
		File outFile;

		outFile = new File("wdjet.err");
		if (! outFile.exists() || (outFile.isFile() && outFile.canWrite())) {
		   debug = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
		}
		else {
		   System.err.println("ERROR: Cannot write to file output.text." );
		}
	}
	
	public void connect() throws RemoteException
	{
		spasstic = new HLDBManager();
		spassticHost = "encoden.cnse.albany.edu";
		//spassticHost = "localhost";
		spassticUserName = "spasstic";
		spassticPassword = "sp@55t1k";
		try{
			spasstic.connect(spassticHost, spassticUserName, spassticPassword);
		}catch(Exception e){
				String msg = "Error connecting to SpassticHost!\n"+e.toString();
				debug.println(msg);
				throw new RemoteException(msg);
		}
	}
		
	
	private void verifySpassticConnection() throws RemoteException
	{
		//if (spasstic.isConnected() == false)
		//{
			connect();
		//}
		
	}
	
	private void setCurrentDatabase(String currentDB) throws RemoteException
	{
		verifySpassticConnection();
		try{
			spasstic.setCurrentDatabase(currentDB);
		}catch(Exception e){
			String msg = "Couldn't set currentDB to: " + currentDB + "\n" + e.toString();
			debug.println(msg);
			throw new RemoteException(msg);
		}
	}

	private void verifyLogicLoaded(UUID logicID) throws RemoteException
	{
		this.getLogic( logicID );
	}		

	/*
	public static void main ( String args[] ) throws RemoteException
    {
    	// Assign a security manager, in the event that dynamic
    	// classes are loaded
    	if (System.getSecurityManager() == null)
    		System.setSecurityManager ( new RMISecurityManager() );

    	// Create an instance of our power service server ...
    	WdjetServiceServer svr = new WdjetServiceServer();

    	// ... and bind it with the RMI Registry
    	try{
    		Naming.bind ("WdjetService", svr);
    	}catch(Exception e){
    		debug.println("ERROR:" + e.toString());
    	}
    	debug.println ("Service bound....");
    }
	*/
	
	public UUID runInterlocutor(UUID logicID, UUID loci, String name, boolean parseQueryAnnos, boolean addTrackPrefix, List<TableDescriptor> tables, List<Locution> locutions, boolean controlIsPopulation, List<UUID> controls) throws Exception
	{
		verifySpassticConnection();
		LocusSet qLoci = (LocusSet)getWdjetItem( loci );
		OmeLogic logic = (OmeLogic) getWdjetItem(logicID);
		setCurrentDatabase(logic.getSpass());
		ArrayList<LocusSet> ctrlLoci = new ArrayList<LocusSet>();
		String ctrlsDesc = "[ ";
		for (int i = 0; i < controls.size(); i++){
			ctrlLoci.add( (LocusSet)getWdjetItem( controls.get(i) ) );
			ctrlsDesc += logic.getNode(controls.get(i)).getDisplayName() + ";";
			
		}
		ctrlsDesc += " ]";
		//this.setCurrentDatabase( logic.getSpass() );
		Interlocutor myInterlocutor = null;
		try{
			myInterlocutor = new Interlocutor(qLoci, spasstic, parseQueryAnnos, addTrackPrefix);
		}catch(Exception excep){
			throw new RemoteException("WdjetServiceServerERROR: Could not create interLocutor instance " + excep.toString()); 
		}
		String statsDesc = "Interlocutor on " + logic.getNode(loci).getDisplayName() + " controls = ";

		if (ctrlLoci.size() > 0){
			if (controlIsPopulation){
				statsDesc +=  " population: ";
				if (ctrlLoci.size() > 1){
					throw new RemoteException("WdjetServiceServerERROR: controlIsPopulation - only one population LocusSet allowed!"); 
				}
				myInterlocutor.setPopulationControl(ctrlLoci.get(0) );
			}else{
				statsDesc +=  " sampling: ";
				myInterlocutor.setSamplingControls(ctrlLoci );
			}
		}
		
		statsDesc += ctrlsDesc;

		String tablesDesc = "[ ";
		for (int i = 0; i < tables.size(); i++){
			tablesDesc += tables.get(i).getFriendlyName()+";";
		}
		tablesDesc += " ]";
		try{
			myInterlocutor.interLocute( tables , locutions );
		}catch(Exception excep){
			throw new RemoteException("WdjetServiceServerERROR: Could not run interLocution " + excep.toString()); 
		}

		UUID nodeID = UUID.randomUUID();
		
		try 
		{
			List<UUID> parents = new ArrayList<UUID>();
			parents.add(loci);
			String parentName = logic.getNode(loci).getDisplayName();
			String display = name;
			String details = "Interlocutor on "+parentName + " vs " + tablesDesc + statsDesc;
			OmeNode newNode = new OmeNode(OmeNode.TYPES.INTERLOCUTOR, nodeID, display, details, parents, true);
			((OmeLogic)getWdjetItem(logicID)).addNode(newNode);
			Interlocution locuData = myInterlocutor.getResult();
			if (locuData == null)
			{
				throw new RemoteException("Interlocutor returned data is NULL!");
			}
			setWdjetItem(nodeID, locuData);
			
		}
		catch (Exception e)
		{
			throw new RemoteException("WdjetServiceServerERROR: "+ e.toString());
		}
		
		
		return nodeID;
		
	}
	
	public Object getOmeNodeData( UUID logic, UUID id ) throws RemoteException
	{
		if (!wdjetItemStack.containsKey(logic)){
			loadLogic(logic);
		}
		
		Object item = getWdjetItem(id);
		if (item == null){
			throw new RemoteException("Item at id: "+id.toString()+ " -- cannot get data!");
		}
	 	return item;
	}

	public byte[] getCompressedOmeNodeData( UUID logic, UUID id ) throws RemoteException
	{
		if (!wdjetItemStack.containsKey(logic)){
			loadLogic(logic);
		}
		
		Object item = getWdjetItem(id);
		if (item == null){
			throw new RemoteException("Item at id: "+id.toString()+ " -- cannot get data!");
		}
		byte[] itemBytes;
		try{
			 itemBytes = Compressor.compressObject(item);
		 }catch( Exception e){
		 	throw new RemoteException("WdjetServiceServer: error making compressed bytes! "+e.toString());
		}
	 	return itemBytes;
	}
	
	public void removeOmeNode( UUID logic, UUID id ) throws RemoteException
	{
		OmeLogic omelogic = (OmeLogic)wdjetItemStack.get(logic);
		
		if (omelogic.hasChildren(id))
		{
			throw( new RemoteException("Error removing OmeNode - is not a leaf!"));
		}
		
		omelogic.removeNode( id );
		wdjetItemStack.remove(id);
	}


	public DatabaseDescriptor[] getSpAssDescriptions()  throws RemoteException
	{

		this.verifySpassticConnection();
		DatabaseDescriptor[] DDs =  null;

		try
		{
			DDs = spasstic.getDatabases();
		}catch(Exception e){
			String msg = "WdjetServiceServer.getSpAssDescriptions() failed at:\n" + e.toString();
			debug.println(msg);
			throw new RemoteException(msg);
		}
		
		return DDs;
		
	}
	
	public TableDescriptor[] getTableDescriptors(String spassDB) throws Exception
	{
		this.setCurrentDatabase(spassDB);

		TableDescriptor[] TDs = null;
		try
		{
			TDs = spasstic.getTables();
		}catch(Exception e){
			String msg = "WdjetServiceServer.getTableDescriptors() failed at:\n" + e.toString();
			debug.println(msg);
			throw new RemoteException(msg);
		}
	
		return TDs;

	}
	
	
	private void setWdjetItem(UUID id, Object obj)throws RemoteException{
		wdjetItemStack.put(id, obj);
	}
	
	private Object getWdjetItem(UUID id) throws RemoteException{
		return wdjetItemStack.get(id);
	}
	
	public UUID getNewLogic(String spass, String name, String description) throws RemoteException
	{
		
		UUID id = UUID.randomUUID();
		OmeLogic newLogic = new OmeLogic();
		if( name == null ){
			name = "<undefined>";
		}
		newLogic.setSpass( spass );
		newLogic.setID( id );
		newLogic.setDisplayName( name );
		newLogic.setDescription( description );

		setWdjetItem(id, newLogic);		
		return id;
	}
	
	public void saveLogic(UUID logicID) throws RemoteException{
		//write out to disk or something
		FileOutputStream os;
		ObjectOutputStream s;
		OmeLogic logicToSave = (OmeLogic)getWdjetItem(logicID);
		String saveFile = saveDirectory + logicID.toString()+".ome";
		try {
			os = new FileOutputStream(saveFile);
			s = new ObjectOutputStream(os);
		} catch ( Exception e ) {
			throw new RemoteException("WdjetServiceServerERROR: Unable to open stream as an object stream!");
		}
		try {
			s.writeObject(logicToSave);
		} catch ( Exception e ) {
			throw new RemoteException("WdjetServiceServerERROR: Unable to write logic object to file!");
		}
		Iterator<OmeNode> nodesToSave = logicToSave.getIterator();
		UUID nodeID;
		Object nodeObject;
		while (nodesToSave.hasNext())
		{
			nodeID = ((OmeNode)nodesToSave.next()).getID();
			nodeObject = getWdjetItem(nodeID);
			try {
				s.writeObject(nodeObject);
			} catch ( Exception e ) {
				throw new RemoteException("WdjetServiceServerERROR: Unable to write logic object to file: "+nodeID.toString());
			}

		}
		try{
			s.close();
			os.close();
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Error closing streams? in saveLogic()" );
		}
	}
	
	public OmeLogic[] getSavedLogics() throws RemoteException
	{
		File dir = new File(saveDirectory);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".ome");
			}
		};
		String [] children;
		try{
    		children = dir.list(filter);
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Could not access server save directory - " + saveDirectory);
		}
		OmeLogic[] logics = new OmeLogic[children.length];
		FileInputStream f = null;
		ObjectInputStream s = null;
		String fname;
		UUID lname;
		for (int i = 0; i < children.length;i++)
		{
			fname = saveDirectory+children[i];
			lname = UUID.fromString(children[i].substring(0,children[i].length()-5));
			
			try{
				f= new FileInputStream(fname);
				s= new ObjectInputStream(f);
			}catch(Exception e){
				throw new RemoteException("WdjetServiceServerERROR: Could not open file for input - "+ fname);
			}
			try{
				logics[i] = (OmeLogic)s.readObject();
			}catch(Exception e){
				throw new RemoteException("WdjetServiceServerERROR: Could not open file for input - "+ children[i]);
			}
		}
		try{
			s.close();
			f.close();
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Error closing streams? in getSavedWdjets()" );
		}
		return logics;
	}
	
	public OmeLogic loadLogic(UUID logicID) throws RemoteException
	{
		OmeLogic returnLogic;
		FileInputStream f;
		ObjectInputStream s;
		String filename = saveDirectory+logicID+".ome";
		try{
			f= new FileInputStream(filename);
			s= new ObjectInputStream(f);
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Could not open file for input - "+ filename);
		}
		try{
			returnLogic = (OmeLogic)s.readObject();
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Could not read omeLogic from file - "+ filename);
		}
		setWdjetItem(logicID, returnLogic);
		Iterator<OmeNode> nodeIter = returnLogic.getIterator();
		UUID curNodeID;
		Object nodeObject;
		while ( nodeIter.hasNext() )
		{
			curNodeID = ((OmeNode) nodeIter.next()).getID();
			try{
				nodeObject = s.readObject();
			}catch(Exception e){
				throw new RemoteException("WdjetServiceServerERROR: Could not read omeNode Data from file - "+ curNodeID);
			}		
			if (wdjetItemStack.containsKey(curNodeID)){
				throw new RemoteException("WdjetServiceServerERROR: Data node ID conflicts with existing node!!!" + curNodeID);
			}
			setWdjetItem( curNodeID, nodeObject );
		}
		try{
			s.close();
			f.close();
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Error closing streams? in loadLogic()" );
		}
		
		setCurrentDatabase( returnLogic.getSpass() );
		
		return returnLogic;
	}
	
	public OmeLogic getLogic(UUID logicID) throws RemoteException{
	
		//OmeLogic returnLogic;
		
		if (wdjetItemStack.containsKey(logicID))
		{
			//logic is already loaded
			try{
				return (OmeLogic) getWdjetItem( logicID );
			}catch( Exception e ){
				throw new RemoteException("WdjetServiceServerException: logicID target is null or can't be cast as org.omelogic.hocuslocus.logic.OmeLogic!" + e.toString());
			}

		}else{

			//try to load it from file
			return	loadLogic( logicID );
		}
	}		
	
	public UUID importLocusSetFile(UUID logicID, String display, String details, byte[] data, int format, boolean compressed) throws RemoteException
	{
		InputStream input;
		LocusSet newLoci;
		UUID nodeID = UUID.randomUUID();

		try{
			if (compressed == true)
			{
				input = Compressor.decompress( data );
			}else{
				input = Compressor.bytesToInputStream( data );
			}
			
			debug.println("ready to parseloci -- format = " + format);
			
			newLoci = LocusSetIO.readLocusSet( format , input, nodeID.toString());
			
			details = "Name: "+ newLoci.getName() + "\nSize: " + newLoci.getSize() + "\nID: " + nodeID.toString() + "\nDetails: " + details;
	
		}catch (Exception e){
			//debug.println("UhOH!!!\n"+e.toString());
			throw new RemoteException("WdjetServiceServerERROR: Cannot make locus set! " + e.toString());
		}
		
		if (display.equals("")||display == null)
		{
			display = "<"+nodeID.toString() + ">";
		}
		try 
		{
			OmeNode newNode = new OmeNode(OmeNode.TYPES.LOCUS_SET, nodeID, display, details, null, true);
			((OmeLogic)getWdjetItem(logicID)).addNode(newNode);
			setWdjetItem(nodeID, newLoci);
			
		}
		catch (Exception e)
		{
			throw new RemoteException("WdjetServiceServerERROR: "+ e.toString());
		}
		
		
		return nodeID;
	}	
	
	public void addGenePromoters( UUID session, UUID nodeID, int upstream, int downstream) throws RemoteException
	{
		LocusSet newLoci = (LocusSet)getWdjetItem(nodeID);
		
		try{
			newLoci = GeneIE.addPromoters( newLoci, upstream, downstream );
		}catch(Exception e){
			throw new RemoteException("WdjetServiceServerERROR: Problem adding promoters."+ e.toString());
		}	
		
		setWdjetItem( nodeID, newLoci );
		OmeNode setNode = (OmeNode)((OmeLogic)getWdjetItem(session)).getNode(nodeID);
		setNode.setDetails(setNode.getDetails()+"\nAdded Promoters -- up: "+ upstream +" dn: "+downstream+".");
		return;
	}	
	
	
	public UUID importLocusSetSpasstic(UUID logicID, String display, String details, List<TableDescriptor> tables, Collection<String> ids) throws RemoteException
	{
		verifySpassticConnection();
		setCurrentDatabase( ((OmeLogic)getWdjetItem(logicID)).getSpass() );

		UUID nodeID = UUID.randomUUID();
		LocusSet newLoci = new LocusSet(nodeID.toString());

		LocusSet curSet;
		try{ 	
			for ( int i = 0; i < tables.size(); i++){
				if ( ids.size() == 0 ){
					curSet = spasstic.getCompleteLoci(tables.get(i));
				}else{
					curSet = spasstic.getCompleteLoci(tables.get(i), ids);
				}
				newLoci.appendSet(curSet);
			}
		}
		catch (Exception e)
		{
			throw new RemoteException("WdjetServiceServerERROR: "+ e.toString());
		}
		
		
		if (display.equals("")||display == null)
		{
			display = "<"+nodeID.toString() + ">";
		}
		try 
		{
			OmeNode newNode = new OmeNode(OmeNode.TYPES.LOCUS_SET, nodeID, display, details, null, true);
			((OmeLogic)getWdjetItem(logicID)).addNode(newNode);
			setWdjetItem(nodeID, newLoci);
			
		}
		catch (Exception e)
		{
			throw new RemoteException("WdjetServiceServerERROR: "+ e.toString());
		}
		
		
		return nodeID;
	}	

	public LocusSet getLocusSet(UUID logicID, UUID setID) throws java.rmi.RemoteException{
		
		try{
			LocusSet retLoci = (LocusSet) getWdjetItem(setID);
			return retLoci;
		}catch (Exception e){
			throw new java.rmi.RemoteException("WdjetServiceServerException: setID target is null or can't be cast as org.omelogic.locus.Locus!"+e.toString());
		}
	}
	
	//public void importSpassticLoci(/*args*/);
	//Interlocutor
	//connexus
	//generate controls


}
