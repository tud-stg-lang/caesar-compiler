// Generated by optgen from LexgenOptions.opt
package org.caesarj.tools.lexgen;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

public class LexgenOptions extends org.caesarj.util.Options {

  public LexgenOptions(String name) {
    super(name);
  }

  public LexgenOptions() {
    this("Lexgen");
  }
  public boolean definition = false;
  public boolean inter = false;
  public boolean keywords = false;
  public boolean tokens = false;
  public boolean flexrules = false;

  public boolean processOption(int code, Getopt g) {
    switch (code) {
    case 'd':
      definition = !false; return true;
    case 'i':
      inter = !false; return true;
    case 'k':
      keywords = !false; return true;
    case 't':
      tokens = !false; return true;
    case 'f':
      flexrules = !false; return true;
    default:
      return super.processOption(code, g);
    }
  }

  public String[] getOptions() {
    String[]	parent = super.getOptions();
    String[]	total = new String[parent.length + 5];
    System.arraycopy(parent, 0, total, 0, parent.length);
    total[parent.length + 0] = "  --definition, -d:     Generates a definition file [false]";
    total[parent.length + 1] = "  --inter, -i:          Generates an interface file [false]";
    total[parent.length + 2] = "  --keywords, -k:       Generates a keyword file [false]";
    total[parent.length + 3] = "  --tokens, -t:         Generates CToken entries in the interface file (implies --inter) [false]";
    total[parent.length + 4] = "  --flexrules, -f:      Generates JFlex rules for literals, keywords and operators [false]";
    
    return total;
  }


  public String getShortOptions() {
    return "diktf" + super.getShortOptions();
  }


  public void version() {
    System.out.println("Version 2.1A released 11. February 2002");
  }


  public void usage() {
    System.err.println("usage: at.dms.lexgen.Main [option]* [--help] <file>");
  }


  public void help() {
    System.err.println("usage: at.dms.lexgen.Main [option]* [--help] <file>");
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
    new LongOpt("definition", LongOpt.NO_ARGUMENT, null, 'd'),
    new LongOpt("inter", LongOpt.NO_ARGUMENT, null, 'i'),
    new LongOpt("keywords", LongOpt.NO_ARGUMENT, null, 'k'),
    new LongOpt("tokens", LongOpt.NO_ARGUMENT, null, 't'),
    new LongOpt("flexrules", LongOpt.NO_ARGUMENT, null, 'f')
  };
}