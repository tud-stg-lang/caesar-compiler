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
 * $Id: JVariableDeclarationStatement.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CVariableInfo;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 14.4: Local Variable Declaration Statement
 *
 * A local variable declaration statement declares one or more local variable names.
 */
public class JVariableDeclarationStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	vars		the variables declared by this statement
   */
  public JVariableDeclarationStatement(TokenReference where, JVariableDefinition[] vars, JavaStyleComment[] comments) {
    super(where, comments);

    this.vars = vars;
  }

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	var		the variable declared by this statement
   */
  public JVariableDeclarationStatement(TokenReference where, JVariableDefinition var, JavaStyleComment[] comments) {
    super(where, comments);

    this.vars = new JVariableDefinition[] {var};
  }

  /**
   * Returns an array of variable definition declared by this statement
   */
  public JVariableDefinition[] getVars() {
    return vars;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Sets the variables to be for variables
   */
  public void setIsInFor() {
    for (int i = 0; i < this.vars.length; i++) {
      vars[i].setIsLoopVariable();
    }
  }

  /**
   * Unsets the variables to be for variables
   */
  public void unsetIsInFor() {
    for (int i = 0; i < this.vars.length; i++) {
      vars[i].unsetIsLoopVariable();
    }
  }

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    for (int i = 0; i < this.vars.length; i++) {
      try {
	context.getBlockContext().addVariable(vars[i]);
	vars[i].analyse(context);

	if (vars[i].hasInitializer()) {
	  context.setVariableInfo(vars[i].getIndex(), CVariableInfo.INITIALIZED);
	}
      } catch (UnpositionedError e) {
	throw new CLineError(getTokenReference(), e.getFormattedMessage());
      }
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void recurse(IVisitor s) {
    for (int i = 0; i < vars.length; i++) {
        vars[i].accept(s);
    }
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    for (int i = 0; i < this.vars.length; i++) {
      if (vars[i].getValue() != null) {
	vars[i].getValue().genCode(context, false);
	vars[i].genStore(context);
      }
    }
  }
  
  public JVariableDefinition[] getVariables() {return vars;}

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JVariableDefinition[]		vars;
}
