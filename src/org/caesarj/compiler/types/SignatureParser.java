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
 * $Id: SignatureParser.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.types;


public interface SignatureParser {

  MethodSignature parseMethodSignature(TypeFactory factory, String signature);

  CType parseSignature(TypeFactory factory, String signature);

  ClassSignature parseClassSignature(TypeFactory factory, String signature);

  class ClassSignature {
    public ClassSignature(CReferenceType st, CReferenceType[] ifes) {
      superType = st;
      interfaces = ifes;
    }

    public final CReferenceType             superType;
    public final CReferenceType[]           interfaces;
  }

  class MethodSignature {
    public MethodSignature(CType retType, CType[] params, CReferenceType[] exceptns) {
      returnType = retType;
      parameterTypes = params;
      exceptions = exceptns;
    }

    public final CType                  returnType;
    public final CType[]                parameterTypes;
    public final CReferenceType[]           exceptions;
  }
}
