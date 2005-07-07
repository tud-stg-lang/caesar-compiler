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
 * $Id: CCjAdvice.java,v 1.7 2005-07-07 14:25:18 thiago Exp $
 */

package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.compiler.aspectj.AttributeAdapter;
import org.caesarj.compiler.aspectj.CaesarAdviceKind;
import org.caesarj.compiler.aspectj.CaesarFormalBinding;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarPointcutScope;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.optimize.BytecodeOptimizer;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * Represents an advice in the AST.
 * 
 * @author Jürgen Hallpap
 */
public class CCjAdvice extends CSourceMethod {

	/** The advice pointcut.*/
	private CaesarPointcut pointcut;

	/** The advice kind.*/
	private CaesarAdviceKind kind;

	/** Generated attributes for advice method.*/
	private Attribute adviceAttribute;

	/** The extraArgumentFlags of the advice, needed for attribute creation.*/
	private int extraArgumentFlags;


	/**
	 * Constructor for CaesarSourceAdvice.
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param params the formal parameters for this advice (necessary for local variable table)
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param body
	 * @param families
	 * @param parameterNames
	 * @param extraArgumentFlags
	 */
	public CCjAdvice(
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		JFormalParameter[] params,
		CType[] paramTypes,
		CReferenceType[] exceptions,
		JBlock body,
		CaesarPointcut pointcut,
		CaesarAdviceKind kind,
		int extraArgumentFlags) {
		super(
			owner,
			modifiers | ACC_FINAL,
			ident,
			returnType,
			params,
			paramTypes,
			exceptions,
			false,
			false,
			body);

		this.pointcut = pointcut;
		this.kind = kind;
		this.extraArgumentFlags = extraArgumentFlags;
	}

	/**
	 * Generate the methodInfo
	 */
	public MethodInfo genMethodInfo(
		BytecodeOptimizer optimizer,
		TypeFactory factory)
		throws ClassFileFormatException {

		MethodInfo methodInfo = super.genMethodInfo(optimizer, factory);
		methodInfo.getAttributes().add(adviceAttribute);

		return methodInfo;
	}

	/**
	 * Creates the corresponding attribute.
	 * 
	 * @param context
	 * @param caller
	 */
	public void createAttribute(
		CContext context,
		CClass caller,
		JFormalParameter[] formalParameters,
		TokenReference tokenReference,
		int orderNr) {

		List formalBindings = new ArrayList();
		
		//bind parameters
		for (int i = 0; i < parameters.length; i++) {

			if (!formalParameters[i].isGenerated()) {
				
				formalBindings.add(
					new CaesarFormalBinding(
						parameters[i].getSignature(),
						formalParameters[i].getIdent(),
						i,
						orderNr,
						orderNr,
						tokenReference.getFile()
						));
			}
		}

		//set formal bindings
		FjClassContext classContext = (FjClassContext) context;
		//classContext.setBindings((FormalBinding[]) formalBindings.toArray(new FormalBinding[0]));
		classContext.setBindings(
			//CaesarFormalBinding.wrappees(
			(CaesarFormalBinding[]) formalBindings.toArray(new CaesarFormalBinding[0]));
		//resolve the pointcut
		pointcut.resolve(new CaesarPointcutScope(classContext, caller));

		//create the advice attribute
		//AjAttribute ajAttribute;
		if (kind == CaesarAdviceKind.Around) {
			adviceAttribute = AttributeAdapter.createAroundAdviceAttribute(
					kind, 
					pointcut,
					extraArgumentFlags,
					orderNr,
					tokenReference.getLine()
				);
				/*
			ajAttribute =
				new AjAttribute.AdviceAttribute(
					kind.wrappee(),
					pointcut,
					extraArgumentFlags,
					0,
					0,
					null,
					false,
					new ResolvedMember[0],
					new boolean[0],
					new TypeX[0]);
				*/
		} else {
			adviceAttribute = AttributeAdapter.createAdviceAttribute(
					kind, 
					pointcut,
					extraArgumentFlags,
					orderNr,
					tokenReference.getLine()
				);
			/*
			ajAttribute =
				new AjAttribute.AdviceAttribute(
					kind.wrappee(),
					pointcut,
					extraArgumentFlags,
					0,
					0,
					null);
			*/
		}

		//wrap the attribute
		//adviceAttribute = new AttributeAdapter(ajAttribute);
	}

	/**
	 * Returns the kind.
	 * 
	 * @return AdviceKind
	 */
	public CaesarAdviceKind getKind() {
		return kind;
	}

	/**
	 * Returns whether this is an around advice.
	 * 
	 * @return boolean
	 */
	public boolean isAroundAdvice() {
		return kind.equals(CaesarAdviceKind.Around);
	}

	/**
	 * Sets the specified Flag in the extraArgumentFlags field.
	 * 
	 * @param newFlag
	 */
	public void setExtraArgumentFlag(int newFlag) {
		this.extraArgumentFlags |= newFlag;
	}

}
