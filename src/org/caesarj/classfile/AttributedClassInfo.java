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
 * $Id: AttributedClassInfo.java,v 1.4 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Vector;


/**
 * Extends ClassInfo to support generating the class to a byte[]
 * and to add some extra attributes.
 * 
 * @author Jürgen Hallpap
 */
public class AttributedClassInfo extends ClassInfo {

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param modifiers
	 * @param thisClass
	 * @param superClass
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param innerClasses
	 * @param sourceFile
	 * @param genericSignature
	 * @param deprecated
	 * @param synthetic
	 */
	public AttributedClassInfo(
		short modifiers,
		String thisClass,
		String superClass,
		ClassConstant[] interfaces,
		FieldInfo[] fields,
		MethodInfo[] methods,
		InnerClassInfo[] innerClasses,
		String sourceFile,
		String genericSignature,
		boolean deprecated,
		boolean synthetic,
		Attribute[] extraAttributes) {

		super(
			modifiers,
			thisClass,
			superClass,
			interfaces,
			fields,
			methods,
			innerClasses,
			sourceFile,
			genericSignature,
			deprecated,
			synthetic);

		//Add the extraAttributes to the class attributes
		for (int i = 0; i < extraAttributes.length; i++) {
			getAttributes().add(extraAttributes[i]);
		}
	}

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param modifiers
	 * @param thisClass
	 * @param superClass
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param innerClasses
	 * @param sourceFile
	 * @param genericSignature
	 * @param deprecated
	 * @param synthetic
	 */
	public AttributedClassInfo(
		short modifiers,
		String thisClass,
		String superClass,
		Vector interfaces,
		Vector fields,
		Vector methods,
		InnerClassInfo[] innerClasses,
		String sourceFile,
		String genericSignature,
		boolean deprecated,
		boolean synthetic) {
		super(
			modifiers,
			thisClass,
			superClass,
			interfaces,
			fields,
			methods,
			innerClasses,
			sourceFile,
			genericSignature,
			deprecated,
			synthetic);
	}

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param in
	 * @param interfaceOnly
	 * @throws IOException
	 * @throws ClassFileFormatException
	 */
	public AttributedClassInfo(DataInput in, boolean interfaceOnly)
		throws IOException, ClassFileFormatException {
		super(in, interfaceOnly);
	}

}
