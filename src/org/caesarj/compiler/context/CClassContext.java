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
 * $Id: CClassContext.java,v 1.2 2004-02-08 20:28:00 ostermann Exp $
 */

package org.caesarj.compiler.context;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.JExpression;
import org.caesarj.compiler.ast.JFieldDeclaration;
import org.caesarj.compiler.ast.JLocalVariable;
import org.caesarj.compiler.ast.JOuterLocalVariableExpression;
import org.caesarj.compiler.ast.JTypeDeclaration;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.CWarning;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a class context during check
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CContext
 */
public class CClassContext extends CContext {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * @param	parent		the parent context or null at top level
   * @param	clazz		the corresponding clazz
   */
  // andreas start
  //CClassContext(CContext parent, KjcEnvironment environment, CSourceClass clazz, JTypeDeclaration decl) {
  public CClassContext(CContext parent, KjcEnvironment environment, CSourceClass clazz, JTypeDeclaration decl) {
  // andreas end
    super(parent, environment);

    if (decl == null) {
      throw new InconsistencyException();
    }

    this.self = clazz;
    this.decl = decl;
    this.fieldInfo = new CVariableInfo(null);
  }

  /**
   * Verify all final fields are initialized
   * @exception UnpositionedError	this error will be positioned soon
   */
  public void close(JTypeDeclaration decl,
		    CVariableInfo staticC,
		    CVariableInfo instanceC,
		    CVariableInfo[] constructorsC)
    throws UnpositionedError
  {
    JFieldDeclaration[]	fields = decl.getFields();

    for (int i = 0; i < fields.length; i++) {
      CSourceField	field = (CSourceField)fields[i].getField();

      if (isFieldRedefined(field.getIdent())) {
	reportTrouble(new CWarning(decl.getTokenReference(),
				   KjcMessages.FIELD_RENAME_SUPER,
				   field.getIdent(),
				   null));
      }
    }

    // getAbstractMethods test the consistence of abstract methods 
    CMethod[]		methods = self.getAbstractMethods(this, true);

    if (methods.length > 0) { 
      if (! self.isAbstract()) {
        // check all abstract methods are implemented
        check(false, KjcMessages.CLASS_MUST_IMPLEMENT, self.getIdent(), methods[0]);
      }
    }
  }

  /*
   * Returns true iff a field with same name is already defined in a superclass or
   * an implemented interface.
   *
   * @param	ident		the name of the field
   */
  private boolean isFieldRedefined(String ident) throws UnpositionedError {
    try {
      // defined once in super classes ?
      return self.lookupSuperField(getCClass(), null, ident) != null;
    } catch (UnpositionedError e) {
      if (! e.hasDescription(KjcMessages.FIELD_AMBIGUOUS)) {
	throw e;
      }
      // defined more than once!
      return true;
    }
  }

