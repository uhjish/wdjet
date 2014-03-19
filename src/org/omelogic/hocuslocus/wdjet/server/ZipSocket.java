/*
 *      ZipSocket.java
 *      
 *      Copyright 2008 Ajish D. George <ajish@hocuslocus.com>
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
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */
package org.omelogic.hocuslocus.wdjet.server;

 
import java.net.*;
import java.io.*;
import java.util.zip.*;

public class ZipSocket extends Socket {
    private InputStream in;
    private OutputStream out;
    
    public ZipSocket() { super(); }
    public ZipSocket(String host, int port) 
        throws IOException {
            super(host, port);
    }
    
    public InputStream getInputStream() 
        throws IOException {
            if (in == null) {
                in = new ZipInputStream(super.getInputStream());
            }
  return in;
    }
 
    public OutputStream getOutputStream() 
        throws IOException {
            if (out == null) {
                out = new ZipOutputStream(super.getOutputStream());
            }
            return out;
    }
    
    public synchronized void close() throws IOException {
        OutputStream o = getOutputStream();
        o.flush();
        super.close();
    }
}

