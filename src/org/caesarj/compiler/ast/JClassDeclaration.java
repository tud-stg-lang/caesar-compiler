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
 * $Id: JClassDeclaration.java,v 1.2 2004-02-08 20:27:58 ostermann Exp $
 */

package org.caesarj.compiler.ast;

import java.util.ArrayList;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.CVariableInfo;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a java class in the syntax tree
 */
public class JClassDeclaration extends JTypeDeclaration {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a class declaration node in the syntax tree.
   *
   * @param	where		the line of this node in the source code
   * @param	modifiers	the list of modifiers of this class
   * @param	ident		the simple name of this class
   * @param	superClass	the super class of this class
   * @param	interfaces	the interfaces implemented by this class
   * @param	fields		the fields defined by this class
   * @param	methods		the methods defined by this class
   * @param	inners		the inner classes defined by this class
   * @param	initializers	the class and instance initializers defined by this class
   * @param	javadoc		java documentation comments
   * @param	comment		other comments in the source code
   */
  public JClassDeclaration(TokenReference where,
			   int modifiers,
			   String ident,
                           CTypeVariable[] typeVariables,
			   CReferenceType superClass,
			   CReferenceType[] interfaces,
			   JFieldDeclaration[] fields,
			   JMethodDeclaration[] methods,
			   JTypeDeclaration[] inners,
			   JPhylum[] initializers,
			   JavadocComment javadoc,
			   JavaStyleComment[] comment)
  {
    super(where, modifiers, ident, typeVariables, interfaces, fields, methods, inners, initializers, javadoc, comment);
    this.superClass = superClass;
 }

  // ----------------------------------------------------------------------
  // INTERFACE CHECKING
  // ----------------------------------------------------------------------

  /** 
   * In the pass the superclass of this class the interfaces must be set, 
   * so that they are  available for the next pass.
   * It is not possible to check the interface of methods, fields, ... in 
   * the same pass.
   */
  public void join(CContext context) throws PositionedError {
    CReferenceType      objectType;

    objectType = context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT);

    // construct the CClassContext; should be the first thing!
    if (self == null) {
      self = constructContext(context);
    }

    if (superClass == null) {
      if (sourceClass.getQualifiedName() == JAV_OBJECT) {
	// java/lang/Object
	// superClass = null;
	// superClass1 = null;
      } else {
	superClass = objectType;
      }
    } else {
      try {
	superClass =(CReferenceType) superClass.checkType(self);
      } catch (UnpositionedError e) {
	throw e.addPosition(getTokenReference());
      }
    }

    // check access
    if (superClass != null) {
      CClass	clazz = superClass.getCClass();

      check(context, 
            clazz.isAccessible(getCClass()),
	    KjcMessages.CLASS_ACCESSPARENT, superClass.getQualifiedName());
      check(context,
	    !clazz.isFinal(),
	    KjcMessages.CLASS_PARENT_FINAL, superClass.getQualifiedName());
      check(context,
	    !clazz.isInterface(),
	    KjcMessages.CLASS_EXTENDS_INTERFACE, superClass.getQualifiedName());
    }
    sourceClass.setSuperClass(superClass);

