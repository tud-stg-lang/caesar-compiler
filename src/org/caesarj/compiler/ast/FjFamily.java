package org.caesarj.compiler.ast;

import java.util.StringTokenizer;
import java.util.Vector;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CField;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JMethodCallExpression;
import org.caesarj.kjc.JNameExpression;
import org.caesarj.kjc.JThisExpression;

public abstract class FjFamily {
	
	protected CReferenceType type;
	protected CReferenceType innerType;
	protected JExpression familyAccessor;
	
	protected FjFamily( CReferenceType type, JExpression familyAccessor ) {
		if( type == null )
			throw new IllegalArgumentException( "type must be set!" );
		this.type = type;
		this.familyAccessor = familyAccessor;
	}
	
	public abstract String getIdentification();

	public void setInnerType( CTypeContext context, CReferenceType innerType ) throws UnpositionedError {
		this.innerType = (CReferenceType) innerType.checkType( context );
	}

	public FjFamily first() {
		return this;
	}

	public CReferenceType getType() {
		return type;
	}
	
	public CReferenceType getInnerType() {
		return innerType;
	}
	
	public String toString() {
		StringTokenizer tokens = new StringTokenizer( getIdentification(), ";" );
		StringBuffer s = new StringBuffer( "{" );
		while( tokens.hasMoreTokens() ) {
			String family = tokens.nextToken();
			int lastSeperator = family.lastIndexOf( "|" );
			s.append( family.substring( lastSeperator + 1 ) );
			if( tokens.hasMoreTokens() )
				s.append( ";" );
		}
		s.append( "}" );
		return s.toString();
	}

	
	public boolean equals( Object o ) {
		if( !(o instanceof FjFamily) )
			return false;
		return getIdentification().equals( ((FjFamily) o).getIdentification() );
	}
	
	public JExpression getFamilyAccessor() {
		return familyAccessor;
	}

	public boolean isSubFamily( FjFamily other ) {
		return other != null
			&& other.getIdentification().endsWith( getIdentification() );
	}
	
	public int hashCode() {
		return getIdentification().hashCode();
	}
	
	public boolean isParameter() {
		return false;
	}
	
	public int getParameterIndex() {
		return -1;
	}

	public boolean isThis() {
		return false;
	}	

	public boolean isOuterThis() {
		return false;
	}	
}

class FjFieldFamily extends FjFamily {

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

class FjVariableFamily extends FjFamily {
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

class FjParameterFamily extends FjFamily {
	
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

class FjThisFamily extends FjFamily {
	protected CClassContext context;
	
	public FjThisFamily( CClassContext context ) {
		super( new FjTypeSystem().cleanInterface( 
			context.getCClass() ).getAbstractType(),
			new JThisExpression( FjConstants.STD_TOKEN_REFERENCE ) );
		this.context = context;
	}
	
	public String getIdentification() {
		FjTypeSystem fjts = new FjTypeSystem();
		return fjts.cleanInterface( 
			context.getCClass() ).getQualifiedName() +
			"|" + FjConstants.THIS_NAME;
	}

	public boolean isThis() {
		return true;
	}	
}

class FjOuterThisFamily extends FjFamily {
	protected CClassContext context;
	
	public FjOuterThisFamily( CClassContext context ) {
		super( new FjTypeSystem().cleanInterface( 
			context.getCClass().getOwner() ).getAbstractType(),
			new JMethodCallExpression(
				FjConstants.STD_TOKEN_REFERENCE,
				null,
				FjConstants.GET_FAMILY_METHOD_NAME,
				JExpression.EMPTY ) );
		// inner class context
		this.context = context;
	}
	
	public String getIdentification() {
		FjTypeSystem fjts = new FjTypeSystem();
		return fjts.cleanInterface( 
			context.getCClass() ).getQualifiedName() +
			"|" + FjConstants.OUTER_THIS_NAME;
	}
	
	public boolean isOuterThis() {
		return true;
	}
}

class FjCompositFamily extends FjFamily {
	protected Vector families;
	protected CReferenceType type;
	public FjCompositFamily( Vector families ) {
		super( ((FjFamily) families.elementAt(0)).getType(),
			((FjFamily) families.elementAt(0)).getFamilyAccessor() );
		this.families = families;
		this.innerType = first().getInnerType();
	}

	public FjFamily first() {
		return ((FjFamily) families.elementAt(0));
	}

	public boolean isThis() {
		return first().isThis();
	}

	public boolean isOuterThis() {
		return first().isOuterThis();
	}

	public boolean isParameter() {
		return first().isParameter();
	}

	public int getParameterIndex() {
		return first().getParameterIndex();
	}
	
	public String getIdentification() {
		StringBuffer s = new StringBuffer(
			((FjFamily) families.elementAt( 0 )).getIdentification() );
		for( int i = 1; i < families.size(); i++ ) {
			s.append( ';' );
			s.append( ((FjFamily) families.elementAt( i )).getIdentification() );
		}
		return s.toString();
	}
}
/**
 * This class represents a family that is composed by more than one family.
 * For example: if a parameter is defined as f.f1.B b, where f is a field
 * of the class, and f1 is a field of the type of f, and B is a virtual type
 * of the type of f1.
 * 
 * 
 * @author Walter Augusto Werner
 */
class FjLinkedFamily
	extends FjFamily
{
	private FjFamily otherFamily;
	private FjFamily family;
	
	public FjLinkedFamily(FjFamily family, FjFamily otherFamily)
	{
		super(family.getType(), family.getFamilyAccessor());
		
		this.family = family;
		this.otherFamily = otherFamily;
	}

	public String getIdentification() 
	{
		return family.getIdentification() + ";" 
			+ otherFamily.getIdentification();
	}
	
	public CReferenceType getInnerType()
	{
		return otherFamily.getInnerType();
	}

	public boolean isOuterThis()
	{
		return family.isOuterThis();
	}

	public boolean isParameter()
	{
		return family.isParameter();
	}

	public boolean isThis()
	{
		return family.isThis();
	}

	public void setInnerType(CTypeContext context, CReferenceType innerType)
		throws UnpositionedError
	{
		otherFamily.setInnerType(context, innerType);
	}

	public FjFamily first()
	{
		return this;
	}
	public CReferenceType getType()
	{
		return otherFamily.getType();
	}

}
