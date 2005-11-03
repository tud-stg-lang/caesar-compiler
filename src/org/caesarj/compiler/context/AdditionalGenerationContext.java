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
 * $Id: AdditionalGenerationContext.java,v 1.4 2005-11-03 11:41:05 gasiunas Exp $
 */

package org.caesarj.compiler.context;

import java.lang.ref.WeakReference;

import org.caesarj.compiler.export.CClass;

/**
 * This here is comes near to a hack, but poor
 * extensibility of KOPI gives me no other chance than doing it this way.
 * 
 * We need this class in order to be able to set targets for super and field access 
 * to the current class or super of the current class.
 * 
 * @author Ivica Aracic
 */
public class AdditionalGenerationContext {
	
	private WeakReference<CClass> currentClass = null;
	
	private static AdditionalGenerationContext singleton = new AdditionalGenerationContext();

	public static AdditionalGenerationContext instance() {
		return singleton;
	}

	private AdditionalGenerationContext() {
	}
	
	
	public CClass getCurrentClass() {
		return currentClass.get();
	}

	public void setCurrentClass(CClass currentClass) {
		this.currentClass = new WeakReference<CClass>(currentClass);
	}
}
