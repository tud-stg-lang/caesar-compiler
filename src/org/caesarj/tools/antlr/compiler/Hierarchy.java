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
 * $Id: Hierarchy.java,v 1.1 2004-02-08 16:47:46 ostermann Exp $
 */

package org.caesarj.tools.antlr.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;

import org.caesarj.tools.antlr.runtime.ANTLRException;
import org.caesarj.tools.antlr.runtime.TokenStreamException;

public class Hierarchy {
  public Hierarchy() {
    symbols = new Hashtable(10);
    files = new Hashtable(10);

    LexerRoot.setPredefined(true);
    ParserRoot.setPredefined(true);

    symbols.put(LexerRoot.getName(), LexerRoot);
    symbols.put(ParserRoot.getName(), ParserRoot);
  }
  public void addGrammar(GrammarDefinition gr) {
    gr.setHierarchy(this);
    // add grammar to hierarchy
    symbols.put(gr.getName(), gr);
    // add grammar to file.
    GrammarFile f = getFile(gr.getFileName());
    f.addGrammar(gr);
  }
  public void addGrammarFile(GrammarFile gf) {
    files.put(gf.getName(), gf);
  }
  public void expandGrammarsInFile(String fileName) {
    GrammarFile f = getFile(fileName);
    for (Enumeration e=f.getGrammars().elements(); e.hasMoreElements(); ) {
      GrammarDefinition g = (GrammarDefinition)e.nextElement();
      g.expandInPlace();
    }
  }
  public GrammarDefinition findRoot(GrammarDefinition g) {
    if ( g.getSuperGrammarName()==null ) {		// at root
      return g;
    }
    // return root of super.
    GrammarDefinition sg = g.getSuperGrammar();
    if ( sg==null ) {
	return g;		// return this grammar if super missing
    }
    return findRoot(sg);
  }
  public GrammarFile getFile(String fileName) {
    return (GrammarFile)files.get(fileName);
  }
  public GrammarDefinition getGrammar(String gr) {
    return (GrammarDefinition)symbols.get(gr);
  }
  public static String optionsToString(IndexedVector options) {
    String s = "options {"+System.getProperty("line.separator");
    for (Enumeration e = options.elements() ; e.hasMoreElements() ;) {
      s += (Option)e.nextElement()+System.getProperty("line.separator");
    }
    s += "}"+
      System.getProperty("line.separator")+
      System.getProperty("line.separator");
    return s;
  }
  public void readGrammarFile(String file) throws FileNotFoundException {
    FileReader grStream = new FileReader(file);
    addGrammarFile(new GrammarFile(file));

    // Create the simplified grammar lexer/parser
    PreprocessorLexer ppLexer = new PreprocessorLexer(grStream);
    ppLexer.setFilename(file);
    Preprocessor pp = new Preprocessor(ppLexer);
    pp.setFilename(file);

    // populate the hierarchy with class(es) read in
    try {
      pp.grammarFile(this, file);
    } catch (TokenStreamException io) {
      Utils.toolError("Token stream error reading grammar(s):"+io);
    } catch (ANTLRException se) {
      Utils.toolError("error reading grammar(s):"+se);
    }
  }
  /**
   * Return true if hierarchy is complete, false if not
   */
  public boolean verifyThatHierarchyIsComplete() {
    boolean complete = true;
    // Make a pass to ensure all grammars are defined
    for (Enumeration e = symbols.elements() ; e.hasMoreElements() ;) {
      GrammarDefinition c = (GrammarDefinition)e.nextElement();
      if ( c.getSuperGrammarName()==null ) {
	continue;		// at root: ignore predefined roots
      }
      GrammarDefinition superG = c.getSuperGrammar();
      if ( superG == null ) {
	Utils.toolError("grammar "+c.getSuperGrammarName()+" not defined");
	complete = false;
	symbols.remove(c.getName()); // super not defined, kill sub
      }
    }

    if ( !complete ) {
	return false;
    }

    // Make another pass to set the 'type' field of each grammar
    // This makes it easy later to ask a grammar what its type
    // is w/o having to search hierarchy.
    for (Enumeration e = symbols.elements() ; e.hasMoreElements() ;) {
      GrammarDefinition c = (GrammarDefinition)e.nextElement();
      if ( c.getSuperGrammarName()==null ) {
	continue;		// ignore predefined roots
      }
      c.setType(findRoot(c).getName());
    }

    return true;
  }

  private static GrammarDefinition LexerRoot = new GrammarDefinition("Lexer", null, null);
  private static GrammarDefinition ParserRoot = new GrammarDefinition("Parser", null, null);
  private Hashtable symbols;	// table of grammars
  private Hashtable files;	// table of grammar files read in
}
