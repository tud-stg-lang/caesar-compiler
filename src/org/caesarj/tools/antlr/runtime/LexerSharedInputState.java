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
 * $Id: LexerSharedInputState.java,v 1.1 2004-02-08 16:47:50 ostermann Exp $
 */

package org.caesarj.tools.antlr.runtime;

import java.io.InputStream;
import java.io.Reader;

/**
 * This object contains the data associated with an
 *  input stream of characters.  Multiple lexers
 *  share a single LexerSharedInputState to lex
 *  the same input stream.
 */
public class LexerSharedInputState {
  protected int column=1;
  protected int line=1;
  protected int tokenStartColumn = 1;
  protected int tokenStartLine = 1;
  protected InputBuffer input;

  /**
   * What file (if known) caused the problem?
   */
  protected String filename;

  public int guessing = 0;

  public LexerSharedInputState(InputBuffer inbuf) {
    input = inbuf;
  }

  public LexerSharedInputState(InputStream in) {
    this(new ByteBuffer(in));
  }

  public LexerSharedInputState(Reader in) {
    this(new CharBuffer(in));
  }
}
