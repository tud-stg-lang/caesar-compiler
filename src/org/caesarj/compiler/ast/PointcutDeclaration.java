package org.caesarj.compiler.ast;

import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.patterns.Pointcut;

import org.caesarj.kjc.CBinaryTypeContext;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JStatement;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

/**
 * A Pointcut declaration.
 * 
 * @author Jürgen Hallpap
 */
public class PointcutDeclaration extends FjMethodDeclaration {

	public static final PointcutDeclaration[] EMPTY =
		new PointcutDeclaration[0];

	/**The corresponding Pointcut.*/
	private Pointcut pointcut;

	/**
	 * Constructor for PointcutDeclaration.
	 * @param where
	 * @param modifiers
	 * @param typeVariables
	 * @param returnType
	 * @param ident
	 * @param parameters
	 * @param exceptions
	 * @param body
	 * @param javadoc
	 * @param comments
	 */
	public PointcutDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		JavadocComment javadoc,
		Pointcut pointcut) {
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			CReferenceType.EMPTY,
			(modifiers & ACC_ABSTRACT) == 0
				? new JBlock(where, new JStatement[0], null)
				: null,
			javadoc,
			null);

		this.pointcut = pointcut;
	}

	public CaesarPointcut getSourcePointcut() {
		return (CaesarPointcut) getMethod();
	}

	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkInterface(CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {

		FjClassContext caesarContext = (FjClassContext) context;

		//		CSourceMethod result = super.checkInterface(context);

		CBinaryTypeContext typeContext =
			new CBinaryTypeContext(
				context.getClassReader(),
				context.getTypeFactory(),
				context,
				typeVariables,
				(modifiers & ACC_STATIC) == 0);

		CType[] parameterTypes = new CType[parameters.length];
		String[] parameterNames = new String[parameters.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = parameters[i].checkInterface(typeContext);
		}

		CaesarPointcut pointcutMethod =
			new CaesarPointcut(
				context.getCClass(),
				modifiers,
				ident,
				context.getTypeFactory().getVoidType(),
				parameterTypes,
				exceptions,
				typeVariables,
				false,
				false,
				body,
				pointcut,
				new FjFamily[0]);

		FjSourceClass crosscuttingClass =
			(FjSourceClass) context.getCClass();

		ResolvedPointcutDefinition rpd =
			pointcutMethod.resolve(
				context,
				pointcutMethod.getOwner(),
				parameters,
				getTokenReference());

		crosscuttingClass.addResolvedPointcut(rpd);

		setInterface(pointcutMethod);

		return pointcutMethod;
	}

}
