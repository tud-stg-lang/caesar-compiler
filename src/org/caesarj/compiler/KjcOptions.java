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
 * $Id: KjcOptions.java,v 1.6 2005-03-01 15:38:42 gasiunas Exp $
 */

package org.caesarj.compiler;

import org.caesarj.compiler.constants.CaesarConstants;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class KjcOptions extends org.caesarj.util.Options {

  public KjcOptions(String name) {
    super(name);
  }

  public KjcOptions() {
    this("cjc");
  }
  public boolean verbose = false;
  /* Andreas start
  public boolean java = false;
  */
  public boolean _java = false;
  // Andreas end
  public String encoding = null;
  public int warning = 1;
  public int optimize = 1;
  public String extdirs = null;
  public String destination = null;
  public String classpath = null;
  public String source = "1.2";
  public String assertion = "none";
  public boolean generic = false;
  public String filter = "org.caesarj.compiler.DefaultFilter";

  public boolean processOption(int code, Getopt g) {
    switch (code) {
    case 'v':
      verbose = !false; return true;
    case 'e':
      encoding = getString(g, ""); return true;
    case 'w':
      warning = getInt(g, 2); return true;
    case 'O':
      optimize = getInt(g, 2); return true;
    case 't':
      extdirs = getString(g, ""); return true;
    case 'd':
      destination = getString(g, ""); return true;
    case 'C':
      classpath = getString(g, ""); return true;
    case 'f':
      filter = getString(g, ""); return true;
    default:
      return super.processOption(code, g);
    }
  }

  public String[] getOptions() {
    String[]	parent = super.getOptions();
    String[]	total = new String[parent.length + 7];
    System.arraycopy(parent, 0, total, 0, parent.length);
    total[parent.length + 0] = "  --verbose, -v:        Prints out information during compilation [false]";
    total[parent.length + 1] = "  --encoding, -e <String>: Sets the character encoding for the source file(s).";
    total[parent.length + 2] = "  --warning, -w <int>:   Maximal level of warnings to be displayed [1]";
    total[parent.length + 3] = "  --optimize, -O <int>:  Optimizes X times [1]";
    total[parent.length + 4] = "  --extdirs, -t <String>: Define location of installed extensions";
    total[parent.length + 5] = "  --destination, -d <String>: Writes files to destination";
    total[parent.length + 6] = "  --classpath, -C <String>: Changes class path to classpath";
    
    return total;
  }


  public String getShortOptions() {
    return "ve:w:O:t:d:C:f:" + super.getShortOptions();
  }


  public void version() {
    System.out.println("Version " + CaesarConstants.VERSION_STR);
  }


  public void usage() {
    System.err.println("usage: cjc [option]* [--help] <source-files>");
  }


  public void help() {
    System.out.println("usage: cjc [option]* [--help] <source-files>");
    System.out.println();
    System.out.println("  <source-files> - source file paths relative to current dir or absolute");
    System.out.println("                   use prefix @ to pass list files");
    System.out.println("Options:");
    printOptions();
    System.out.println();
    version();    
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
    new LongOpt("encoding", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
    new LongOpt("warning", LongOpt.OPTIONAL_ARGUMENT, null, 'w'),
    new LongOpt("optimize", LongOpt.REQUIRED_ARGUMENT, null, 'O'),
    new LongOpt("extdirs", LongOpt.REQUIRED_ARGUMENT, null, 't'),
    new LongOpt("destination", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
    new LongOpt("classpath", LongOpt.REQUIRED_ARGUMENT, null, 'C'),     
  };
}

