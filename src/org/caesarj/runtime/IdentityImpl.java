package org.caesarj.runtime;

public class IdentityImpl {

	public static boolean _identical( Object left, Object right ) {
		if( left == right )
			return true;
		else if( left instanceof Child && right instanceof Child )
			return ((Child) left)._getTarget() == ((Child) right)._getTarget();
		else
			return false;
	}
}
