package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.phylum.expression.*;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CField;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;

public class FjFieldFamily extends FjFamily {

	protected CField field;

	public FjFieldFamily( CTypeContext context, CField field ) throws UnpositionedError {
		super( (CReferenceType) field.getType().checkType( context ),
			new JNameExpression( FjConstants.STD_TOKEN_REFERENCE, field.getIdent() ) );
		this.field = field;
	}

	public String getIdentification() {
		return field.getOwner().getQualifiedName() + "|" + field.getIdent();
	}
}
