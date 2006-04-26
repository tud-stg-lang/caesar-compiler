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
 * $Id: CaesarMember.java,v 1.4 2006-04-26 16:55:25 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;

/** 
* Wraps the AspectJ-memberclasses. Provides factory method to create
* subclass instances of Member.
* @author Karl Klose
*/
public class CaesarMember {
// Attributes
	private Member	member;
// Access
	public Member wrappee()
	{
		return member;
	}
// Construction
	public CaesarMember(Member member)
	{
		this.member = member;
	}
// Constants
	public static final Member.Kind FIELD = Member.FIELD;
	public static final Member.Kind METHOD = Member.METHOD;
// Interface
	public int getModifiers()
	{
		// This function should only be called on ResolvedMembers!
		return ((ResolvedMember)member).getModifiers();
	}
	
	public String getName()
	{
		return member.getName();
	}
// Factory Methods
	/*
	 * Create a wrapped Member-instance
	 * @param kind The kind of Member
	 * @param declaringType The signature of the membertype
	 * @return a Wrapper for the created Member instance
	 */
	public static CaesarMember Member(
		Member.Kind kind, 
		String declaringType,
		int modifiers,
		String name,
		String signature){
			 return new CaesarMember( 
				 new Member(
					 kind, TypeX.forSignature(declaringType), modifiers,
					 name, signature));
		}
	
	/*
	 * Create a wrapped ResolvedMember instance
	 * @param kind The kind of Member
	 * @param declaringType The signature of the membertype
	 * @return a Wrapper for the created Member instance
	 */
	public static CaesarMember ResolvedMember(
		Member.Kind kind, 
		String declaringType,
		int modifiers,
		String name,
		String signature){
			 return new CaesarMember( 
				 new ResolvedMember(
					 kind, TypeX.forSignature(declaringType), modifiers,
					 name, signature));
		}
		
	/*
	 * Create a wrapped ResolvedPointcutDefinition-instance
	 * @param kind The kind of Member
	 * @param declaringName The qualified name of the membertype
	 * @return a Wrapper for the created ResolvedPointcutDefinition instance
	 */
	public static CaesarMember ResolvedPointcutDefinition(
		ResolvedTypeX declaringType,
		int modifiers,
		String name,
		ResolvedTypeX[] parameterTypes,
		CaesarPointcut pointcut)
		{
			// convert the signatures to types
			return new CaesarMember( 
				new ResolvedPointcutDefinition(
					declaringType,
					modifiers,
					name,
					parameterTypes,
					pointcut.wrappee()
					));			
		}
 
/*
 * Return a wrapper for the result of the call to AjMemberMaker
 */	   
	public static CaesarMember privilegedAccessMethodForFieldGet(
		String signature, CaesarMember field)
	{
		return new CaesarMember(
			AjcMemberMaker.privilegedAccessMethodForFieldGet(
				TypeX.forSignature(signature), field.wrappee()));
	}

	public static CaesarMember privilegedAccessMethodForFieldSet(
		String signature, 
		CaesarMember field) 
	{
		return new CaesarMember(
					AjcMemberMaker.privilegedAccessMethodForFieldSet(
						TypeX.forSignature(signature), field.wrappee()));		
	}

	public static CaesarMember privilegedAccessMethodForMethod(
		String signature, CaesarMember member)
		{
			return new CaesarMember(
					AjcMemberMaker.privilegedAccessMethodForMethod(
						TypeX.forSignature(signature), 
						(ResolvedMember)member.wrappee()));
		}
}