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
 * $Id: KjcTypeFactory.java,v 1.3 2003-10-29 12:29:07 kloppenburg Exp $
 */

package org.caesarj.kjc;

import java.util.HashSet;
import java.util.Hashtable;

import org.caesarj.compiler.UnpositionedError;
import org.caesarj.util.InconsistencyException;
		
/**
 * Factory for standard Java Types
 */
public class KjcTypeFactory extends org.caesarj.util.Utils implements TypeFactory, Constants {

  private static HashSet primitives = new HashSet();

  static {
	  primitives.add("void");
	  primitives.add("boolean");
	  primitives.add("byte");
	  primitives.add("char");
	  primitives.add("double");
	  primitives.add("float");
	  primitives.add("int");
	  primitives.add("long");
	  primitives.add("short");
  }
  public boolean isPrimitive(String typeName) {
  	return primitives.contains(typeName);
  }
  
  public KjcTypeFactory (ClassReader reader, boolean genericEnabled) {
    CReferenceType.init(null);

    this.genericEnabled = genericEnabled;
    context = new CBinaryTypeContext(reader, this);
    knownTypes = new Hashtable(100);

    voidType = new CVoidType();
    nullType = new CNullType();


    
    primitiveBoolean = new CBooleanType();
    primitiveByte = new CByteType();
    primitiveChar = new CCharType();
    primitiveDouble = new CDoubleType();
    primitiveFloat = new CFloatType();
    primitiveInt = new CIntType();
    primitiveLong = new CLongType();
    primitiveShort = new CShortType();


    objectType = createType(JAV_OBJECT, true);
    classType = createType(JAV_CLASS, true);
    stringType = createType(JAV_STRING, true);

    throwableType = createType(JAV_THROWABLE, true);
    exceptionType = createType(JAV_EXCEPTION, true);
    errorType = createType(JAV_ERROR, true);
    runtimeExceptionType = createType(JAV_RUNTIME_EXCEPTION, true);

    kopiRuntimeType = createType(KOPI_RUNTIME, true);

    try {
      objectType = (CReferenceType)objectType.checkType(context);
      classType =  (CReferenceType)classType.checkType(context);
      stringType = (CReferenceType)stringType.checkType(context);
      throwableType = (CReferenceType)throwableType.checkType(context);
      exceptionType = (CReferenceType)exceptionType.checkType(context);
      errorType = (CReferenceType)errorType.checkType(context); 
      runtimeExceptionType = (CReferenceType)runtimeExceptionType.checkType(context);

      kopiRuntimeType = (CReferenceType)kopiRuntimeType.checkType(context);
    } catch (UnpositionedError e){
      throw new InconsistencyException("Failure while loading standard types.");
    }

    addKnownTypes(JAV_OBJECT, objectType);
    addKnownTypes(JAV_CLASS, classType);
    addKnownTypes(JAV_STRING, stringType);
    addKnownTypes(JAV_EXCEPTION, exceptionType);
    addKnownTypes(JAV_THROWABLE, throwableType);
    addKnownTypes(JAV_ERROR, errorType);
    addKnownTypes(JAV_RUNTIME_EXCEPTION, runtimeExceptionType);
    addKnownTypes(KOPI_RUNTIME, kopiRuntimeType);
  }

  // ----------------------------------------------------------------------
  // SPECIAL TYPES
  // ----------------------------------------------------------------------

  public CVoidType getVoidType() {
    return voidType;
  }

  public CNullType getNullType() {
    return nullType;
  }

  // ----------------------------------------------------------------------
  // PRIMITIVE TYPES
  // ----------------------------------------------------------------------

  public CPrimitiveType getPrimitiveType(int typeID) {
    switch(typeID){
    case PRM_BOOLEAN:
      return primitiveBoolean;
    case PRM_BYTE:
      return primitiveByte;
    case PRM_CHAR: 
      return primitiveChar;
    case PRM_DOUBLE:
      return primitiveDouble;
    case PRM_FLOAT:
      return primitiveFloat;
    case PRM_INT:
      return primitiveInt;
    case PRM_LONG:
      return primitiveLong;
    case PRM_SHORT:
      return primitiveShort;
    default:
      throw new InconsistencyException("Unknown typeID: " + typeID);
    }
  }

  public CReferenceType createReferenceType(int typeShortcut){
    switch(typeShortcut){
      // ----------------------------------------------------------------------
      // STANDARD JAVA TYPES
      // ----------------------------------------------------------------------
    case RFT_OBJECT:
      return objectType;
    case RFT_CLASS:
      return classType;
    case RFT_STRING:
      return stringType;
    case RFT_THROWABLE:
      return throwableType;
    case RFT_EXCEPTION:
      return exceptionType;
    case RFT_ERROR:
      return errorType;
    case RFT_RUNTIMEEXCEPTION:
      return runtimeExceptionType;
    case RFT_KOPIRUNTIME:
      return kopiRuntimeType;
    default:
      throw new InconsistencyException("Unknown typeShortcut: " + typeShortcut);
    }
  }

  // ----------------------------------------------------------------------
  // Create Types for classes
  // ----------------------------------------------------------------------
  /**
   * @return the environment.
   */
  public boolean isGenericEnabled() {
    return genericEnabled;
  }

  /**
   * creates a new Type
   */
//   public CReferenceType createType(CClass clazz, CReferenceType[][] arguments) {
//     return new CReferenceType(clazz);
//   }

  /**
   * Creates a type
   *  
   * @param     name is qualified or unqualified name of the type
   * @param     binary is true if this type is load from a binary class
   * @return    an unchecked type
   */
  public CReferenceType createType(String name, boolean binary) {
    CReferenceType      ref = (CReferenceType) knownTypes.get(name);

    if (ref == null) {
      return  new CClassNameType(name, binary);
    } else {
      return ref;
    }
  }

  /**
   * Creates a (generic) type
   *  
   * @param     name is qualified or unqualified name of the type
   * @param     arguments type arguments if this type is generic
   * @param     binary is true if this type is load from a binary class
   * @return    an unchecked type
   */
  public CReferenceType createType(String name, CReferenceType[][] arguments, boolean binary) {
    CReferenceType      ref = (CReferenceType) knownTypes.get(name);

    if (ref == null) {
      return new CClassNameType(name, arguments, binary);
    } else {
      return ref;
    }
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATION
  // ----------------------------------------------------------------------

  protected final void addKnownTypes(String typeName, CReferenceType type) {
    knownTypes.put(typeName, type);
  }

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------

  private CVoidType	voidType;
  private CNullType	nullType;

  private CPrimitiveType	primitiveBoolean;
  private CPrimitiveType	primitiveByte;
  private CPrimitiveType	primitiveChar;
  private CPrimitiveType	primitiveDouble;
  private CPrimitiveType	primitiveFloat;
  private CPrimitiveType	primitiveInt;
  private CPrimitiveType	primitiveLong;
  private CPrimitiveType	primitiveShort;

  private CReferenceType	objectType;
  private CReferenceType	classType;
  private CReferenceType	stringType;
  private CReferenceType	throwableType;
  private CReferenceType	exceptionType;
  private CReferenceType	errorType;
  private CReferenceType	runtimeExceptionType;

  private CReferenceType	kopiRuntimeType;

  private final boolean         genericEnabled;
  private final Hashtable       knownTypes;

  protected final CBinaryTypeContext    context;
}
