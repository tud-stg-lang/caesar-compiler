package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.phylum.declaration.FjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


public class FjParameterFamily extends FjFamily {
	
	protected JFormalParameter param;
	protected FjMethodDeclaration method;
	protected CClassContext context;
	protected JFormalParameter[] params;
	protected int parameterIndex;
	
	public FjParameterFamily( CClassContext context, FjMethodDeclaration method, JFormalParameter param, JFormalParameter[] params, int parameterIndex )
		throws UnpositionedError {
		super( (CReferenceType) param.getType().checkType( context ),
			new JNameExpression( FjConstants.STD_TOKEN_REFERENCE, param.getIdent() ) );
		this.param = param;
		this.context = context;
		this.method = method;
		this.params = params;
		this.parameterIndex = parameterIndex;
	}
	
	public String getIdentification() {
		return
			context.getClassContext().getCClass().getQualifiedName() +
			"|" +
			FjConstants.uniqueMethodId( method.getIdent(), params ) +
			"|" +
			param.getIdent();
	}
	
	public boolean isParameter() {
		return true;
	}
	
	public int getParameterIndex() {
		return parameterIndex;
	}
}

