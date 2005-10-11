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
 * $Id: JClassFieldDeclarator.java,v 1.4 2005-10-11 14:59:55 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CSimpleBodyContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * JLS 8.3 : Class Field Declaration.
 * JLS 9.3 ; Field (Constant) Declaration.
 *
 */
public class JClassFieldDeclarator extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	decl		the declarator of this field
   */
  public JClassFieldDeclarator(TokenReference where, JFieldDeclaration decl) {
    super(where, null);
    this.decl = decl;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the statement (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void analyse(CBodyContext context) throws PositionedError {
    decl.analyse(context);

    decl.getField().setAnalysed(true); // mark as analysed

    if (decl.hasInitializer()) {
      ((CSourceField)decl.getField()).setValue(decl.getVariable().getValue());

      if  (((CSourceField)decl.getField()).isFinal() && !decl.getVariable().getValue().isConstant()) {
        simpleContext = new CSimpleBodyContext(context, context.getEnvironment(), context);
        ((CSourceField)decl.getField()).setDeclarationOwner(this);
      }
    }
  }

  /**
   * 2nd part of analysation.
   */
  public void  analyseDeclaration(){
    if (simpleContext != null) {
      CExpressionContext	expressionContext = new CExpressionContext(simpleContext, simpleContext.getEnvironment());
      
      simpleContext = null;
      ((CSourceField)decl.getField()).setDeclarationOwner(null);
      try {
        ((CSourceField)decl.getField()).setValue(decl.getVariable().getValue().analyse(expressionContext));
      } catch (PositionedError e){
        // thrown in the first evaluation
      }
    }
  }

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void recurse(IVisitor s) {
      decl.accept(s);
  }

  /**
   * Generates a sequence of bytescodes
   *
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    TypeFactory         factory = context.getTypeFactory();

    if (decl.getField().getConstantValue(factory) == null) {
      decl.genCode(context);
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JFieldDeclaration	decl;
  private CBodyContext          simpleContext;
}
