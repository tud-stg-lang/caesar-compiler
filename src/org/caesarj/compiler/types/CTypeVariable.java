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
 * $Id: CTypeVariable.java,v 1.2 2004-02-08 20:28:00 ostermann Exp $
 */

package org.caesarj.compiler.types;

import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMember;
import org.caesarj.util.SimpleStringBuffer;
import org.caesarj.util.UnpositionedError;

public class CTypeVariable extends CReferenceType {

  public CTypeVariable(String name, CReferenceType[] bounds) {
    super();
    this.bounds = bounds;
    this.name = name;
    owner = null;
  }

  private CTypeVariable(CTypeVariable tv) {
    super();
    bounds = tv.bounds;
    name = tv.name;
    index = tv.index;
    owner = null;
  }
  // ----------------------------------------------------------------------
  // BODY CHECKING
  // ----------------------------------------------------------------------

  /**
   * @param	context		the context (may be be null)
   * @exception UnpositionedError	this error will be positioned soon
   */
  public CType checkType(CTypeContext context) throws UnpositionedError {
      // check bounds
    if (checked) {
      return this;
    }
    checked = true;
    for (int i = 0; i < bounds.length; i++) {
      bounds[i].checkType(context);
    }
    if (bounds.length == 0) {
      setClass(context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).getCClass());
    } else if (bounds.length == 1) {
      bounds[0]  = (CReferenceType) bounds[0].checkType(context);
      setClass(bounds[0].getCClass());
    } else {
      CClass  first = null;
     
      bounds[0]  = (CReferenceType) bounds[0].checkType(context);

      if (!bounds[0].getCClass().isInterface()) {
        setClass(bounds[0].getCClass());
      } else {
        first = bounds[0].getCClass();
      }
      for (int i=1; i<bounds.length; i++) {
        bounds[i] = (CReferenceType) bounds[i].checkType(context);
        
        if (!bounds[i].getCClass().isInterface()) {
          throw new UnpositionedError(KjcMessages.TV_NOT_AN_INTERFACE, bounds[i]);
        }
        if ((first != null) && (first.getQualifiedName().compareTo(bounds[0].getCClass().getQualifiedName()) > 0)) {
          first = bounds[0].getCClass();
        }
      }
     if (first != null) {
        setClass(first);
      }
    }
    checked = true;
    return this;
  }
 
  public void setMethodTypeVariable(boolean methodTypeVariable) {
    this.methodTypeVariable = methodTypeVariable;
  } 

  public boolean isMethodTypeVariable() {
    return methodTypeVariable;
  }

  public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
    if (this == dest) {
      return true;
    }
    if (dest.isTypeVariable()) {
      if (!inst) {
        return false;
      } else {
        CReferenceType[]    destBounds = ((CTypeVariable)dest).getBounds();

        for (int i=0; i < destBounds.length; i++) {
          boolean       isAssignable = false;

          for (int j=0; j < bounds.length; j++) {
            if (bounds[j].isAssignableTo(context, destBounds[i], true)) {
              isAssignable = true;
              break;
            }
          }
          if (!isAssignable) return false;
        }
        return true;
      }
    } else { 
      if (!dest.isClassType()) {
        return false;
      }
      if (bounds.length == 0) {
        return context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).isAssignableTo(context, dest);
      }
      for (int i = 0; i <bounds.length; i++) {
        if (bounds[i].isAssignableTo(context, dest)) {
          return true;
        }
      }
      return false;
    }
 }

  public boolean isAssignableTo(CTypeContext context, CType dest, CReferenceType[] subst) {
    if (this == dest) {
      return true;
    }
    if (dest.isTypeVariable()) {
      int       index = ((CTypeVariable)dest).getIndex();

      dest = subst[index];
    } 
    if (dest.isTypeVariable()) {
      return dest == this;
    } else {
      if (bounds.length ==0) {
        return context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT).isAssignableTo(context, dest);
      }
      for (int i = 0; i <bounds.length; i++) {
        if (bounds[i].isAssignableTo(context, dest, subst)) {
          return true;
        }
      }
      return false;
    }
  }

  public boolean equals(CType other) {
    return this == other; 
  }

  public boolean equals(CType other, CReferenceType[] subst) {
    if (!other.isTypeVariable()) {
      return false;
    } else {
      return subst[((CTypeVariable) other).getIndex()] == this;
    }
  }
  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * If the variable has a clas type among its bounds, it returns the erasure of
   * that class type otherwise the interface which has the least canonical name.
   * If the varible has no bound it return java.lang.Object.
   * @return erasure of the type
   */
  public CClass getCClass() {
    return super.getCClass();
  }

  public void setIndex(int i) {
    index=i;
  }
  public int getIndex() {
    return index;
  }

  public CReferenceType[] getBounds(){
    return bounds;
  }

  public boolean isTypeVariable() {
    return true;
  }

  public static CTypeVariable[] cloneArray(CTypeVariable[] array) {
    CTypeVariable[]     copy = new CTypeVariable[array.length];

    for(int i=0; i < array.length; i++) {
      copy[i] = new CTypeVariable(array[i]);
    }
    return copy;
  }


  /**
   * Transforms this type to a string
   */
  public String toString() {
    return name;
  }


  /**
   * @return the name of this type variable
   */
  public String getIdent() {
    return name;
  }

  /**
   * returns the qualified name of the erasure not the
   * name of the type  variable.
   * @see #getIdent()
   */
  public String getQualifiedName() {
    return getCClass().getQualifiedName();
  }

  // ----------------------------------------------------------------------
  // SIGNATURE GENERATING
  // ----------------------------------------------------------------------

  /**
   * Appends the generic signature (attribute) of this type variable to the specified buffer.
   */
  public void appendGenericSignature(SimpleStringBuffer buffer) {
    buffer.append('T');
    buffer.append(name);
    buffer.append(';');
  }
  /**
   * Appends the VM signature of this type variable to the specified buffer.
   */
  public void appendSignature(SimpleStringBuffer buffer) {
    buffer.append('L');
    buffer.append(getCClass().getQualifiedName());
    buffer.append(';');
  }

  /**
   * Appends the generic signature of the definition of this type variable to the specified buffer.
   */
  public void appendDefinitionSignature(SimpleStringBuffer buffer) {
    buffer.append(name);
    buffer.append(':');
    if (bounds.length == 0) {
      buffer.append(CStdType.Object.getSignature());
    } else {
      bounds[0].appendGenericSignature(buffer);
      for (int i = 1; i < bounds.length; i++) {
        buffer.append(':');
        bounds[i].appendGenericSignature(buffer);
      }
    }
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CReferenceType[]      bounds;
  private int                   index;
  private String                name;
  private CMember               owner;
  private boolean               checked;
  private boolean               methodTypeVariable;

  public static final CTypeVariable[]   EMPTY = new CTypeVariable[0];
  public static final CTypeVariable     BAD_SUB= new CTypeVariable("<BAD>",CReferenceType.EMPTY);
}
