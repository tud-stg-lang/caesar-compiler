package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.phylum.expression.FjConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.util.TokenReference;

public class FjConstructorBlock extends JConstructorBlock {

	public FjConstructorBlock(
		TokenReference where,
		JConstructorCall constructorCall,
		JStatement[] body) {
		super(where, constructorCall, body);
		cacheConstructorCall = (FjConstructorCall) constructorCall;
	}

	private FjConstructorCall cacheConstructorCall;
	public FjConstructorCall getConstructorCall() {
		return cacheConstructorCall;
	}
}
