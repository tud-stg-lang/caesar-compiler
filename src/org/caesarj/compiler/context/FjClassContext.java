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
 * $Id: FjClassContext.java,v 1.6 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.context;

import java.util.EmptyStackException;
import java.util.Stack;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.aspectj.CaesarFormalBinding;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.export.CSourceClass;

public class FjClassContext
	extends CClassContext {

	protected CaesarFormalBinding[] bindings;

	public FjClassContext(
		CContext parent,
		KjcEnvironment environment,
		CSourceClass clazz,
		JTypeDeclaration decl) {
		super(parent, environment, clazz, decl);
		fjAdditionalContextStack = new Stack();
	}

	public CClassContext getParent() {
		return this;
	}

	protected Stack fjAdditionalContextStack;
	public void pushContextInfo(Object o) {
		fjAdditionalContextStack.push(o);
	}
	public Object popContextInfo() {
		try {
			return fjAdditionalContextStack.pop();
		} catch (EmptyStackException e) {
			return null;
		}
	}
	public Object peekContextInfo() {
		try {
			return peekContextInfo(0);
		} catch (Exception e) {
			return null;
		}
	}
	public Object peekContextInfo(int howDeep) {
		try {
			return fjAdditionalContextStack.elementAt(
				fjAdditionalContextStack.size() - howDeep - 1);
		} catch (Exception e) {
			return null;
		}
	}

	public CaesarFormalBinding[] getBindings() {
		if (bindings == null) {
			bindings = new CaesarFormalBinding[0];
		}

		return bindings;
	}

	public void setBindings(CaesarFormalBinding[] formals) {
		bindings =  formals ;
	}

	public CCompilationUnitContext getParentCompilationUnitContext() {
		CContext p = parent;
		while (!(p instanceof CCompilationUnitContext)) p = p.getParentContext(); 
		return (CCompilationUnitContext) p;
	}


}
