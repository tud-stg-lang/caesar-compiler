/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: CMember.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.util.InconsistencyException;

/**
 * This class represents an exported member of a class
 * @see	CMember
 * @see	CField
 * @see	CMethod
 * @see	CClass
 */
public abstract class CMember extends org.caesarj.util.Utils implements Constants {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs a field export
   * @param	owner		the owner of this member
   * @param	modifiers	the modifiers on this member
   * @param	ident		the ident of this member
   * @param	deprecated	is this member deprecated
   */
  public CMember(CClass owner,
		 int modifiers,
		 String ident,
		 boolean deprecated,
                 boolean synthetic)
  {
    this.owner = owner;
    this.modifiers = modifiers;
    this.ident = ident;
    this.deprecated = deprecated;
    this.synthetic = synthetic;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Returns the owner of this member
   */
  public CClass getOwner() {
    return owner;
  }
  /**
   * Returns the owner of this member
   */
  public CReferenceType getOwnerType() {
    return (owner == null) ? null : owner.getAbstractType();
  }

  /**
   * @return the ident of this method
   */
  public String getIdent() {
    return ident;
  }

  /**
   * @return the fully qualified name of this member
   */
  public String getQualifiedName() {
    return owner.getQualifiedName() + JAV_NAME_SEPARATOR + ident;
  }

  /**
   * @return the fully qualified name of this member
   */
  public String getPrefixName() {
    return owner.getQualifiedName();
  }

  /**
   * @return the fully qualified name of this member
   */
  public String getJavaName() {
    return getQualifiedName().replace('/' , '.');
  }

  /**
   * @return the modifiers of this member
   */
  public int getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers of this member.
   */
  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  // ----------------------------------------------------------------------
  // ACCESSORS (QUICK)
  // ----------------------------------------------------------------------

  /**
   * @return	the interface
   */
  public CField getField() {
    throw new InconsistencyException();
  }

  /**
   * @return	the interface
   */
  public CMethod getMethod() {
    throw new InconsistencyException();
  }

  /**
   * @return	the interface
   */
  public CClass getCClass() {
    throw new InconsistencyException();
  }

  public abstract CTypeVariable lookupTypeVariable(String ident);
  // ----------------------------------------------------------------------
  // ACCESSORS (QUICK)
  // ----------------------------------------------------------------------

  /**
   * @return	true if this member is static
   */
  public boolean isStatic() {
    return CModifier.contains(modifiers, ACC_STATIC);
  }

  /**
   * @return	true if this member is public
   */
  public boolean isPublic() {
    return CModifier.contains(modifiers, ACC_PUBLIC);
  }

  /**
   * @return	true if this member is protected
   */
  public boolean isProtected() {
    return CModifier.contains(modifiers, ACC_PROTECTED);
  }

  /**
   * @return	true if this member is private
   */
  public boolean isPrivate() {
    return CModifier.contains(modifiers, ACC_PRIVATE);
  }

  /**
   * @return	true if this member is final
   */
  public boolean isFinal() {
    return CModifier.contains(modifiers, ACC_FINAL);
  }

  /**
   * @return	true if this member is deprecated
   */
  public boolean isDeprecated() {
    return deprecated;
  }

  /**
   * @return	true if this member is synthectic
   */
  public boolean isSynthetic() {
    return synthetic;
  }

  /**
   * Checks whether this type is accessible from the specified class (JLS 6.6).
   *
   * Note : top level class and interface types are handled in CClass.
   *
   * @return	true iff this member is accessible
   */
  public boolean isAccessible(CClass from) {
    if (isPublic() || from.isDefinedInside(owner) || owner.isDefinedInside(from)) {
      return true;
    } else if (from.isNested() && isAccessible(from.getOwner())) {
      return true;
    } else if (!isPrivate() && owner.getPackage() == from.getPackage()) {
      return true;
    } else if (isProtected() && from.descendsFrom(owner)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks whether this type is only accessible over an accessor method
   * from the specified class.
   * If the method is not accessible, then it returns false.
   *
   * Note : use isAccessible first do determine the accessability
   *
   * @return	true iff this member is accessible with an accessor
   */
  public boolean requiresAccessor(CClass from, boolean isSuper) {
    // verify(isAccessible(from));
    if (getOwner() == from) {
      return false;
    } else if (from.descendsFrom(getOwner())) {
      return false;
    } else if (!from.descendsFrom(getOwner()) && isSuper) {
      return true;
    } else if (!isPrivate())  {
      return false;
    } else {
      if (from.isNested() && requiresAccessor(from.getOwner(), isSuper)) {
        return true;
      } else if (from.isDefinedInside(owner) || owner.isDefinedInside(from)) {
        return true;
      } else {
        return false;
      }
    }
  }

  public CSourceClass getAccessorOwner(CSourceClass from) {
    if (getOwner() == from) {
      return (CSourceClass) from;
    } else if (isPrivate()) {
      return (CSourceClass) getOwner();
    } else {
      CClass    target = from;

      while (!target.descendsFrom(getOwner())) {
        target = target.getOwner();
      }
      return (CSourceClass) target;
    }
  }
  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  protected CClass			owner;	//!!! make private
  protected int				modifiers;
  protected String			ident;
  private boolean			deprecated;
  protected boolean			synthetic;
}
