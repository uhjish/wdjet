/*
 *      ImportDialogPanel.java
 *
 */

package org.omelogic.hocuslocus.wdjet.client;

import java.util.UUID;
import java.lang.Exception;

public interface ImportDialogPanel
{

	public void runImportProcess(WdjetService service, UUID session);

}
