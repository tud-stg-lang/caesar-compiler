/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
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
 * $Id: OptimizeOptions.java,v 1.2 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.optimize;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class OptimizeOptions extends org.caesarj.util.Options {

  public OptimizeOptions(String name) {
    super(name);
  }

  public OptimizeOptions() {
    this("Optimize");
  }
  public boolean verbose = false;
  public int optimize = 2;
  public String destination = null;

  public boolean processOption(int code, Getopt g) {
    switch (code) {
    case 'v':
      verbose = !false; return true;
    case 'O':
      optimize = getInt(g, 0); return true;
    case 'd':
      destination = getString(g, ""); return true;
    default:
      return super.processOption(code, g);
    }
  }

  public String[] getOptions() {
    String[]	parent = super.getOptions();
    String[]	total = new String[parent.length + 3];
    System.arraycopy(parent, 0, total, 0, parent.length);
    total[parent.length + 0] = "  --verbose, -v:        Displays information while processing files [false]";
    total[parent.length + 1] = "  --optimize, -O<int>:  Level of optimization [2]";
    total[parent.length + 2] = "  --destination, -d<String>: Destination directory";
    
    return total;
  }


  public String getShortOptions() {
    return "vO:d:" + super.getShortOptions();
  }


  public void version() {
    System.out.println("Version 2.1A released 11. February 2002");
  }


  public void usage() {
    System.err.println("usage: org.caesarj.optimize.Main [option]* [--help] <class-files>");
  }


  public void help() {
    System.err.println("usage: org.caesarj.optimize.Main [option]* [--help] <class-files>");
    printOptions();
    System.err.println();
    version();
    System.err.println();
    System.err.println("This program is part of the Kopi Suite.");
    System.err.println("For more info, please see: http://www.dms.at/kopi");
  }

  public LongOpt[] getLongOptions() {
    LongOpt[]	parent = super.getLongOptions();
    LongOpt[]	total = new LongOpt[parent.length + LONGOPTS.length];
    
    System.arraycopy(parent, 0, total, 0, parent.length);
    System.arraycopy(LONGOPTS, 0, total, parent.length, LONGOPTS.length);
    
    return total;
  }

  private static final LongOpt[] LONGOPTS = {
    new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
    new LongOpt("optimize", LongOpt.REQUIRED_ARGUMENT, null, 'O'),
    new LongOpt("destination", LongOpt.REQUIRED_ARGUMENT, null, 'd')
  };
}
