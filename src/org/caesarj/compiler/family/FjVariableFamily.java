package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


public class FjVariableFamily extends FjFamily {
	protected CBlockContext context;
	protected JLocalVariable var;

	public FjVariableFamily( CBlockContext context, JLocalVariable var ) throws UnpositionedError {
		super( (CReferenceType) var.getType().checkType( context ),
			new JNameExpression( FjConstants.STD_TOKEN_REFERENCE, var.getIdent() ) );
		this.context = context;
		this.var = var;
	}

	public String getIdentification() {
		return
			context.getClassContext().getCClass().getQualifiedName() +
			"|" +
			FjConstants.uniqueMethodId( context.getMethodContext().getCMethod() ) +
			"|" +
			var.getIdent();
	}
}

