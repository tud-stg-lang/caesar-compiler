package org.caesarj.compiler.ast;

import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CMethodNotFoundError;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JTypeNameExpression;
import org.caesarj.compiler.CaesarConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;

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
