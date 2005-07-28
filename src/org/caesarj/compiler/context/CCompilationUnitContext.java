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
 * $Id: CCompilationUnitContext.java,v 1.10 2005-07-28 11:46:45 gasiunas Exp $
 */

package org.caesarj.compiler.context;

import java.util.Vector;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CCompilationUnit;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a local context during checkBody
 * It follows the control flow and maintain informations about
 * variable (initialized, used, allocated), exceptions (thrown, catched)
 * It also verify that context is still reachable
 *
 * There is a set of utilities method to access fields, methods and class
 * with the name by clamping the parsing tree
 * @see CContext
 * @see CCompilationUnitContext
 * @see CClassContext
 * @see CMethodContext
 * @see CBodyContext
 * @see CBlockContext
 */
public class CCompilationUnitContext extends CContext {

    public int getDepth(){
        return 0;
    }
    
	// ----------------------------------------------------------------------
	// CONSTRUCTORS
	// ----------------------------------------------------------------------

	/**
	 * Constructs a compilation unit context.
	 */
	public CCompilationUnitContext(
		CompilerBase compiler,
		KjcEnvironment environment,
		CCompilationUnit cunit,
		Vector classes) {
		super(null, environment);
		this.compiler = compiler;
		this.cunit = cunit;
		this.classes = classes;
	}

	/**
	 * Constructs a compilation unit context.
	 */
  //Walter: the visibility was changed from package protected to public
	public CCompilationUnitContext(CompilerBase compiler,
		KjcEnvironment environment,
		CCompilationUnit cunit) {
		this(compiler, environment, cunit, null);
	}

	// ----------------------------------------------------------------------
	// ACCESSORS (INFOS)
	// ----------------------------------------------------------------------

	/**
	 * Returns the field definition state.
	 */
	public CVariableInfo getFieldInfo() {
		return null;
	}

	/**
	 * @param	field		the definition of a field
	 * @return	a field from a field definition in current context
	 */
	public int getFieldInfo(CField field) {
		return 0;
	}

	// ----------------------------------------------------------------------
	// ACCESSORS (LOOKUP)
	// ----------------------------------------------------------------------

	/**
	 * @param	caller		the class of the caller
	 * @return	a class according to imports or null if error occur
	 * @exception UnpositionedError	this error will be positioned soon
	 */
	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError {
		return cunit.lookupClass(caller, name);
	}

	// ----------------------------------------------------------------------
	// ACCESSORS (TREE HIERARCHY)
	// ----------------------------------------------------------------------

	/**
	 * getParentContext
	 * @return	the parent
	 */
	public CContext getParentContext() {
		return null;
	}

	/**
	 * getClass
	 * @return	the near parent of type CClassContext
	 */
	public CClassContext getClassContext() {
		return null;
	}
	
	public CInitializerContext getInitializerContext() {
	    return null;
	}

	/**
	 * getMethod
	 * @return	the near parent of type CMethodContext
	 */
	public CMethodContext getMethodContext() {
		return null;
	}

	/**
	 * @return	the compilation unit
	 */
	public CCompilationUnitContext getCompilationUnitContext() {
		return this;
	}

	public CBlockContext getBlockContext() {
		return null;
	}
	
	// ----------------------------------------------------------------------
	// ERROR HANDLING
	// ----------------------------------------------------------------------

	/**
	 * Reports a semantic error detected during analysis.
	 *
	 * @param	trouble		the error to report
	 */
	public void reportTrouble(PositionedError trouble) {
		compiler.reportTrouble(trouble);
	}

	// ----------------------------------------------------------------------
	// CLASS HANDLING
	// ----------------------------------------------------------------------

	/**
	 * Adds a class to generate
	 */
	public void addSourceClass(CSourceClass clazz) {
		classes.addElement(clazz);
	}

	/**
	 * Returns the cunit.
	 * @return CCompilationUnit
	 */
	public CCompilationUnit getCunit() {
		return cunit;
	}
	
	public String toString() {
	    return "CU-ctx";
	}
	
	// ----------------------------------------------------------------------
	// DATA MEMBERS
	// ----------------------------------------------------------------------

	private /*final*/
	CompilerBase compiler;
	private /*final*/
	Vector classes;
	private /*final*/
	CCompilationUnit cunit;

}
