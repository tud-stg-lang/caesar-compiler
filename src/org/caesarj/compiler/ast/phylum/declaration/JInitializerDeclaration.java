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
 * $Id: JInitializerDeclaration.java,v 1.6 2005-06-03 12:00:57 klose Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JEqualityExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JFieldAccessExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JTypeNameExpression;
import org.caesarj.compiler.ast.phylum.expression.literal.JBooleanLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.expression.literal.JStringLiteral;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JIfStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CInitializerContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class JInitializerDeclaration extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   */
  public JInitializerDeclaration(TokenReference where,
				 JBlock body,
				 boolean isStatic,
				 boolean isDummy,
                                 TypeFactory factory)
  {
    super(where,
	  ACC_PRIVATE | (isStatic ? ACC_STATIC : 0),
	  factory.getVoidType(),
	  (isStatic ? JAV_STATIC_INIT : JAV_INIT),
	  JFormalParameter.EMPTY,
	  CReferenceType.EMPTY,
	  body,
	  null,
	  null);

    this.isDummy = isDummy;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Return true if this initialiser declaration is used only to check code
   * and that it should not generate code
   */
  public boolean isDummy() {
    return isDummy;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @return true iff sub tree is correct enough to check code
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    CSourceMethod       method = super.checkInterface(context);

    if (! method.isStatic()) {
      // method Block$() is a synthetic method
      method.setSynthetic(true);
    }
    return method;
  }
  /**
   * Analyses the node (semantically).
   * @param	context		the analysis context
   * @exception	PositionedError	the analysis detected an error
   */
  public void checkBody1(CClassContext context) {
    // I've made it !
    // nothing to check.
  }

  /**
   * TODO [documentation]
   * @param	context		the actual context of analyse
   * @exception	PositionedError		Error catched as soon as possible
   */
  public void checkInitializer(CClassContext context) throws PositionedError {
    if (getMethod().isStatic()) {
      TokenReference    ref = TokenReference.NO_REF;
      JBlock            classBlock = null;
      CClass          clazz = context.getCClass();

      if (!clazz.isInterface()) {
        if (context.getEnvironment().getSourceVersion() >= KjcEnvironment.SOURCE_1_4) {
          JExpression     loadClass;
          CClass          topLevelClass;
          String          clazzName;

          while (clazz.getOwner() != null) {
            clazz = clazz.getOwner();
          }
 
          topLevelClass = clazz;

          if (clazz.isInterface()) {
            clazz = clazz.getAssertionStatusClass(context);
          }

          clazzName = clazz.getIdent();
          if (clazzName.lastIndexOf('/') > 0) {
            clazzName = clazzName.substring(clazzName.lastIndexOf('/')+1);
          }
          clazzName = JAV_IDENT_CLASS + clazzName;

          loadClass = new JMethodCallExpression(ref,
                                                new JTypeNameExpression(ref,
                                                                        clazz.getAbstractType()),
                                                JAV_IDENT_CLASS,
                                                new JExpression[] {
                                                  new JStringLiteral(ref,
                                                                     topLevelClass.getQualifiedName().replace('/','.'))
                                                });

          classBlock =   new JBlock(ref, 
                                    new JStatement[] {
                                      new JIfStatement(ref, 
                                                       new JEqualityExpression(ref,
                                                                               true,
                                                                               new JFieldAccessExpression(ref,
                                                                                                   new JTypeNameExpression(ref,
                                                                                                                           clazz.getAbstractType()),
                                                                                                   clazzName),
                                                                               new JNullLiteral(ref)), 
                                                       new JExpressionStatement(ref,
                                                                                new JAssignmentExpression(ref,
                                                                                                          new JFieldAccessExpression(ref,
                                                                                                                              new JTypeNameExpression(ref,
                                                                                                                                                      clazz.getAbstractType()),
                                                                                                                              clazzName),
                                                                                                          loadClass),
                                                                                null),
                                                       null,
                                                       null),
                                      new JIfStatement(ref, 
                                                       new JMethodCallExpression(ref,
                                                                                 new JFieldAccessExpression(ref, 
                                                                                                     new JTypeNameExpression(ref, 
                                                                                                                             clazz.getAbstractType()),
                                                                                                     clazzName), 
                                                                                 "desiredAssertionStatus",
                                                                                 JExpression.EMPTY),
                                                       new JExpressionStatement(ref,
                                                                                new JAssignmentExpression(ref,
                                                                                                          new JFieldAccessExpression(ref, IDENT_ASSERT),
                                                                                                          new JBooleanLiteral(ref, false)),
                                                                                null),
                                                       new JExpressionStatement(ref,
                                                                                new JAssignmentExpression(ref,
                                                                                                          new JFieldAccessExpression(ref, IDENT_ASSERT),
                                                                                                          new JBooleanLiteral(ref, true)),
                                                                                null),
                                                       null)},
                                    null);
        }
        if (classBlock != null) {
          body = new JBlock(getTokenReference(), 
                            new JStatement[] { classBlock, body },
                            null);
          ((CSourceMethod) getMethod()).setBody(body);
        }
      }
    }
    
    if (!getMethod().isStatic() && !isDummy()) {
      context.addInitializer();
    }

    // we want to do that at check intitializers time
    CMethodContext	self = new CInitializerContext(context, context.getEnvironment(), this);
    CBlockContext	block = new CBlockContext(self, context.getEnvironment(), parameters.length);

    if (!getMethod().isStatic()) {
      // add this local var
      block.addThisVariable();
    }
    body.analyse(block);
      if (! block.isReachable()) {
	throw new CLineError(getTokenReference(), KjcMessages.STATEMENT_UNREACHABLE);
      }

    block.close(getTokenReference());
    self.close(getTokenReference());
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private final boolean		isDummy;
}
