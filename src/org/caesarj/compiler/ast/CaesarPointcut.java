package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.Pointcut;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;

/**
 * Represents a Pointcut in the AST.
 * 
 * @author J?rgen Hallpap
 */
public class CaesarPointcut extends FjSourceMethod {

	/** The pointcut.*/
	private Pointcut pointcut;

	/**
	 * Constructor for CaesarSourcePointcut.
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param body
	 */
	public CaesarPointcut(
		CClass owner,
		int modifiers,
		String ident,
		CType returnType,
		CType[] paramTypes,
		CReferenceType[] exceptions,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JBlock body,
		Pointcut pointcut,
		FjFamily[] families) {
		super(
			owner,
			modifiers,
			ident,
			returnType,
			paramTypes,
			exceptions,
			typeVariables,
			deprecated,
			synthetic,
			body,
			families);

		this.pointcut = pointcut;
	}

	public ResolvedPointcutDefinition resolve(
		CContext context,
		CClass caller,
		JFormalParameter[] formalParameters,
		TokenReference tokenReference) {

		List parameterTypes = new ArrayList();
		List formalBindings = new ArrayList();

		for (int i = 0; i < parameters.length; i++) {

			if (!formalParameters[i].isGenerated()) {

				TypeX type = TypeX.forSignature(parameters[i].getSignature());

				parameterTypes.add(type);

				formalBindings.add(
					new FormalBinding(
						type,
						formalParameters[i].getIdent(),
						i,
						tokenReference.getLine(),
						tokenReference.getLine(),
						tokenReference.getFile()));
			}
		}

		FjClassContext classContext = (FjClassContext) context;
		classContext.setBindings(
			(FormalBinding[]) formalBindings.toArray(new FormalBinding[0]));

		pointcut.resolve(new CaesarScope((FjClassContext) context, caller));

		ResolvedPointcutDefinition rpd =
			new ResolvedPointcutDefinition(
				TypeX.forName(getOwner().getQualifiedName()),
				getModifiers(),
				getIdent(),
				(TypeX[]) parameterTypes.toArray(new TypeX[0]),
				pointcut);

		return rpd;
	}

}
