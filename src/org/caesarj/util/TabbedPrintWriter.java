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
 * $Id: TabbedPrintWriter.java,v 1.1 2004-02-08 20:28:01 ostermann Exp $
 */

package org.caesarj.util;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * This class implements a tabbed print writer
 */
// !!! laurent 20020713 : make this class inherits from PrintWriter ?
public class TabbedPrintWriter {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a new TAbbedPrintWriter
   * 
   * @param	writer		the writer into the code is generated
   */
  public TabbedPrintWriter(Writer writer) {
    this.writer =  new PrintWriter(writer);
    this.pos = 0;
  }

  /**
   * Close the stream at the end
   */
  public void close() {
    writer.close();
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public int getPos() {
    return pos;
  }

  /**
   * Set pos
   */
  public void setPos(int pos) {
    this.pos = pos;
  }

  /**
   * Increment tab
   */
  public void add(int pos) {
    this.pos += pos;
  }

  /**
   * Decrement tab
   */
  public void sub(int pos) {
    this.pos += pos;
  }

  /**
   * Print a new line
   */
  public void println() {
    writer.println();
    column = 0;
    line++;
  }

  /**
   * Print a string
   */
  public void print(String s) {
    /*if (Math.max(column, pos) + s.length() > 80 && s.length() > 2) {
      println();
    }*/
    checkPos();
    writer.print(s);
    column += s.length();
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------

  private void checkPos() {
    if (column < pos) {
      writer.print(space(pos - column));
      column = Math.max(column, pos);
    }
  }

  private String space(int count) {
    if (count <= 0) {
      count = 1;
    }
    return spaceIn(count);
  }

  private String spaceIn(int count) {
    return new String(new char[count]).replace((char)0, ' ');
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final PrintWriter             writer;

  protected int				pos;
  protected int				line;
  protected int				column;
}