    super.join(context);
  }

  /**
   * Sets the super class
   */
  public void setSuperClass(CReferenceType superClass) {
    this.superClass = superClass;
  }

  /**
   * Sets the super class
   */
  public void setInterfaces(CReferenceType[] interfaces) {
    this.interfaces = interfaces;
  }


  /**
   * Second pass (quick), check interface looks good
   * Exceptions are not allowed here, this pass is just a tuning
   * pass in order to create informations about exported elements
   * such as Classes, Interfaces, Methods, Constructors and Fields
   * @exception	PositionedError	an error with reference to the source file
   */
  public void checkInterface(final CContext context) throws PositionedError {

    checkModifiers(context); 

    statInit = constructInitializers(true,
                                     context.getEnvironment().getSourceVersion() >= KjcEnvironment.SOURCE_1_4,
                                     context.getTypeFactory());
// lackner 11-11-01: done in JTypeDeclaration 
//     if (statInit != null) {
//       statInit.checkInterface(self);
//     }

    int	i = 0;
    for (; i < methods.length; i++) {
      if (methods[i] instanceof JConstructorDeclaration) {
	break;
      }
    }
    if (i == methods.length && getDefaultConstructor() == null) {
      setDefaultConstructor(constructDefaultConstructor(context.getEnvironment()));
    }

    instanceInit = constructInitializers(false,
                                         false,
                                         context.getTypeFactory());
    if (instanceInit != null) {
      instanceInit.checkInterface(self);
    }
	
    if (getCClass().getSuperClass() != null) {
      check(context,
            !getCClass().getSuperClass().descendsFrom(getCClass()),
	    KjcMessages.CLASS_CIRCULARITY,
	    ident);
    }
	super.checkInterface(context, superClass);
    
    // Check inners
    for (int k = 0; k  < inners.length; k++) {
      if (!inners[k].getCClass().isStatic()) {
        inners[k].addOuterThis();
      }
    }
    
  }


  /**
   * Checks that the modifiers are valid (JLS 8.1.1).
   *
   * @param	context		the analysis context
   * @exception	PositionedError	an error with reference to the source file
   */
