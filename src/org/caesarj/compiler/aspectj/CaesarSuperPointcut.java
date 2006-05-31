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
 * $Id: CaesarSuperPointcut.java,v 1.1 2006-05-31 13:23:43 thiago Exp $
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.bridge.IMessage;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.TypePatternList;

/**
 * This class represents a reference to a pointcut in the super class.
 * 
 * It is created by the parser when something like this is found :
 * 
 * public cclass ClsB extends ClsA {
 * 		pointcut p() : super.apointcut() || ...;
 * }
 * 
 * @author Thiago Tonelli Bartolomei <thiago.bartolomei@gmail.com>
 */
public class CaesarSuperPointcut extends ReferencePointcut {

	public CaesarSuperPointcut(String name, TypePatternList arguments) {
		super((TypeX) null, name, arguments);
	}
	
	/**
	 * Resolve the bindings. If we are in a caesar scope, we try to 
	 * resolve the pointcut in the super type. Otherwise, we just use
	 * the regular resolving.
	 */
	public void resolveBindings(IScope scope, Bindings bindings) {
		
		if (scope instanceof CaesarScope) {
			onType = ((CaesarScope) scope).getSuperRegisterType();
		}
		try {
			super.resolveBindings(scope, bindings);
		} catch (Exception e) {
			onType = null;
			scope.message(IMessage.ERROR, this, "Can't find referenced pointcut");
			return;
		}
	}
}
