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
 * $Id: Main.java,v 1.1 2003-07-05 18:29:45 werner Exp $
 */

package org.caesarj.kopi;

import java.lang.reflect.Method;

/**
 * Wrapper class for all KOPI software.
 *
 * @author Edouard G. Parmelan <edouard.parmelan@quadratec.fr>
 */
public class Main {
  public static void main(String[] args) {
    try {
      /* Get class of command to invoke */
      Class		commandClass = Class.forName("at.dms." + args[0] + ".Main");

      /* Get method with signature main(String[])V */
      Method		commandMain = commandClass.getDeclaredMethod("main", new Class[]{ String[].class });

      /* Build the invoke arguments */
      String[]		commandArgs = new String[args.length - 1];
      System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

      /* Invoke it */
      commandMain.invoke(null, new Object[] { commandArgs });
    } catch (Exception e) {
      org.caesarj.kjc.Main.main(args);
    }
  }
}
