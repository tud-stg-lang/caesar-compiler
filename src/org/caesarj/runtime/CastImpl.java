package org.caesarj.runtime;

public class CastImpl {
	public static Object checkFamily( Object expectedFamily, Child child )
		throws RuntimeException {
		
		if( child._getFamily() != expectedFamily )
			throw new RuntimeException( "cast to given family not possible" );
		else
			return child;
	}
}
