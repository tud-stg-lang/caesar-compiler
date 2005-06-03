/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: DynArray.java,v 1.2 2005-06-03 08:24:47 klose Exp $
 */
package org.caesarj.runtime.aspects;



/**
 * @author vaidas
 *
 * TODO [documentation]
 */
public class DynArray {
	private transient Object elementData[];

	private int size;

	public DynArray(int initialCapacity) {
		this.elementData = new Object[initialCapacity];
	}

	public DynArray() {
		this(10);
	}

	public DynArray(DynArray c) {
		size = c.size;
		// Allow 10% room for growth
		elementData = new Object[c.elementData.length];
		System.arraycopy(c.elementData, 0, elementData, 0, size);
	}

	/**
	 * Trims the capacity of this <tt>DynArray</tt> instance to be the
	 * list's current size.  An application can use this operation to minimize
	 * the storage of an <tt>DynArray</tt> instance.
	 */
	public void trimToSize() {
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			Object oldData[] = elementData;
			elementData = new Object[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>DynArray</tt> instance, if
	 * necessary, to ensure  that it can hold at least the number of elements
	 * specified by the minimum capacity argument. 
	 *
	 * @param   minCapacity   the desired minimum capacity.
	 */
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return  the number of elements in this list.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this list has no elements.
	 *
	 * @return  <tt>true</tt> if this list has no elements;
	 *          <tt>false</tt> otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 *
	 * @param elem element whose presence in this List is to be tested.
	 * @return  <code>true</code> if the specified element is present;
	 *		<code>false</code> otherwise.
	 */
	public boolean contains(Object elem) {
		return indexOf(elem) >= 0;
	}

	/**
	 * Searches for the first occurence of the given argument, testing 
	 * for equality using the <tt>equals</tt> method. 
	 *
	 * @param   elem   an object.
	 * @return  the index of the first occurrence of the argument in this
	 *          list; returns <tt>-1</tt> if the object is not found.
	 * @see     Object#equals(Object)
	 */
	public int indexOf(Object elem) {
		if (elem == null) {
			for (int i = 0; i < size; i++)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (elem.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in
	 * this list.
	 *
	 * @param   elem   the desired element.
	 * @return  the index of the last occurrence of the specified object in
	 *          this list; returns -1 if the object is not found.
	 */
	public int lastIndexOf(Object elem) {
		if (elem == null) {
			for (int i = size - 1; i >= 0; i--)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (elem.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in the correct order.
	 *
	 * @return an array containing all of the elements in this list
	 * 	       in the correct order.
	 */
	public Object[] toArray() {
		Object[] result = new Object[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order; the runtime type of the returned array is that of the
	 * specified array.  If the list fits in the specified array, it is
	 * returned therein.  Otherwise, a new array is allocated with the runtime
	 * type of the specified array and the size of this list.<p>
	 *
	 * If the list fits in the specified array with room to spare (i.e., the
	 * array has more elements than the list), the element in the array
	 * immediately following the end of the collection is set to
	 * <tt>null</tt>.  This is useful in determining the length of the list
	 * <i>only</i> if the caller knows that the list does not contain any
	 * <tt>null</tt> elements.
	 *
	 * @param a the array into which the elements of the list are to
	 *		be stored, if it is big enough; otherwise, a new array of the
	 * 		same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list.
	 * @throws ArrayStoreException if the runtime type of a is not a supertype
	 *         of the runtime type of every element in this list.
	 */
	public Object[] toArray(Object a[]) {
		if (a.length < size)
			a = (Object[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), size);

		System.arraycopy(elementData, 0, a, 0, size);

		if (a.length > size)
			a[size] = null;

		return a;
	}

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws    IndexOutOfBoundsException if index is out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object get(int index) {
		return elementData[index];
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws    IndexOutOfBoundsException if index out of range
	 *		  <tt>(index &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object set(int index, Object element) {
		Object oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param o element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of Collection.add).
	 */
	public boolean add(Object o) {
		ensureCapacity(size + 1); 
		elementData[size++] = o;
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * @throws    IndexOutOfBoundsException if index is out of range
	 *		  <tt>(index &lt; 0 || index &gt; size())</tt>.
	 */
	public void add(int index, Object element) {
		ensureCapacity(size + 1); 
		System.arraycopy(elementData, index, elementData, index + 1, size
				- index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 *
	 * @param index the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws    IndexOutOfBoundsException if index out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object remove(int index) {
		Object oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		elementData[--size] = null; // Let gc do its work

		return oldValue;
	}
	
	/**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that <tt>(o==null ? e==null :
     * o.equals(e))</tt>, if the collection contains one or more such
     * elements.  Returns <tt>true</tt> if the collection contained the
     * specified element (or equivalently, if the collection changed as a
     * result of the call).<p>
     *
     * This implementation iterates over the collection looking for the
     * specified element.  If it finds the element, it removes the element
     * from the collection using the iterator's remove method.<p>
     *
     * Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's iterator method does not implement the <tt>remove</tt>
     * method and this collection contains the specified object.
     *
     * @param o element to be removed from this collection, if present.
     * @return <tt>true</tt> if the collection contained the specified
     *         element.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     * 		  not supported by this collection.
     */
	public boolean remove(Object o) {
		int idx = indexOf(o);
		if (idx == -1) {
			return false;
		}
		remove(idx);
		return true;
	}

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns.
	 */
	public void clear() {
		// Let gc do its work
		for (int i = 0; i < size; i++)
			elementData[i] = null;

		size = 0;
	}		
}