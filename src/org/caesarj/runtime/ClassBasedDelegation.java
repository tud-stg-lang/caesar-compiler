package org.caesarj.runtime;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * implementation of "class based delegation"
 * method dispatching
 * 
 * @author andreas
 */
public class ClassBasedDelegation {
	
	/**
	 * returns set of methods defined
	 * in the class parameter
	 * 
	 * @param clazz - the class to be inspected
	 */
	public MethodArray Ms( Class clazz ) {
		Vector v = new Vector();
		return new MethodArray( clazz.getMethods() );
	}
	public MethodArray Ms( Class[] classes ) {
		return Ms( classes, 0 );
	}
	private MethodArray Ms( Class[] classes, int index ) {
		if( index == classes.length )
			return new MethodArray();
		else
			return Ms( classes, index+1 ).append( Ms(classes[0]) );
	}

	/**
	 * returns the method of the statically known
	 * class hierarchy that corresponds to the
	 * method parameter or null if there is no such
	 * method
	 * 
	 * @param clazz - the class to be inspected
	 * @param m - the method that is looked for
	 */
	public Method Mc( Class clazz, Method m ) {
		try {
			return clazz.getMethod( m.getName(), m.getParameterTypes() );
		} catch( NoSuchMethodException e ) {
			return null;
		}
	}
	
	/**
	 * returns the method declared directly in
	 * the class parameter or null if there is
	 * no such method

	 * @param clazz - the class to be inspected
	 * @param m - the method that is looked for
	 */
	public Method MD( Class clazz, Method m ) {
		try {
			return clazz.getDeclaredMethod( m.getName(), m.getParameterTypes() );
		} catch( NoSuchMethodException e ) {
			return null;
		}
	}
	
	/**
	 * returns if method m is "compatible" regarding
	 * "class based delegation", i.e. if m eventually
	 * found in target is semantically the same as m
	 * present in tail
	 * 
	 * @param target - "this" of current processing
	 * @param tail - "implementation holder" of current processing
	 * @param m - the method that is checked
	 */
	public boolean CM( Child target, Child tail, Method m ) {
		if( target == tail ) {
			return true;
		} else if( tail._isChildOf( target ) ) {
			return CM( tail, target, m );
		} else if( target._isChildOf( tail ) && !Ms( superTypeOf( target.getClass() ) ).contains( m ) ) {
			return false;
		} else {
			return CM( target._getParent()._getTail(), tail, m );
		}
	}
	
	/**
	 * returns the object designated to invoke
	 * method m regarding "class based delegation"
	 * 
	 * @param target - "this" of current processing
	 * @param tail - "implementation holder" of current processing
	 * @param m - the method that is to be invoked
	 */
	public Child M( Child target, Child tail, String uniqueMethodId ) {
		try {
			StringTokenizer t = new StringTokenizer( uniqueMethodId, "/" );
			String methodName = t.nextToken();
			Vector parameters = new Vector();
			while( t.hasMoreTokens() ) {
				parameters.add( Class.forName( t.nextToken() ) );
			}
			Class[] classParams = new Class[ parameters.size() ];
			for( int i = 0; i < parameters.size(); i++ ) {
				classParams[ i ] = (Class) parameters.elementAt( i );
			}
			return M( target, tail, tail.getClass().getMethod( methodName, classParams ) );			
		} catch( Throwable t ) {
			t.printStackTrace();
			return null;
		}
	}
	public Child M( Child target, Child tail, Method m ) {
		if( CM( target, tail, m ) && MD( target.getClass(), m ) != null ) {
			return target;
		} else if( target != tail ) {
			return M( target._getParent()._getTarget(), tail, m );
		} else {
			return M( target._getParent()._getTarget(), target._getParent()._getTail(), m );
		}
	}

	public static Child fjSuper( Child child ) {
		return child._getTail()._getTarget()._getDispatcher( child._getTarget() );
	}
	
	public static Child fjSub( Child child ) {
		Child lastSub = null;
		Child sub = child._getTarget();
		while( sub._getParent() != child._getTail() ) {
			lastSub = sub;
			sub = sub._getParent();
		}
		if( lastSub == null )
			return child._getTarget();
		else
			return lastSub._getDispatcher( child._getTarget() );
	}
	
	public Class superTypeOf( Class clazz ) {
		if( GeneratedDispatching.getInstance().isGeneratedClass( clazz ) )
			// supertype present in the superinterfaces
			// of the own clean interface: { Child, SuperType_CleanIfc }
			return clazz.getInterfaces()[ 0 ].getInterfaces()[ 1 ];
		else
			return clazz.getSuperclass();
	}
	
	class MethodArray {
		Method[] methods;
		MethodArray() {
			this.methods = new Method[ 0 ];
		}
		MethodArray( Method[] methods ) {
			this.methods = methods;
		}
		boolean contains( Method m ) {
			return contains( m, 0, methods.length );
		}
		boolean contains( Method m, int start, int end ) {
			if( start == end )
				return false;
			else
				return equals( m, methods[ start ] ) || contains( m, start + 1, end );			
		}
		boolean equals( Method m1, Method m2 ) {
			if( !m1.getName().equals( m2.getName() ) )
				return false;
			else if( m1.getReturnType() != m2.getReturnType() )
				return false;
			else return equals( m1.getParameterTypes(), m2.getParameterTypes() );
		}
		boolean equals( Class[] m1Parameters, Class[] m2Parameters ) {
			if( m1Parameters.length != m2Parameters.length )
				return false;
			else {
				for( int i = 0; i < m1Parameters.length; i++ ) {
					if( m1Parameters[ i ] != m2Parameters[ i ] )
						return false;
				}
				return true;
			}
		}
		MethodArray append( MethodArray tail ) {
			Method[] head = methods;
			methods = new Method[ methods.length + tail.methods.length ];
			int i = 0;
			for( ; i < head.length; i++ ) {
				methods[ i ] = head[ i ];
			}
			for( int j = 0; i < methods.length; i++, j++ ) {
				methods[ i ] = tail.methods[ j ];
			}
			return this;
		}
	}
}