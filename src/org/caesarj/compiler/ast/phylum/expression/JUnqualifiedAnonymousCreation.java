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
 * $Id: JUnqualifiedAnonymousCreation.java,v 1.2 2004-03-17 15:23:11 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.CMethodNotFoundError;
import org.caesarj.compiler.ast.phylum.declaration.*;
import org.caesarj.compiler.ast.phylum.statement.*;
import org.caesarj.compiler.ast.phylum.variable.*;
import org.caesarj.compiler.ast.visitor.*;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CThrowableInfo;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * !!! This class represents a new allocation expression 'new Toto(1){}'
 */
public class JUnqualifiedAnonymousCreation extends JExpression {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * This method is directly called by the parser
   * @param	where		the line of this node in the source code
   * @param	objectType	the type of this object allocator
   * @param	params		parameters to be passed to constructor
   */
  public JUnqualifiedAnonymousCreation(TokenReference where,
				      CReferenceType objectType,
				      JExpression[] params,
				      JClassDeclaration decl)
  {
    super(where);

    this.type = objectType;
    this.params = params;
    this.decl = decl;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Compute the type of this expression (called after parsing)
   * @return the type of this expression
   */
  public CType getType(TypeFactory factory) {
    return type;
  }

  /**
   * Returns true iff this expression can be used as a statement (JLS 14.8)
   */
  public boolean isStatementExpression() {
    return true;
  }

  // ----------------------------------------------------------------------
  // SEMANTIC ANALYSIS
  // ----------------------------------------------------------------------

  /**
   * Analyses the expression (semantically).
   * @param	context		the analysis context
   * @return	an equivalent, analysed expression
   * @exception	PositionedError	the analysis detected an error
   */
  public JExpression analyse(CExpressionContext context) throws PositionedError {
    TypeFactory         factory = context.getTypeFactory();
    CType[]	argsType;
    CClass	owner = context.getClassContext().getCClass();
    CReferenceType	superClass;

    try {
      type = (CReferenceType)type.checkType(context);
    } catch (UnpositionedError e) {
      throw e.addPosition(getTokenReference());
    }

    check(context, !type.isTypeVariable(), KjcMessages.NEW_TVPE_VARIABLE, type);

    argsType = new CType[params.length];

    for (int i = 0; i < argsType.length; i++) {
      params[i] = params[i].analyse(context);
      argsType[i] = params[i].getType(factory);
      verify(argsType[i] != null);
    }

    /* JLS 15.9.2: If the class instance creation expression occurs 
       in a static context (§8.1.2), then i has no immediately enclosing 
       instance. Otherwise, the immediately enclosing instance of i is this. */
    decl.generateInterface(context.getClassReader(), 
                           owner,
			   owner.getQualifiedName() + "$" + context.getClassContext().getNextSyntheticIndex());

    CClass      superCClass = type.getCClass();

    if (type.getCClass().isInterface()) {
      superClass = context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);
      decl.setInterfaces(new CReferenceType[] { type });
    } else {
      superClass = type;
    }
    decl.setSuperClass(superClass);

    // The class of the super class must be set explicitly
    // before lookup of the constructor of the superclass
    // because it will be set in decl only in checkInterface.
    // On the other hand checkInterface needs the constructor
    // to be created.
    // graf 010422 :
    // But, why not analyse the constructor later ? Perhaps
    // to be able to signal an error ?

    decl.getCClass().setSuperClass(superClass);

    // add implicit constructor
    JConstructorDeclaration	cstr;
    CMethod			superCstr;

    try {
      superCstr = superClass.getCClass().lookupMethod(context, decl.getCClass(), null, JAV_CONSTRUCTOR, argsType, superClass.getArguments());
    } catch (UnpositionedError cue) {
      throw cue.addPosition(getTokenReference());
    }
    if (superCstr == null) {
      throw new CMethodNotFoundError(getTokenReference(), null, superClass.toString(), argsType);
    }

    CType[]		parameters = superCstr.getParameters();
    JFormalParameter[]	fparams = new JFormalParameter[parameters.length];
    CReferenceType[]	throwables = superCstr.getThrowables();

    for (int i = 0; i < parameters.length; i++) {
      fparams[i] = new JFormalParameter(getTokenReference(),
					JLocalVariable.DES_GENERATED,
					parameters[i],
					"dummy" + i,
					true);
    }

    JExpression[]       checkedParams = new JExpression[params.length];

    for (int i = 0; i < params.length; i++) {
      checkedParams[i] = new JLocalVariableExpression(getTokenReference(),
						      fparams[i]);
    }
    JConstructorCall    cstrCall = new JConstructorCall(getTokenReference(), false, checkedParams);

    cstr = new JConstructorDeclaration(getTokenReference(),
				       ACC_PUBLIC,
				       decl.getCClass().getIdent(),
				       fparams,
				       throwables,
				       new JConstructorBlock(getTokenReference(), cstrCall, new JStatement[0]),
				       null,
				       null,
                                       factory);
    decl.setDefaultConstructor(cstr);
    decl.join(context.getClassContext());
    decl.checkInterface(context.getClassContext());
    /* FJRM !!! is done in checkInterface step above
    //Walter start
    if (decl instanceof JClassDeclaration)
		((JClassDeclaration)decl).initFamilies(context.getClassContext());
	//Walter end
	*/
    
    if (context.isStaticContext()) {
      decl.getCClass().setModifiers(decl.getCClass().getModifiers() | ACC_STATIC);
    } else {
      decl.addOuterThis();
    }

    decl.checkInitializers(context);
    decl.checkTypeBody(context);

    context.getClassContext().getTypeDeclaration().addLocalTypeDeclaration(decl);
    type = decl.getCClass().getAbstractType(); // !!! FIXIFGEN
    owner.addInnerClass(type);

    //!!! review and create test cases
    context = new CExpressionContext(context, context.getEnvironment());

    check(context, !type.getCClass().isAbstract(), KjcMessages.NEW_ABSTRACT, type);
    check(context, !type.getCClass().isInterface(), KjcMessages.NEW_INTERFACE, type);

    constructor = cstr.getMethod();

    // check access
    local = context.getClassContext().getCClass();
    check(context, constructor.isAccessible(local), KjcMessages.CONSTRUCTOR_NOACCESS, type);

    check(context, 
          !constructor.getOwner().hasOuterThis() 
          || (!context.isStaticContext() 
              && (local.descendsFrom(constructor.getOwner().getOwner()) 
                  || (local.getOwner() != null && local.getOwner() == constructor.getOwner().getOwner()))),
          KjcMessages.INNER_INHERITENCE, constructor.getOwnerType(), local);

    if (constructor.getOwner().getSuperClass().hasOuterThis()
        && !local.descendsFrom(constructor.getOwner().getSuperClass().getOwner())) {
      check(context, 
            inCorrectOuter(local,constructor.getOwner().getSuperClass().getOwner()), 
            KjcMessages.INNER_INHERITENCE, constructor.getOwnerType(), local);
      CClass            itsOuterOfThis = local;

      while (!itsOuterOfThis.getOwner().descendsFrom(constructor.getOwner().getSuperClass().getOwner())) {
        itsOuterOfThis = itsOuterOfThis.getOwner();
      }
      // anlayse creates accessor(s) to the correct this$0 field
      cstrCall.setSyntheticOuter(itsOuterOfThis.getOwner());
    } 

    CReferenceType[]	exceptions = constructor.getThrowables();
    for (int i = 0; i < exceptions.length; i++) {
      context.getBodyContext().addThrowable(new CThrowableInfo(exceptions[i], this));
    }

    argsType = constructor.getParameters();

    for (int i = 0; i < params.length; i++) {
      params[i] = params[i].convertType(context, argsType[i]);
    }

    return this;
  }

