package org.caesarj.compiler.family;

import java.util.Vector;

import org.caesarj.compiler.types.CReferenceType;


public class FjCompositFamily extends FjFamily {
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

