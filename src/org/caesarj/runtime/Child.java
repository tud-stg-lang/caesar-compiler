package org.caesarj.runtime;

/**
 * @author andreas
 */
public interface Child {
	
	// fields for navigation
	final Child fjSuper = null;
	final Child fjSub = null;

	// a child's methods
	boolean _isChildOf( Child parent );
	Child _getParent();
	Child _getTarget();
	Child _getTail();
	Child _getDispatcher( Object self );
	void _setFamily( Object family );
	Object _getFamily();
	Object _getObjectId(Object object);
	
	// an object's methods
    public int hashCode();
    public int _hashCode_selfContext(Object obj);
    public boolean equals(Object obj);
    public boolean _equals_selfContext(Object obj, Object obj1);
    public Object clone() throws CloneNotSupportedException;
    public Object _clone_selfContext(Object obj) throws CloneNotSupportedException;
    public String toString();
    public String _toString_selfContext(Object obj);
	public void finalize() throws Throwable;
	public void _finalize_selfContext(Object self) throws Throwable;
}
