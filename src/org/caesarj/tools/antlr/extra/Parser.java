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
 * $Id: Parser.java,v 1.2 2004-02-08 20:28:00 ostermann Exp $
 */

package org.caesarj.tools.antlr.extra;

import java.util.Vector;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.tools.antlr.runtime.LLkParser;
import org.caesarj.tools.antlr.runtime.ParserException;
import org.caesarj.util.Messages;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class describes the capabilities of parsers.
 */
public abstract class Parser extends LLkParser {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a new parser instance.
   * @param	compiler	the invoking compiler.
   * @param	scanner		the token stream generator
   * @param	lookahead	lookahead
   */
  protected Parser(CompilerBase compiler, Scanner scanner, int lookahead) {
    super(scanner, lookahead);
    this.compiler = compiler;
    this.scanner = scanner;
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Returns the compiler driver which invoked the parser.
   */
  public CompilerBase getCompiler() {
    return compiler;
  }

  /**
   * Returns the input buffer.
   */
  public final InputBuffer getBuffer() {
    return scanner.getBuffer();
  }

  /**
   * Returns a reference to the current position in the source file.
   */
  protected final TokenReference buildTokenReference() {
    return scanner.getTokenReference();
  }

  /**
   *
   */
  protected final JavaStyleComment[] getStatementComment() {
    return scanner.getStatementComment();
  }

  /**
   *
   */
  protected final JavadocComment getJavadocComment() {
    return scanner.getJavadocComment();
  }

  /**
   *
   */
  protected Vector getComment() {
    return null; // scanner.getComment();
  }

  /**
   * Reports that an error has been detected in the lexical analyser.
   * The handling is delegated to the compiler driver.
   * @param	error		the error to report
   */
  protected final void reportTrouble(PositionedError trouble) {
    compiler.reportTrouble(trouble);
  }

  /**
   * Generate an human readable error message
   */
  public PositionedError beautifyParseError(ParserException e) {
    String	message = e.toString(); // can do better

    if (message == null) {
      message = "unknown";
    } else {
      int	idx = message.indexOf(",");

      if (idx >= 0) {
	message = message.substring(idx + 1);
      }
    }

    return new PositionedError(scanner.getTokenReference(), Messages.SYNTAX_ERROR, message);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private final CompilerBase	compiler;
  private final Scanner		scanner;
}