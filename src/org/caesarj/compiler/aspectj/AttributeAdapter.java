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
 * $Id: AttributeAdapter.java,v 1.5 2005-04-04 09:44:45 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import java.io.DataOutput;
import java.io.IOException;

import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.PerClause;
import org.caesarj.classfile.AsciiConstant;
import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ConstantPool;

/**
 * Adapts the AjAttributes to the KOPI world.
 * 
 * @author Jürgen Hallpap
 */
public class AttributeAdapter extends Attribute {

	/** The AjAttribute that should be adapted.*/
	private AjAttribute ajAttribute; //adaptee

	/** The name of the attribute.*/
	private AsciiConstant attributeName;

	/**
	 * Method AttributeAdapter.
	 * 
	 * @param adaptee
	 */
	public AttributeAdapter(AjAttribute adaptee) {
		ajAttribute = adaptee;
		attributeName = new AsciiConstant(ajAttribute.getNameString());
	}

	/**
	 * Every class attribute needs a different tag.
	 * 
	 * @return int
	 */
	 public int getTag() {
		//XXX
		return ajAttribute.hashCode();
	}

	/**
	 * Returns the size of the attribute construct.
	 * 
	 * @return int
	 */
	protected int getSize() {
		// 2 byte for the name index
		// 4 byte for the length of the attribute data
		// x byte for the data
		return 2 + 4 + ajAttribute.getBytes().length;
	}

	/**
	 * Adds the attributeName to the constantPool.
	 */
	protected void resolveConstants(ConstantPool cp)
		throws ClassFileFormatException {

		cp.addItem(attributeName);
	}

	/**
	 * @see org.caesarj.classfile.Attribute#write(ConstantPool, DataOutput)
	 */
	protected void write(ConstantPool cp, DataOutput out)
		throws IOException, ClassFileFormatException {
		out.write(ajAttribute.getAllBytes(attributeName.getIndex()));
	}

	/*
	 *	Create an Adapter which adapts an AroundAdviceAttribute.  
	 * @author Karl Klose
	 */
	public static AttributeAdapter createAroundAdviceAttribute(
		CaesarAdviceKind	kind,
		CaesarPointcut		pointcut,
		int					extraArgumentFlags,
		int					line
	)
	{
		AjAttribute attribute = new AjAttribute.AdviceAttribute(
										kind.wrappee(),
										pointcut.wrappee(),
										extraArgumentFlags,
										line,
										line,
										null,
										false,
										new ResolvedMember[0],
										new boolean[0],
										new TypeX[0]);
		return new AttributeAdapter(attribute);
	}
	
	/*
	 *	Create an Adapter which adapts an AdviceAttribute.  
	 * @author Karl Klose
	 */
	public static AttributeAdapter createAdviceAttribute(
		CaesarAdviceKind	kind,
		CaesarPointcut		pointcut,
		int					extraArgumentFlags,
		int					line
	)
	{
		AjAttribute attribute =
			new AjAttribute.AdviceAttribute(
				kind.wrappee(),
				pointcut.wrappee(),
				extraArgumentFlags,
				line,
				line,
				null);
		return new AttributeAdapter(attribute);
	}
	/* 
	 * Create an adapter which adapts an aspect created from 
	 *  a PerClause. (The perclause is wrapped by an CaesarPointcut here.)
	 * @author Karl Klose
	 */
	 public static AttributeAdapter createAspect(CaesarPointcut pointcut)
	 {
	 	PerClause perclause = (PerClause)pointcut.wrappee();
	 	return new AttributeAdapter(new AjAttribute.Aspect(perclause));
	 }

	/**
	 * @param declare
	 * @return
	 */
	public static AttributeAdapter createDeclareAttribute(CaesarDeclare declare) {
		return new AttributeAdapter(
			new AjAttribute.DeclareAttribute(declare.wrappee()));
	}

	/**
	 * @param members	An array of wrapped 'ResolvedMembers'
	 * @return	A wrapped  PrivilegedAttribute
	 */
	public static AttributeAdapter createPrivilegedAttribute(CaesarMember[] members) {
		// we need to create an array of the wrappee at this point
		ResolvedMember	rm[] = new ResolvedMember[members.length];
		for (int i=0; i<members.length; i++)	
			rm[i] = (ResolvedMember)members[i].wrappee();
		// ... because the AjAttribute doesn't know CaesarMembers
		return new AttributeAdapter(
			new AjAttribute.PrivilegedAttribute(rm));
	}

	/**
	 * @param rpd	a ResolvedPointcut wrapped in a CaesarMember
	 * @return	a wrapped Pointcutdeclaration
	 */
	public static AttributeAdapter createPointcutDeclarationAttribute(
		CaesarMember rpd) {
		return new AttributeAdapter(
			new AjAttribute.PointcutDeclarationAttribute(
				(ResolvedPointcutDefinition)rpd.wrappee()));
	}
}
