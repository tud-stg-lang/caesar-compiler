package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.aspectj.AttributeAdapter;
import org.caesarj.compiler.aspectj.CaesarAdviceKind;
import org.caesarj.compiler.aspectj.CaesarPointcut;

import org.caesarj.compiler.aspectj.CaesarFormalBinding;

import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.kjc.BytecodeOptimizer;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.TypeFactory;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.aspectj.CaesarScope;

/**
 * Represents an advice in the AST.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarAdvice extends FjSourceMethod {

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
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param body
	 * @param families
	 * @param parameterNames
	 * @param extraArgumentFlags
	 */
	public CaesarAdvice(
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		CType[] paramTypes,
		CReferenceType[] exceptions,
		CTypeVariable[] typeVariables,
		JBlock body,
		FjFamily[] families,
		CaesarPointcut pointcut,
		CaesarAdviceKind kind,
		int extraArgumentFlags) {
		super(
			owner,
			modifiers | ACC_FINAL,
			ident,
			returnType,
			paramTypes,
			exceptions,
			typeVariables,
			false,
			false,
			body,
			families);

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
		TokenReference tokenReference) {

		List formalBindings = new ArrayList();

		//bind parameters
		for (int i = 0; i < parameters.length; i++) {
				formalBindings.add(
					new CaesarFormalBinding(
						parameters[i].getSignature(),
						formalParameters[i].getIdent(),
						i,
						tokenReference.getLine(),
						tokenReference.getLine(),
						tokenReference.getFile()));
			}
		

		//set formal bindings
		FjClassContext classContext = (FjClassContext) context;
		classContext.setBindings(
			(CaesarFormalBinding[]) formalBindings.toArray(new CaesarFormalBinding[0]));

		//resolve the pointcut
		pointcut.resolve(new CaesarScope(classContext, caller));

		//create the advice attribute
		if (kind == CaesarAdviceKind.Around) {
			adviceAttribute = AttributeAdapter.createAroundAdviceAttribute(
				kind, pointcut, extraArgumentFlags);
		} else {
			adviceAttribute = AttributeAdapter.createAdviceAttribute(
				kind, pointcut, extraArgumentFlags);
		}
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