// andreas start
  //private void checkModifiers(final CContext context) throws PositionedError {
  protected void checkModifiers(final CContext context) throws PositionedError {
// andreas end
    int		modifiers = getModifiers();

    // Syntactically valid class modifiers
    check(context,
	  CModifier.isSubsetOf(modifiers,
			       ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE | ACC_ABSTRACT
			       | ACC_STATIC | ACC_FINAL | ACC_STRICT),
	  
	  KjcMessages.NOT_CLASS_MODIFIERS,
	  CModifier.toString(CModifier.notElementsOf(modifiers,
						     ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
						     | ACC_ABSTRACT | ACC_STATIC | ACC_FINAL
						     | ACC_STRICT)));

    // JLS 8.1.1 : The access modifier public pertains only to top level
    // classes and to member classes.
    check(context,
	  (!isNested()
	   || !(context instanceof CBodyContext))
	  || !CModifier.contains(modifiers, ACC_PUBLIC),
	  KjcMessages.INVALID_CLASS_MODIFIERS,
	  CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_PUBLIC)));

    // JLS 8.1.1 : The access modifiers protected and private pertain only to
    // member classes within a directly enclosing class declaration.
    check(context,
	  (isNested()
	   && getOwner().getCClass().isClass() 
	   && !(context instanceof CBodyContext))
	  || !CModifier.contains(modifiers, ACC_PROTECTED | ACC_PRIVATE),
	  KjcMessages.INVALID_CLASS_MODIFIERS,
	  CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_PRIVATE)));

    // JLS 8.1.1 : The access modifier static pertains only to member classes.
    check(context,
	  isNested() || !CModifier.contains(modifiers, ACC_STATIC),
	  KjcMessages.INVALID_CLASS_MODIFIERS,
	  CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_STATIC)));

    // JLS 8.1.1.2 : A compile-time error occurs if a class is declared both
    // final and abstract.
    check(context,
	  CModifier.getSubsetSize(modifiers, ACC_FINAL | ACC_ABSTRACT) <= 1,
	  KjcMessages.INCOMPATIBLE_MODIFIERS,
	  CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_FINAL | ACC_ABSTRACT)));

    // JLS 9.5 : A member type declaration in an interface is implicitly
    // static and public.
    if (isNested() && getOwner().getCClass().isInterface()) {
      setModifiers(modifiers | ACC_STATIC | ACC_PUBLIC);
    } 
    if (getCClass().isNested() && getOwner().getCClass().isClass() 
        && !getCClass().isStatic() && context.isStaticContext()) {
      setModifiers(modifiers | ACC_STATIC);
    }
  }

  /**
   * Check that initializers are correct
   * @exception	PositionedError	an error with reference to the source file
   */
  public void checkInitializers(CContext context) throws PositionedError {

  	//Walter start:
  	 //self = new CClassContext(context, context.getEnvironment(), sourceClass, this);
	 self = constructContext(context); 
  	 //Walter end
  	 

    if (assertMethod != null) {
      getCClass().addMethod(assertMethod.checkInterface(self));
      assertMethod.checkBody1(self);
    }

    compileStaticInitializer(self);

    // Check inners
    for (int i = inners.length - 1; i >= 0 ; i--) {
      inners[i].checkInitializers(self);
    }

    super.checkInitializers(context);
  }

  public void compileStaticInitializer(CClassContext context)
    throws PositionedError
  {
    if (statInit != null) {
      statInit.checkInitializer(context);

      // check that all final class fields are initialized
      CField[]	classFields = context.getCClass().getFields();

      for (int i = 0; i < classFields.length; i++) {
	if (classFields[i].isStatic() && !CVariableInfo.isInitialized(context.getFieldInfo(i))) {
	  check(context,
		!classFields[i].isFinal() || classFields[i].isSynthetic(),
		KjcMessages.UNINITIALIZED_FINAL_FIELD,
		classFields[i].getIdent());

	  context.reportTrouble(new CWarning(getTokenReference(),
					     KjcMessages.UNINITIALIZED_FIELD,
					     classFields[i].getIdent()));
	}
      }

      // mark all static fields initialized
      self.markAllFieldToInitialized(true);
    }
  }

  /**
   * checkTypeBody
   * Check expression and evaluate and alter context
   * @param context the actual context of analyse
   * @return  a pure java expression including promote node
   * @exception	PositionedError	an error with reference to the source file
   */
  public void checkTypeBody(CContext context) throws PositionedError {
    // JSR 41 2.2
    // A parameterized type may not inherit directly or indirectly form 
    // java.lang.Throwable
    check(self,!getCClass().isGenericClass() || !getCClass().descendsFrom(context.getTypeFactory().createReferenceType(TypeFactory.RFT_THROWABLE).getCClass()), KjcMessages.GENERIC_THROWABLE); 
            
    if (getCClass().isNested() && getOwner().getCClass().isClass() 
        && !getCClass().isStatic() && !context.isStaticContext()) {
      addOuterThis();
    }
    try {
      CVariableInfo	instanceInfo = null;
      CVariableInfo[]	constructorsInfo;

      if (instanceInit != null) {
	instanceInit.checkInitializer(self);
      }

      for (int i = fields.length - 1; i >= 0 ; i--) {
        ((CSourceField)fields[i].getField()).setFullyDeclared(true);
      }

      for (int i = 0; i < inners.length; i++) {
	try {
	  inners[i].checkTypeBody(self);
	} catch (CBlockError e) {
	  context.reportTrouble(e);
	}
      }

      // First we compile constructors
      constructorsInfo = compileConstructors(context);

      // Now we compile methods
      for (int i = methods.length - 1; i >= 0 ; i--) {
	try {
	  if (!(methods[i] instanceof JConstructorDeclaration)) {
	    methods[i].checkBody1(self);
	  }
	} catch (CBlockError e) {
	  context.reportTrouble(e);
	}
      }

      // Now we check members
      for (int i = methods.length - 1; i >= 0 ; i--) {
	if (!((CSourceMethod)methods[i].getMethod()).isUsed() &&
	    !methods[i].getMethod().getIdent().equals(JAV_CONSTRUCTOR)) {
	  context.reportTrouble(new CWarning( methods[i].getTokenReference(),
					     KjcMessages.UNUSED_PRIVATE_METHOD,
					     methods[i].getMethod().getIdent()));
	}
      }
      for (int i = fields.length - 1; i >= 0 ; i--) {
	if (!((CSourceField)fields[i].getField()).isUsed()) {
	  context.reportTrouble(new CWarning(fields[i].getTokenReference(),
					     KjcMessages.UNUSED_PRIVATE_FIELD,
					     fields[i].getField().getIdent()));
	}
      }

      self.close(this, null, null, null);
      super.checkTypeBody(context);

    } catch (UnpositionedError cue) {
      throw cue.addPosition(getTokenReference());
    }

    self = null;
  }

  /**
   * Compiles the constructors of this class.
   *
   * @param	context		the analysis context
   * @return	the variable state after each constructor
   * @exception	PositionedError	an error with reference to the source file
   */
  private CVariableInfo[] compileConstructors(CContext context)
    throws PositionedError
  {
    JMethodDeclaration[]	constructors;
    CVariableInfo[]		variableInfos;

    // ------------------------------------------------------------------
    // create an array containing the constructors

    if (getDefaultConstructor() != null) {
      // if there is a default constructor, there are no other constructors.
      constructors = new JMethodDeclaration[1];
      constructors[0] = getDefaultConstructor();
    } else {
      int		count;

      // count the number of constructors ...
      count = 0;
      for (int i = 0; i < methods.length; i++) {
	if (methods[i] instanceof JConstructorDeclaration) {
	  count += 1;
	}
      }
      // ... and put them into an array
      constructors = new JMethodDeclaration[count];
      count = 0;
      for (int i = 0; i < methods.length; i++) {
	if (methods[i] instanceof JConstructorDeclaration) {
	  constructors[count] = methods[i];
	  count += 1;
	}
      }
    }

    // ------------------------------------------------------------------
    // compile each constructor

    variableInfos = new CVariableInfo[constructors.length];
    for (int i = 0; i < constructors.length; i++) {
      try {
	constructors[i].checkBody1(self);
     } catch (CBlockError e) {
	context.reportTrouble(e);
      }
    }

    // ------------------------------------------------------------------
    // mark all instance fields initialized

    self.markAllFieldToInitialized(false);

    // ------------------------------------------------------------------
    // check for cycles in constructor calls

    CMethod[]			callers;
    CMethod[]			callees;

    callers = new CMethod[constructors.length];
    callees = new CMethod[constructors.length];

    for (int i = 0; i < constructors.length; i++) {
      callers[i] = constructors[i].getMethod();
      callees[i] = ((JConstructorDeclaration)constructors[i]).getCalledConstructor();
    }

    for (int i = 0; i < constructors.length; i++) {
      if (callees[i] != null) {
	boolean		found = false;

	for (int j = 0; !found && j < constructors.length; j++) {
	  if (callees[i].equals(callers[j])) {
	    found = true;
	  }
	}
	if (! found) {
	  callees[i] = null;
	}
      }
    }

  _scan_:
    for (;;) {
      for (int i = 0; i < constructors.length; i++) {
	// find the first constructor that does not call
	// another constructor : it cannot be part of a
	// cycle
	if (callers[i] != null && callees[i] == null) {
	  // remove it as successor
	  for (int j = 0; j < constructors.length; j++) {
	    if (j != i && callees[j] != null && callees[j].equals(callers[i])) {
	      callees[j] = null;
	    }
	  }

	  // remove it
	  callers[i] = null;

	  // start search again
	  continue _scan_;
	}
      }

      // if we come here, nothing has been done
      break _scan_;
    }

    // if there are remaining constructors, they are part of a cycle
    for (int i = 0; i < constructors.length; i++) {
      if (callers[i] != null) {
	context.reportTrouble(new PositionedError(constructors[i].getTokenReference(),
						  KjcMessages.CYCLE_IN_CONSTRUCTOR_CALL));
	// signal only one
	break;
      }
    }

    return variableInfos;
  }

  /**
   * Constructs the default constructor with no arguments.
   */
  private JConstructorDeclaration constructDefaultConstructor(KjcEnvironment environment) {
    int         modifier;
    CClass      owner = getCClass();
    TypeFactory factory = environment.getTypeFactory();

    if (owner.isPublic()) {
      /* JLS 8.8.7 : If the class is declared public, then the default constructor 
         is implicitly given the access modifier public (?6.6); */
      modifier = ACC_PUBLIC;
    } else if (owner.isProtected()) {
      /* JLS 8.8.7 : If the class is declared protected, then the default 
         constructor is implicitly given the access modifier protected (?6.6); */
      modifier = ACC_PROTECTED;
    } else if (owner.isPrivate()) {
      /* JLS 8.8.7 : If the class is declared private, then the default constructor is 
         implicitly given the access modifier private (?6.6);*/
      modifier = ACC_PRIVATE;
    } else {
      /* JLS 8.8.7 : otherwise, the default constructor has the default 
         access implied by no  access modifier. */
      modifier = 0;
    }    
    return new JConstructorDeclaration(getTokenReference(),
				       modifier,
				       ident,
				       JFormalParameter.EMPTY,
				       CReferenceType.EMPTY,
				       new JConstructorBlock(getTokenReference(), null, new JStatement[0]),
				       null,
				       null,
                       factory);
  }

  /**
   * Collects all initializers and builds a single method.
   * @param	isStatic	class or instance initializers ?
   */
  protected JInitializerDeclaration constructInitializers(boolean isStatic, boolean always, TypeFactory factory) {
    ArrayList		elems = new ArrayList();
    boolean		needGen = false;

    for (int i = 0; i < body.length; i++) {
      if ((body[i] instanceof JClassBlock)
	  && (((JClassBlock)body[i]).isStaticInitializer() == isStatic)) {
	elems.add(body[i]);
	needGen = true;
      } else {
	if ((body[i] instanceof JFieldDeclaration)
	    && (((JFieldDeclaration)body[i]).getVariable().isStatic() == isStatic)) {
	  needGen |= ((JFieldDeclaration)body[i]).needInitialization();
	  elems.add(new JClassFieldDeclarator(getTokenReference(), (JFieldDeclaration)body[i]));
	}
      }
    }

    if (elems.size() > 0 || always) {
      JStatement[]	stmts = (JStatement[])elems.toArray(new JStatement[elems.size()]);

      return new JInitializerDeclaration(getTokenReference(),
					 new JBlock(getTokenReference(), stmts, null),
					 isStatic,
					 !needGen && !always,
                                         factory);
    } else {
      return null;
    }
  }


  public void analyseConditions()  throws PositionedError {
    // first super
    if (superClass != null) {
      superClass.getCClass().analyseConditions();
    }
    for (int i = 0; i < methods.length; i++) {
      methods[i].analyseConditions();
    }
    super.analyseConditions();
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------

  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    super.accept(p);

    p.visitClassDeclaration(this,
			    modifiers,
			    ident,
                            typeVariables,
			    superClass != null ? superClass.toString() : null,
			    interfaces,
			    body,
			    methods,
			    inners);
  }

  /**
   * Generate the code in pure java form
   * It is useful to debug and tune compilation process
   * @param	p		the printwriter into the code is generated
   */
  public void genInnerJavaCode(KjcPrettyPrinter p) {
    super.accept(p);

    p.visitInnerClassDeclaration(this,
				 modifiers,
				 ident,
                 superClass != null ? superClass.toString() : null,
				 interfaces,
				 inners,
				 body,
				 methods);
  }
  
  public void setMethods(JMethodDeclaration[] methods) {
  	this.methods = methods;
  } 

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  // andreas start
  //private CReferenceType		superClass;
  protected CReferenceType		superClass;
  // andreas end
  
  protected CReferenceType		wouldBeSuperClass;
  
  //  private CClassContext		self;

  JMethodDeclaration            assertMethod;
}
