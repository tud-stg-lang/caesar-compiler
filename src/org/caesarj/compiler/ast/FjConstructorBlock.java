package org.caesarj.compiler.ast;

import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorCall;
import org.caesarj.kjc.JStatement;
import org.caesarj.compiler.TokenReference;

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
