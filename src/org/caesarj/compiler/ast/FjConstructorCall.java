package org.caesarj.compiler.ast;

import org.caesarj.util.TokenReference;

public class FjConstructorCall extends JConstructorCall {

	public FjConstructorCall(
		TokenReference where,
		boolean functorIsThis,
		JExpression[] arguments) {
		this(where, functorIsThis, null, arguments);
	}

	public FjConstructorCall(
		TokenReference where,
		boolean functorIsThis,
		JExpression expr,
		JExpression[] arguments) {
		super(where, functorIsThis, expr, arguments);
		
		cacheFunctorIsThis = functorIsThis;
		cacheExpr = expr;
		cacheArguments = arguments;
	}


	public JExpression[] getArguments() {
		return cacheArguments;
	}

	public boolean isThis() {
		return cacheFunctorIsThis;
	}
	
	//Walter
	public void setArguments(JExpression[] arguments)
	{
		this.arguments = arguments;
		cacheArguments = arguments;
	}

	private boolean cacheFunctorIsThis;
	private JExpression cacheExpr;
	private JExpression[] cacheArguments;
}