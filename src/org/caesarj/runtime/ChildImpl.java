package org.caesarj.runtime;

public class ChildImpl implements Child {

	protected  Child _parent;
	protected  Child _target;
	protected  Child _tail;
	protected  Object _family;

	public ChildImpl( Child parent ) {
		super();
		if( parent != null )
			_parent = parent;
		else
			_parent = this;
			
		_target = this;
		_tail = this;
	}

	public boolean _isChildOf(Child parent) {
		if( parent == _parent )
			return true;
		else if( this == _parent )
			return false;
		else
			return _parent._getTarget()._isChildOf( parent );
	}

	public Child _getParent() {
		return _parent;
	}

	public Child _getTarget() {
		return _target;
	}
	
	public Child _getTail() {
		return _tail;
	}
	
	public Child _getDispatcher( Object self ) {

		return (Child) GeneratedDispatching.getInstance().get(
			((Child) self)._getTarget(),
			_getParent()._getTarget() );
	}

	public void _setFamily( Object family ) {
		_family = family;
	}
	
	public Object _getFamily() {
		return _family;
	}

	// an object's methods:

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public Object _clone_selfContext( Object obj ) throws CloneNotSupportedException {
		return clone();
	}

	public boolean equals(Object other) {
		if( !(other instanceof Child) )
			return super.equals( other );

		Child otherChild = (Child) other;		
		return _getTarget() == otherChild._getTarget();
	}

	public boolean _equals_selfContext(Object self, Object other) {
		return equals( other );
	}

	public int hashCode() {
		return super.hashCode();
	}

	public int _hashCode_selfContext(Object obj) {
		return hashCode();
	}

	public String toString() {
		return super.toString();
	}

	public String _toString_selfContext(Object obj) {
		return toString();
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public void _finalize_selfContext( Object self ) throws Throwable {
		finalize();
	}
}
