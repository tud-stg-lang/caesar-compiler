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
 * $Id: Constants.java,v 1.1 2003-07-05 18:29:39 werner Exp $
 */

package org.caesarj.kjc;

import java.util.Vector;

/**
 * Defines all constants shared by compiler
 */
public interface Constants extends org.caesarj.classfile.Constants {

  // ----------------------------------------------------------------------
  // TYPE IDENTIFIER
  // ----------------------------------------------------------------------

  int TID_VOID			= 1;
  int TID_BYTE			= 2;
  int TID_SHORT			= 3;
  int TID_CHAR			= 4;
  int TID_INT			= 5;
  int TID_LONG			= 6;
  int TID_FLOAT			= 7;
  int TID_DOUBLE		= 8;
  int TID_CLASS			= 9;
  int TID_ARRAY			= 10;
  int TID_BOOLEAN		= 11;
  int TID_NULL                  = 12;


  // ----------------------------------------------------------------------
  // COMPILER FLAGS
  // ----------------------------------------------------------------------

  int CMP_VERSION		= 0xC0DE01;

  // ----------------------------------------------------------------------
  // JAVA CONSTANTS
  // ----------------------------------------------------------------------

  String JAV_CLASS		= "java/lang/Class".intern();
  String JAV_CLONEABLE		= "java/lang/Cloneable".intern();
  String JAV_ERROR		= "java/lang/Error".intern();
  String JAV_EXCEPTION		= "java/lang/Exception".intern();
  String JAV_OBJECT		= "java/lang/Object".intern();
  String JAV_RUNTIME_EXCEPTION	= "java/lang/RuntimeException".intern();
  String JAV_STRING		= "java/lang/String".intern();
  String JAV_STRINGBUFFER	= "java/lang/StringBuffer".intern();
  String JAV_THROWABLE		= "java/lang/Throwable".intern();
  String JAV_SERIALIZABLE	= "java/io/Serializable".intern();

  String JAV_IMAGE              = "javax/swing/ImageIcon";
  String JAV_COLOR              = "java/awt/Color";

  String JAV_CONSTRUCTOR	= "<init>";
  String JAV_INIT		= "Block$";
  String JAV_STATIC_INIT	= "<clinit>";

  String JAV_THIS		= "this";
  String JAV_OUTER_THIS		= "this$0";
  String JAV_ACCESSOR		= "access$";

  String JAV_NAME_SEPARATOR	= "/";
  String JAV_RUNTIME		= "java/lang";
  String JAV_CLONE		= "clone";
  String JAV_LENGTH		= "length";

  String JAV_IDENT_CLASS        = "class$";

  String JAV_ERROR_ASSERT       = "java/lang/AssertionError";

  // ----------------------------------------------------------------------
  // Extensions (Assertion, ...
  // ----------------------------------------------------------------------
  String KOPI_ERROR_PRECOND      = "at/dms/assertion/PreconditionError";
  String KOPI_ERROR_POSTCOND     = "at/dms/assertion/PostconditionError";
  String KOPI_ERROR_INV          = "at/dms/assertion/InvariantError";
  String KOPI_ERROR_ASSERT       = "at/dms/assertion/AssertionError";
  String KOPI_RUNTIME           = "at/dms/assertion/AssertionRuntime";

  String IDENT_EXCEPTION        = "$pe";
  String IDENT_CLASS_ASSERT     = "$$Assertions";
  String IDENT_ASSERT           = "$assertionsDisabled";
  String IDENT_FIELD            = "field$"; //field$0, field$1 ....
  String IDENT_STORAGE          = "$storage";
  String IDENT_RETURN           = "$return";
  String IDENT_CLASS            = "$class";
  String IDENT_PRE              = "$pre";
  String IDENT_POST             = "$post";
  String IDENT_V_POST           = "$V$post";
  String IDENT_INVARIANT        = "$invariant";
  String IDENT_STORE            = "$$Store"; //$$Store0 $$Store1 ...
  String IDENT_PARAMETER        = "parameter"; //$parameter0 $parameter1 ...
  String IDENT_SUPER_STORAGE    ="superStore$"; //superStore$$0 superStore$$1 ...

  // ----------------------------------------------------------------------
  // BINARY OPERATORS
  // ----------------------------------------------------------------------

  public int OPE_SIMPLE		= 0;
  public int OPE_PLUS			= 1;
  public int OPE_MINUS			= 2;
  public int OPE_STAR			= 3;
  public int OPE_SLASH			= 4;
  public int OPE_PERCENT		= 5;
  public int OPE_SR			= 6;
  public int OPE_BSR			= 7;
  public int OPE_SL			= 8;
  public int OPE_BAND			= 9;
  public int OPE_BXOR			= 10;
  public int OPE_BOR			= 11;
  public int OPE_BNOT			= 12;
  public int OPE_LNOT			= 13;
  public int OPE_LT			= 14;
  public int OPE_LE			= 15;
  public int OPE_GT			= 16;
  public int OPE_GE			= 17;
  public int OPE_EQ			= 18;
  public int OPE_NE			= 19;

  // ----------------------------------------------------------------------
  // UNARY OPERATORS
  // ----------------------------------------------------------------------

  public int OPE_PREINC		= 20;
  public int OPE_PREDEC		= 21;
  public int OPE_POSTINC		= 22;
  public int OPE_POSTDEC		= 23;

  // ----------------------------------------------------------------------
  // UTILITIES EMPTY COLLECTION
  // ----------------------------------------------------------------------

}
