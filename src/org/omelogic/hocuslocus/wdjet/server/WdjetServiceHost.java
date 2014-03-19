/*
 *      WdjetServiceHost.java
 *
 *      Copyright 2007 Ajish D. George <ajishg@gmail.com>
 */
package org.omelogic.hocuslocus.wdjet.server;

import java.rmi.*;
import java.rmi.server.*;


public class WdjetServiceHost {

	public WdjetServiceHost()
	{
		try
		{
	//	  System.setSecurityManager (new RMISecurityManager() {
	//		public void checkConnect (String host, int port) {}
	//		public void checkConnect (String host, int port, Object context) {}
	//	  });
		/*	if (System.getSecurityManager() == null) {
			    System.setSecurityManager(new SecurityManager());
			}
*/
			WdjetServiceServer server = new WdjetServiceServer();
			Naming.rebind("rmi://encoden.cnse.albany.edu:1942/WdjetService", server);
		}catch(Exception e){
			System.out.println("WdjetServiceHostERROR Noobles: " + e.toString());
		}
	}
	
	public static void main(String args[])
	{
		
		new WdjetServiceHost();
	}
}
