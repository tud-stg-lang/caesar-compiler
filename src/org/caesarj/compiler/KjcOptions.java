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
 * $Id: KjcOptions.java,v 1.4 2005-01-21 18:16:25 aracic Exp $
 */

package org.caesarj.compiler;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class KjcOptions extends org.caesarj.util.Options {

  public KjcOptions(String name) {
    super(name);
  }

  public KjcOptions() {
    this("Kjc");
  }
  public boolean beautify = false;
  public boolean verbose = false;
  /* Andreas start
  public boolean java = false;
  */
  public boolean _java = false;
  // Andreas end
  public String encoding = null;
  public boolean nowrite = false;
  public int warning = 1;
  public boolean nowarn = false;
  public int optimize = 1;
  public boolean deprecation = false;
  public String extdirs = null;
  public String destination = null;
  public String classpath = null;
  public boolean debug = false;
  public String source = "1.2";
  public String assertion = "none";
  public boolean generic = false;
  public String filter = "org.caesarj.compiler.DefaultFilter";

  public boolean processOption(int code, Getopt g) {
    switch (code) {
    case 'b':
      beautify = !false; return true;
    case 'v':
      verbose = !false; return true;
    case 'j':
      /* Andreas start
      java = !false; return true;
      */
      _java = !false; return true;
      // Andreas end
    case 'e':
      encoding = getString(g, ""); return true;
    case 'n':
      nowrite = !false; return true;
    case 'w':
      warning = getInt(g, 2); return true;
    case '*':
      nowarn = !false; return true;
    case 'O':
      optimize = getInt(g, 2); return true;
    case 'D':
      deprecation = !false; return true;
    case 't':
      extdirs = getString(g, ""); return true;
    case 'd':
      destination = getString(g, ""); return true;
    case 'C':
      classpath = getString(g, ""); return true;
    case 'g':
      debug = !false; return true;
    case 's':
      source = getString(g, ""); return true;
    case 'A':
      assertion = getString(g, ""); return true;
    case 'G':
      generic = !false; return true;
    case 'f':
      filter = getString(g, ""); return true;
    default:
      return super.processOption(code, g);
    }
  }

  public String[] getOptions() {
    String[]	parent = super.getOptions();
    String[]	total = new String[parent.length + 9];
    System.arraycopy(parent, 0, total, 0, parent.length);
    total[parent.length + 0] = "  --verbose, -v:        Prints out information during compilation [false]";
    total[parent.length + 1] = "  --encoding, -e<String>: Sets the character encoding for the source file(s).";
    total[parent.length + 2] = "  --nowrite, -n:        Only checks files, doesn't generate code [false]";
    total[parent.length + 3] = "  --warning, -w<int>:   Maximal level of warnings to be displayed [1]";
    total[parent.length + 4] = "  --optimize, -O<int>:  Optimizes X times [1]";
    total[parent.length + 5] = "  --deprecation, -D:    Tests for deprecated members [false]";
    total[parent.length + 6] = "  --destination, -d<String>: Writes files to destination";
    total[parent.length + 7] = "  --classpath, -C<String>: Changes class path to classpath";
//    total[parent.length + 12] = "  --debug, -g:          Produces debug information (does nothing yet) [false]";
    total[parent.length + 8] = "  --source, -s<String>: Sets the source language (1.1, 1.2, 1.3, 1.4) [1.2]";
//    total[parent.length + 16] = "  --filter, -f<String>: Warning filter [org.caesarj.kjc.DefaultFilter]";
    
    return total;
  }


  public String getShortOptions() {
    return "bvje:nw::*O::Dt:d:C:gs:A:Gf:" + super.getShortOptions();
  }


  public void version() {
    System.out.println("Version 0.1");
  }


  public void usage() {
    System.err.println("usage: org.caesarj.compiler.Main [option]* [--help] <java-files>");
  }


  public void help() {
    System.err.println("usage: org.caesarj.compiler.Main [option]* [--help] <java-files>");
    printOptions();
    System.err.println();
    version();
    System.err.println();
  }

  public LongOpt[] getLongOptions() {
    LongOpt[]	parent = super.getLongOptions();
    LongOpt[]	total = new LongOpt[parent.length + LONGOPTS.length];
    
    System.arraycopy(parent, 0, total, 0, parent.length);
    System.arraycopy(LONGOPTS, 0, total, parent.length, LONGOPTS.length);
    
    return total;
  }

  private static final LongOpt[] LONGOPTS = {
    new LongOpt("beautify", LongOpt.NO_ARGUMENT, null, 'b'),
    new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
    new LongOpt("java", LongOpt.NO_ARGUMENT, null, 'j'),
    new LongOpt("encoding", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
    new LongOpt("nowrite", LongOpt.NO_ARGUMENT, null, 'n'),
    new LongOpt("warning", LongOpt.OPTIONAL_ARGUMENT, null, 'w'),
    new LongOpt("nowarn", LongOpt.NO_ARGUMENT, null, '*'),
    new LongOpt("optimize", LongOpt.OPTIONAL_ARGUMENT, null, 'O'),
    new LongOpt("deprecation", LongOpt.NO_ARGUMENT, null, 'D'),
    new LongOpt("extdirs", LongOpt.REQUIRED_ARGUMENT, null, 't'),
    new LongOpt("destination", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
    new LongOpt("classpath", LongOpt.REQUIRED_ARGUMENT, null, 'C'),
    new LongOpt("debug", LongOpt.NO_ARGUMENT, null, 'g'),
    new LongOpt("source", LongOpt.REQUIRED_ARGUMENT, null, 's'),
    new LongOpt("assertion", LongOpt.REQUIRED_ARGUMENT, null, 'A'),
    new LongOpt("generic", LongOpt.NO_ARGUMENT, null, 'G'),
    new LongOpt("filter", LongOpt.REQUIRED_ARGUMENT, null, 'f')
  };
}
