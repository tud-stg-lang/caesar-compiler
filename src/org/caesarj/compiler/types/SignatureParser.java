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
 * $Id: SignatureParser.java,v 1.1 2004-02-08 16:47:47 ostermann Exp $
 */

package org.caesarj.compiler.types;


public interface SignatureParser {

  MethodSignature parseMethodSignature(TypeFactory factory, String signature);

  CType parseSignature(TypeFactory factory, String signature);

  ClassSignature parseClassSignature(TypeFactory factory, String signature);

  class ClassSignature {
    public ClassSignature(CReferenceType st, CReferenceType[] ifes, CTypeVariable[] tv) {
      superType = st;
      interfaces = ifes;
      typeVariables = tv;
    }

    public final CReferenceType             superType;
    public final CReferenceType[]           interfaces;
    public final CTypeVariable[]        typeVariables;
  }

  class MethodSignature {
    public MethodSignature(CType retType, CType[] params, CReferenceType[] exceptns, CTypeVariable[] tv) {
      returnType = retType;
      parameterTypes = params;
      exceptions = exceptns;
      typeVariables = tv;
    }

    public final CType                  returnType;
    public final CType[]                parameterTypes;
    public final CReferenceType[]           exceptions;
    public final CTypeVariable[]        typeVariables;
  }
}
