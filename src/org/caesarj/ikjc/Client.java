/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: Client.java,v 1.1 2003-07-05 18:29:44 werner Exp $
 */

package org.caesarj.ikjc;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class implements the client side of the kjc incremental compiler
 */
public class Client {

  // ----------------------------------------------------------------------
  // ENTRY POINT
  // ----------------------------------------------------------------------

  /**
   * Entry point
   *
   * @param	args		the command line arguments
   */
  public static void main(String[] args) {
    boolean	success;

    success = new Client().run(args);

    System.exit(success ? 0 : 1);
  }

  /**
   * Runs a remote compilation session.
   *
   * @param	args		the command line arguments
   */
  public boolean run(String[] args) {
    boolean	success;

    if (!parseArguments(args)) {
      return false;
    }
    if (!openConnection()) {
      return false;
    }
    sendArguments();
    success = processDiagnostics();
    closeConnection();

    return success;
  }

  /**
   * Parses the argument list.
   */
  private boolean parseArguments(String[] args) {
    options = new ClientOptions();
    if (!options.parseCommandLine(args)) {
      return false;
    }
    return true;
  }

  /**
   * Opens the connection to the server.
   */
  private boolean openConnection() {
    try {
      socket = new Socket(options.host, options.port);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      return true;
    } catch (UnknownHostException e) {
      System.err.println("Host unknown: " + options.host);
      return false;
    } catch (IOException e) {
      System.err.println("Cannot connect to " + options.host + ":" + options.port + ". Is the server running ?");
      return false;
    }
  }

  /**
   * Closes the connection to the server.
   */
  private void closeConnection() {
    try {
      out.close();
      in.close();
      socket.close();
    } catch (IOException e) {
      System.err.println("An I/O error has occurred : " + e.getMessage());
    }
  }

  /**
   * Sends the compilation arguments to the server.
   */
  private void sendArguments() {
    // working directory
    out.println(System.getProperty("user.dir"));

    // compiler to invoke
    out.println(options.compiler);

    // arguments
    for (int i = 0; i < options.nonOptions.length; i++) {
      out.println(options.nonOptions[i]);
    }

    // an empty line as end-of-argument delimiter
    out.println();
  }

  /**
   * Reads the diagnostics from the server.
   */
  private boolean processDiagnostics() {
    String	fromServer;

    try {
      while ((fromServer = in.readLine()) != null) {
	if (fromServer.endsWith(Constants.TERM_STRING)) {
	  return new Boolean(in.readLine()).booleanValue();
	} else {
	  System.out.println(fromServer);
	}
      }
    } catch (IOException e) {
      System.err.println("An I/O error has occurred : " + e.getMessage());
      return false;
    }

    System.err.println("Unexpected end of connection");
    return false;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private ClientOptions		options;

  private Socket		socket;
  private PrintWriter		out;
  private BufferedReader	in;
}
