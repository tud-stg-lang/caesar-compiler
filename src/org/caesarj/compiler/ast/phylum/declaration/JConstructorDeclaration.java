/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: JConstructorDeclaration.java,v 1.8 2005-07-22 10:28:52 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CConstructorContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents a java class in the syntax tree
 */
public class JConstructorDeclaration extends JMethodDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	parent		parent in which this methodclass is built
   * @param	modifiers	list of modifiers
   * @param	ident		the name of this method
   * @param	parameters	the parameters of this method
   * @param	exceptions	the exceptions throw by this method
   * @param	constructorCall	an explicit constructor invocation
   * @param	body		the body of the method
   * @param	javadoc		java documentation comments
   * @param	comments	other comments in the source code
   */
  public JConstructorDeclaration(TokenReference where,
				 int modifiers,
				 String ident,
				 JFormalParameter[] parameters,
				 CReferenceType[] exceptions,
				 JConstructorBlock body,
				 JavadocComment javadoc,
				 JavaStyleComment[] comments,
                 TypeFactory factory)
  {
    super(where,
	  modifiers,
	  factory.getVoidType(),
	  ident,
	  parameters,
	  exceptions,
          body,
	  javadoc,
	  comments);
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the constructor called by this constructor.
   */
  public CMethod getCalledConstructor() {
    return ((JConstructorBlock)body).getCalledConstructor();
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @exception	PositionedError	an error with reference to the source file
   */
  public CSourceMethod checkInterface(CClassContext context) throws PositionedError {
    check(context,
	  CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE),
	  KjcMessages.INVALID_CONSTRUCTOR_FLAGS,
	  ident);

    if (context == null) {
      new RuntimeException().printStackTrace();
    }

    check(context,
	  ident == context.getCClass().getIdent(),
	  KjcMessages.CONSTRUCTOR_BAD_NAME,
	  ident,
	  context.getCClass().getIdent());
    check(context,
	  !context.getCClass().isInterface(),
	  KjcMessages.CONSTRUCTOR_IN_INTERFACE,
	  ident,
	  context.getCClass().getIdent());
    return super.checkInterface(context);
  }

  /**
   * Check expression and evaluate and alter context
   * @param	context			the actual context of analyse
   * @return	a pure java expression including promote node
   * @exception	PositionedError Error catched as soon as possible
   */
  public void checkBody1(CClassContext context) throws PositionedError {
    check(context, body != null, KjcMessages.CONSTRUCTOR_NOBODY, ident);

    CMethodContext	self = new CConstructorContext(context, 
                                                       context.getEnvironment(), 
                                                       this);
    CBlockContext	block = new CBlockContext(self, 
                                                  context.getEnvironment(), 
                                                  parameters.length);
    CClass              owner = context.getClassContext().getCClass();

    block.addThisVariable();
    if (owner.isNested() && owner.hasOuterThis()) {
      block.addThisVariable(); // add enclosing this$0
    }
    for (int i = 0; i < parameters.length; i++) {
      parameters[i].analyse(block);
    }
    body.analyse(block);

    block.close(getTokenReference());
    self.close(getTokenReference());

    // check that all final instance fields are initialized
/*  CField[]	classFields = context.getCClass().getFields();

    for (int i = 0; i < classFields.length; i++) {
      if (! classFields[i].isStatic() && !CVariableInfo.isInitialized(self.getFieldInfo(i))) {
	check(context,
	      !classFields[i].isFinal() || classFields[i].isSynthetic() || classFields[i].getIdent() == JAV_OUTER_THIS,
	      KjcMessages.UNINITIALIZED_FINAL_FIELD,
	      classFields[i].getIdent());

	context.reportTrouble(new CWarning(getTokenReference(),
					   KjcMessages.UNINITIALIZED_FIELD,
					   classFields[i].getIdent()));
      }
    }*/
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

	public void genCode(GenerationContext context)
	{
		super.genCode(context);
	}
}
