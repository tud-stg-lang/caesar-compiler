package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarConstants;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * The proceed expression.
 * Usage is restricted to around advices.
 * 
 * @author Jürgen Hallpap
 */
public class ProceedExpression
	extends JMethodCallExpression
	implements CaesarConstants {

	/**
	 * Constructor for ProceedExpression.
	 * @param where
	 * @param JExpression[]
	 * @param comments
	 */
	public ProceedExpression(TokenReference where, JExpression[] args) {

		super(
			where,
			null,
			"PROCEED EXPRESSION",
			addAroundClosureArgument(args));

	}

	/**
	 * Appends an additional argument for the aroundClosure.
	 */
	private static JExpression[] addAroundClosureArgument(JExpression[] args) {
		JExpression[] newArgs = new JExpression[args.length + 1];

		System.arraycopy(args, 0, newArgs, 0, args.length);

		newArgs[newArgs.length - 1] =
			new FjNameExpression(
				TokenReference.NO_REF,
				AROUND_CLOSURE_PARAMETER);

		return newArgs;

	}

	/**
	 * Set the ident and prefix of the mehtod call, then analyse.
	 * 
	 * @param context
	 */
	public JExpression analyse(CExpressionContext context)
		throws PositionedError {

		JExpression typePrefix;

		if (context.getClassContext().getCClass().isCrosscutting()) {

			CReferenceType singletonType =
				new CClassNameType(
					context.getClassContext().getCClass().getQualifiedName()
						+ REGISTRY_EXTENSION);

			typePrefix =
				new JTypeNameExpression(getTokenReference(), singletonType);

		} else {
			typePrefix = new FjThisExpression(getTokenReference());
		}

		prefix =
			new FjNameExpression(
				getTokenReference(),
				typePrefix,
				PER_SINGLETON_INSTANCE_FIELD);
		ident =
			(context.getMethodContext().getCMethod().getIdent()
				+ PROCEED_METHOD)
				.intern();
		return super.analyse(context);
	}

	/**
	 * Converts a potential error message.
	 */
	protected void findMethod(
		CExpressionContext context,
		CClass local,
		CType[] argTypes)
		throws PositionedError {

		try {

			super.findMethod(context, local, argTypes);
		} catch (CMethodNotFoundError e) {
			CType[] visibleArgTypes = new CType[argTypes.length - 1];
			System.arraycopy(
				argTypes,
				0,
				visibleArgTypes,
				0,
				argTypes.length - 1);
			throw new CMethodNotFoundError(
				getTokenReference(),
				this,
				PROCEED_METHOD,
				visibleArgTypes);
		}
	}

}
