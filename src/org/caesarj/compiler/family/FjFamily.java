package org.caesarj.compiler.family;

import java.util.StringTokenizer;

import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;

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