  private boolean inCorrectOuter(CClass local, CClass outer) {
    while (local != null) {
      if (local.descendsFrom(outer)) {
        return true;
      }
      local = local.getOwner();
    }
    return false;
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    p.visitUnqualifiedAnonymousCreation(this, type, params, decl);
  }

  /**
   * Generates JVM bytecode to evaluate this expression.
   *
   * @param	code		the bytecode sequence
   * @param	discardValue	discard the result of the evaluation ?
   */
  public void genCode(GenerationContext context, boolean discardValue) {
    CodeSequence code = context.getCodeSequence();

    setLineNumber(code);

    code.plantClassRefInstruction(opc_new, type.getCClass().getQualifiedName());
    if (!discardValue) {
      code.plantNoArgInstruction(opc_dup);
    }
    if (!constructor.getOwner().isStatic() 
        && constructor.getOwner().hasOuterThis()) {
      // inner class
      code.plantLoadThis();
    }

    for (int i = 0; i < params.length; i++) {
      params[i].genCode(context, false);
    }
    constructor.getOwner().genOuterSyntheticParams(context);
    constructor.genCode(context, true);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private JExpression[]		params;
  private CReferenceType        type;
  private CClass		local;
  private CMethod		constructor;
  private JClassDeclaration	decl;
}