  /** 
   * JLS 8.1.2: A statement or expression occurs in a static context if and 
   * only if the innermost method, constructor, instance initializer, static 
   * initializer, field initializer, or explicit constructor statement 
   * enclosing the statement or expression is a static method, a static 
   * initializer, the variable initializer of a static variable, or an 
   * explicit constructor invocation statement 
   *
   * @return true iff the context is static
   */
  public boolean isStaticContext() {
    return false;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Add an initializer to this context
   */
  public void addInitializer() {
    hasInitializer = true;
  }

  /**
   * Returns true if this class need initializers
   */
  public boolean hasInitializer() {
    return hasInitializer;
  }

  /**
   * getNextSyntheticIndex
   * @return an unique int value incrementaly
   */
  public int getNextSyntheticIndex() {
    return ++index;
  }

  /**
   * getNextSyntheticIndex
   * @return an unique int value incrementaly
   */
  public int getNextStoreIndex() {
    return ++storeIndex;
  }
  /**
   * getNextSyntheticIndex
   * @return an unique int value incrementaly
   */
  public int getAnonymousIndex() {
    return ++anonymous;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (INFOS)
  // ----------------------------------------------------------------------

  /**
   * Returns the field definition state.
   */
  public CVariableInfo getFieldInfo() {
    return fieldInfo;
  }

  /**
   * @param	var		the definition of a field
   * @return	all informations we have about this field
   */
  public int getFieldInfo(int index) {
    return fieldInfo.getInfo(index);
  }

  /**
   * @param	index		The field position in method array of local vars
   * @param	info		The information to add
   *
   * We make it a local copy of this information and at the end of this context
   * we will transfert it to the parent context according to controlFlow
   */
  public void setFieldInfo(int index, int info) {
    fieldInfo.setInfo(index, info);
  }

  /**
   * Marks all class or instance fields of this class initialized.
   *
   */
  public void markAllFieldToInitialized(boolean isStatic) {
    CField[]	fields = getCClass().getFields();

    for (int i = 0; i < fields.length; i++) {
      if (fields[i].isStatic() == isStatic) {
	fieldInfo.setInfo(i, CVariableInfo.INITIALIZED);
      }
    }
  }

  /**
   *
   */
  public void setVariableInfo(CVariableInfo info) {
    fieldInfo = info;
  }

  /**
   * Sets the field state after execution of the instance initializer.
   */
  public void setInitializerInfo(CVariableInfo initializerInfo) {
    this.initializerInfo = initializerInfo;
  }

  /**
   * Gets the field state after execution of the instance initializer.
   */
  public CVariableInfo getInitializerInfo() {
    return initializerInfo;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (LOOKUP)
  // ----------------------------------------------------------------------

  /**
   * lookupClass
   * search for a class with the provided type parameters
   * @param	caller		the class of the caller
   * @param	name		method name
   * @return	the class if found, null otherwise
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CClass lookupClass(CClass caller, String name) throws UnpositionedError {
    CClass	clazz = self.lookupClass(caller, name);

    if (clazz != null) {
      if (clazz.isAccessible(caller)) {
        return clazz;
      } else {
        throw new UnpositionedError(KjcMessages.CLASS_NOACCESS, clazz.getIdent());
      }
    } else {
      return super.lookupClass(caller, name);
    }
  }

  /**
   * JLS 15.12.2 :
   * Searches the class or interface to locate method declarations that are
   * both applicable and accessible, that is, declarations that can be correctly
   * invoked on the given arguments. There may be more than one such method
   * declaration, in which case the most specific one is chosen.
   *
   * @param	caller		the class of the caller
   * @param	ident		method name
   * @param	actuals		method parameters
   * @return	the method or null if not found
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CMethod lookupMethod(CTypeContext context, CClass caller, CType primary, String ident, CType[] actuals)
    throws UnpositionedError
  {
    CMethod	method;

    // lookup in current class
    method = self.lookupMethod(context, caller, primary, ident, actuals, self.getTypeVariables());

    // if not found lookup in outer class
    if (method == null) {
      CClassContext	parent = getParentContext().getClassContext();

      if (parent != null) {
	method = parent.lookupMethod(context, caller, primary, ident, actuals);
      }
    }

    return method;
  }

  /**
   * Searches the class or interface to locate declarations of fields that are
   * accessible.
   * 
   * @param	caller		the class of the caller
   * @param	ident		the simple name of the field
   * @return	the field definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CField lookupOuterField(CClass caller, CClass primary, String ident)
    throws UnpositionedError
  {
    CClassContext	parent = getParentContext().getClassContext();

    if (parent != null) {
      CField field =  parent.lookupField(caller, primary, ident);

      return field != null ? field : parent.lookupOuterField(caller, primary, ident);
    }

    return null;
  }

  /**
   * Searches the class or interface to locate declarations of fields that are
   * accessible.
   * 
   * @param	caller		the class of the caller
   * @param	ident		the simple name of the field
   * @return	the field definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CField lookupField(CClass caller, CClass primary, String ident)
    throws UnpositionedError
  {
    return getCClass().lookupField(caller, primary, ident);
  }

  /**
   * lookupOuterLocalVariable
   * @param	ident		the name of the outer variable
   * @return	a variable from an ident in upperclass context
   */
  public JExpression lookupOuterLocalVariable(TokenReference ref, String ident) {
    if (parent instanceof CBodyContext || parent instanceof CExpressionContext || parent instanceof CClassContext) {
      JLocalVariable    var = parent.lookupLocalVariable(ident);

      if (var != null) {
	return new JOuterLocalVariableExpression(ref, var, getCClass());
      } else {
	CClassContext   ctxt = getParentContext().getClassContext();
	return ctxt == null ? null : ctxt.lookupOuterLocalVariable(ref, ident);
      }
    }
    return null;
  }


  /**
   * Searches the class, interface and Method to locate declarations of TV's that are
   * accessible.
   * 
   * @param	ident		the simple name of the field
   * @return	the TV definition
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CTypeVariable lookupTypeVariable(String ident)
    throws UnpositionedError
  {
    return getCClass().lookupTypeVariable(ident);
  }

  /**
   * lookupLocalVariable
   * @param	ident		the name of the local variable
   * @return	a variable from an ident in current context
   */
  public JLocalVariable lookupLocalVariable(String ident) {
    return null;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (TREE HIERARCHY)
  // ----------------------------------------------------------------------

  /**
   * getClassContext
   * @return	the near parent of type CClassContext
   */
  public CClassContext getClassContext() {
    return this;
  }

  /**
   * getCClass
   * @return	the near parent of type CClassContext
   */
  public CClass getCClass() {
    return self;
  }

  /**
   * getMethod
   * @return	the near parent of type CMethodContext
   */
  public CMethodContext getMethodContext() {
    return getParentContext().getMethodContext();
  }

  /**
   * getTypeDeclaration
   * @return	the near parent of type CMethodContext
   */
  public JTypeDeclaration getTypeDeclaration() {
    return decl;
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CVariableInfo                 initializerInfo;

  private CVariableInfo                 fieldInfo;
  private final JTypeDeclaration	decl;
  private boolean                       hasInitializer;
  private int                           index = 0;
  private int                           anonymous = 0;
  private int                           storeIndex = 0;
  protected final CSourceClass          self;
}
