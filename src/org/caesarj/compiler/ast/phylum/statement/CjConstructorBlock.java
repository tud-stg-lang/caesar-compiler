package org.caesarj.compiler.ast.phylum.statement;

import org.caesarj.compiler.ast.phylum.expression.CjConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.util.TokenReference;

public class CjConstructorBlock extends JConstructorBlock {

	public CjConstructorBlock(
		TokenReference where,
		JConstructorCall constructorCall,
		JStatement[] body) {
		super(where, constructorCall, body);
		cacheConstructorCall = (CjConstructorCall) constructorCall;
	}

	private CjConstructorCall cacheConstructorCall;
	public CjConstructorCall getConstructorCall() {
		return cacheConstructorCall;
	}
}
