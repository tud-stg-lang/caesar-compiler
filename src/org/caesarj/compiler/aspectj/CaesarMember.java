/*
 * Created on 17.12.2003
 */
package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
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
		String declaringName,
		int modifiers,
		String name,
		String[] parameterSignatures,
		CaesarPointcut pointcut)
		{
			// convert the signatures to types
			TypeX parameterTypes[] = TypeX.forSignatures(parameterSignatures),
					declaringType = TypeX.forName(declaringName);
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