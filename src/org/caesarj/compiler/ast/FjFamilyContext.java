package org.caesarj.compiler.ast;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;

public class FjFamilyContext {

	protected static FjFamilyContext instance;
	protected Hashtable nameFamilies;
	protected Hashtable classFamilies;
	protected Hashtable families;
	protected boolean assertEqualFamiliesAreSame;

	protected FjFamilyContext() {
		super();
		nameFamilies = new Hashtable();
		classFamilies = new Hashtable();
		families = new Hashtable();
		assertEqualFamiliesAreSame = false;
	}	

	public static FjFamilyContext getInstance() {
		if( instance == null )
			instance = new FjFamilyContext();
		return instance;
	}
	
	public void setFamilyOf( JLocalVariable var, FjFamily family ) {
		nameFamilies.put( var, family );
	}

		
	protected FjFamily lookupFamily( JLocalVariable var ) {
		return (FjFamily) nameFamilies.get( var );
	}
	
	public FjFamily addTypesFamilies( FjFamily first, CReferenceType type ) {
		if( first instanceof FjCompositFamily )
			throw new RuntimeException();
		
		if( !type.isReference() )
			return first;
			
		Vector families = new Vector();
		CClass superClass = type.getCClass();
		FjFamily family = first;
		while( family != null || superClass != null ) {
			if( family != null )
				families.add( family );
			CClass ifc = new FjTypeSystem().cleanInterface( superClass );
			family = (FjFamily) classFamilies.get( ifc );
			superClass = superClass.getSuperClass();
		}
		if( families.size() == 0 )
			return null;
		else
			return new FjCompositFamily( families );
	}

	public void setFamilyOf( CClass clazz, FjFamily family ) {
		CClass ifc = new FjTypeSystem().cleanInterface( clazz );
		classFamilies.put( ifc, mapToOneFamily( family ) );
	}

	protected void assertEqualFamiliesAreSame() {
		
		// this method may be called after completing
		// the compilser's "checkInterface" run;
		// before that time the FjParameterFamily's
		// getIdentification() will fail!
		
		if( assertEqualFamiliesAreSame )
			return;

		Enumeration e = nameFamilies.keys();
		while( e.hasMoreElements() ) {
			Object key = e.nextElement();
			FjFamily family = (FjFamily) nameFamilies.get( key );
			setFamilyOf(
				(JLocalVariable) key,
				mapToOneFamily( family ) );
		}

		assertEqualFamiliesAreSame = true;
	}
	
	protected FjFamily mapToOneFamily( FjFamily f ) {
		
		FjFamily theOneFamily = (FjFamily) families.get( f );
		if( theOneFamily != null ) {
			return theOneFamily;
		} else {
			families.put( f, f );
			return f;
		}
			
	}
}
