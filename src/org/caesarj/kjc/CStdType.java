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
 * $Id: CStdType.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.Compiler;
import org.caesarj.compiler.UnpositionedError;

/**
 * Root for type hierarchy
 */
public class CStdType extends org.caesarj.util.Utils implements Constants {

  // ----------------------------------------------------------------------
  // PRIMITIVE TYPES
  // ----------------------------------------------------------------------

  public static CVoidType	Void;
  public static CNullType	Null;

  public static CPrimitiveType	Boolean;
  public static CPrimitiveType	Byte;
  public static CPrimitiveType	Char;
  public static CPrimitiveType	Double;
  public static CPrimitiveType	Float;
  public static CPrimitiveType	Integer;
  public static CPrimitiveType	Long;
  public static CPrimitiveType	Short;

  public static CReferenceType	Object;
  public static CReferenceType	Class;
  public static CReferenceType	String;
  public static CReferenceType	Throwable;
  public static CReferenceType	Exception;
  public static CReferenceType	Error;
  public static CReferenceType	RuntimeException;

  // ----------------------------------------------------------------------
  // INITIALIZERS
  // ----------------------------------------------------------------------

  /**
   * Initialize all constants
   */
  public static void init(Compiler compiler, KjcEnvironment environment) {
    TypeFactory factory = environment .getTypeFactory();

    Void        = factory.getVoidType();
    Null        = factory.getNullType();
    
    Boolean     = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);
    Byte        = factory.getPrimitiveType(TypeFactory.PRM_BYTE);
    Char        = factory.getPrimitiveType(TypeFactory.PRM_CHAR);
    Double      = factory.getPrimitiveType(TypeFactory.PRM_DOUBLE);
    Float       = factory.getPrimitiveType(TypeFactory.PRM_FLOAT);
    Integer     = factory.getPrimitiveType(TypeFactory.PRM_INT);
    Long        = factory.getPrimitiveType(TypeFactory.PRM_LONG);
    Short       = factory.getPrimitiveType(TypeFactory.PRM_SHORT);

    Object              = factory.createReferenceType(TypeFactory.RFT_OBJECT); 
    Class               = factory.createReferenceType(TypeFactory.RFT_CLASS); 
    String              = factory.createReferenceType(TypeFactory.RFT_STRING);
    Throwable           = factory.createReferenceType(TypeFactory.RFT_THROWABLE);
    Exception           = factory.createReferenceType(TypeFactory.RFT_EXCEPTION); 
    Error               = factory.createReferenceType(TypeFactory.RFT_ERROR); 
    RuntimeException    = factory.createReferenceType(TypeFactory.RFT_RUNTIMEEXCEPTION); 
  }
}
