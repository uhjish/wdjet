package org.omelogic.hocuslocus.wdjet.client;

/*
 *      WdjetService.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 *
 */


import org.omelogic.locus.*;
import org.omelogic.hocuslocus.logic.*;
import org.omelogic.hldbmanager.*;
import org.omelogic.interlocutor.*;

import java.rmi.*;
import java.util.*;

import prefuse.data.Graph;




public interface WdjetService extends java.rmi.Remote
{

	public UUID getNewLogic(String spass, String name, String description) throws Exception;
	public void saveLogic(UUID logicID) throws Exception;
	public OmeLogic[] getSavedLogics() throws RemoteException;
	public OmeLogic getLogic(UUID logicID) throws Exception;
	public UUID importLocusSetFile( UUID logicID, String name, String details, byte[] data, int format, boolean compressed ) throws RemoteException;
	public UUID importLocusSetSpasstic( UUID logicID, String name, String details, List<TableDescriptor> tables, Collection<String> ids) throws RemoteException;
	public void addGenePromoters( UUID session, UUID nodeID, int upstream, int downstream) throws RemoteException;
	public void removeOmeNode( UUID session, UUID nodeID) throws RemoteException;
	//public UUID uploadLocusSet (UUID logicID, LocusSet uploadSet, String displayName, String details, UUID[] parents) throws Exception;
	public LocusSet getLocusSet(UUID logicID, UUID setID) throws Exception;
	public DatabaseDescriptor[] getSpAssDescriptions() throws Exception;
	public TableDescriptor[] getTableDescriptors(String spass) throws Exception;
	public UUID runInterlocutor(UUID logic, UUID loci, String name, boolean parseQueryAnnos, boolean addTrackPrefix, List<TableDescriptor> tables, List<Locution> locutions, boolean controlIsPopulation, List<UUID> controls) throws Exception;
	public Object getOmeNodeData (UUID logic, UUID nodeID) throws Exception;
	public byte[] getCompressedOmeNodeData (UUID logic, UUID nodeID) throws Exception;
	//public void importSpassticLoci(/*args*/);
	//commodus
	//connexus
	//generate controls


}
