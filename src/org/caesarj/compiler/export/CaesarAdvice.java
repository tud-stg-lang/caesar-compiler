package org.caesarj.compiler.export;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.compiler.aspectj.*;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.optimize.BytecodeOptimizer;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * Represents an advice in the AST.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarAdvice extends CSourceMethod {

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
		TokenReference tokenReference) {

		List formalBindings = new ArrayList();
		
		//bind parameters
		for (int i = 0; i < parameters.length; i++) {

			if (!formalParameters[i].isGenerated()) {
				
				formalBindings.add(
					new CaesarFormalBinding(
						parameters[i].getSignature(),
						formalParameters[i].getIdent(),
						i,
						tokenReference.getLine(),
						tokenReference.getLine(),
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
		pointcut.resolve(new CaesarScope(classContext, caller));

		//create the advice attribute
		//AjAttribute ajAttribute;
		if (kind == CaesarAdviceKind.Around) {
			adviceAttribute = AttributeAdapter.createAroundAdviceAttribute(
					kind, 
					pointcut,
					extraArgumentFlags
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
					extraArgumentFlags
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
