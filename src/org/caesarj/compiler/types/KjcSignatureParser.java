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
 * $Id: KjcSignatureParser.java,v 1.1 2004-02-08 16:47:48 ostermann Exp $
 */

package org.caesarj.compiler.types;

import java.util.ArrayList;

import org.caesarj.util.InconsistencyException;

public class KjcSignatureParser implements SignatureParser {

  /**
   * Parses a VM-standard type signature.
   *
   * @param	signature	the type signature
   * @param	from		the start index
   * @param	to		the end index
   * @return	the type represented by the signature
   */
  public final CType parseSignature(TypeFactory factory, String signature) {
    return parseSignature(factory, signature, 0, signature.length());
  }

  /**
   * Parses a VM-standard type signature within a signature string.
   *
   * @param	signature	the type signature
   * @param	from		the start index
   * @param	to		the end index
   * @return	the type represented by the signature 
   */
  protected CType parseSignature(TypeFactory factory,  String signature, int from, int to) {
    CType	type;
    int		bounds;

    bounds = 0;
    for (; signature.charAt(from) == '['; from++) {
      bounds += 1;
    }

    switch (signature.charAt(from)) {
    case 'V':
      type = factory.getVoidType(); 
      break;
    case 'B':
      type = factory.getPrimitiveType(TypeFactory.PRM_BYTE); 
      break;
    case 'C':
      type = factory.getPrimitiveType(TypeFactory.PRM_CHAR); 
      break;
    case 'D':
      type = factory.getPrimitiveType(TypeFactory.PRM_DOUBLE); 
      break;
    case 'F':
      type = factory.getPrimitiveType(TypeFactory.PRM_FLOAT); 
      break;
    case 'I':
      type = factory.getPrimitiveType(TypeFactory.PRM_INT);
      break;
    case 'J':
      type = factory.getPrimitiveType(TypeFactory.PRM_LONG);
      break;
    case 'L':
      type = factory.createType(signature.substring(from + 1, to - 1), true);
      break;
    case 'S':
      type = factory.getPrimitiveType(TypeFactory.PRM_SHORT);
      break;
    case 'Z':
      type = factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN);
      break;
    case 'T': // JSR 14: TypeVariable
      type = new CTypeVariableAlias(signature.substring(from + 1, to - 1));
      break;
    default:
      throw new InconsistencyException("Unknown signature: " + signature.charAt(from));
    }

    return bounds > 0 ? new CArrayType(type, bounds) : type;
  }

  protected CType parseGenericTypeSignature(TypeFactory factory, String signature, char[] sig) {
    int                 from = current;
    CType               type = null;

    while (sig[current] == '[') {
      current += 1;
    }

    if ((sig[current] != 'L') && (sig[current] != 'T')) {
      current += 1;
    }

    else {
      while ((sig[current] != ';') && (sig[current] != '<')) {
        current += 1;
      }
      current += 1;
    }
    if (sig[current-1] != '<') {
      type = parseSignature(factory, signature, from, current);
    } else {
      String            ident = String.valueOf(sig, from+1, current - from-2);
      CReferenceType[]      types = parseTypeArgumentSignature(factory, signature, sig);

      type = factory.createType(ident, new CReferenceType[][]{ types}, true);
    }
    return type;
  }

  protected CReferenceType[] parseTypeArgumentSignature(TypeFactory factory, String signature, char[] sig) {
    ArrayList              vect = new ArrayList(10);

    while (sig[current] != '>') {
      vect.add(parseGenericTypeSignature(factory, signature, sig));
    }
    current++;
    // verify(sig[current] == ';');
    current++;

    return (CReferenceType[])vect.toArray(new CReferenceType[vect.size()]); 
  }

  protected CTypeVariable[] parseTypeParameter(TypeFactory factory, String signature, char[] sig) {
    // '<' TypeVarName1 ':' bound1 ':' bound2 [...] TypeVarName2 : ... '>'
    if (sig[current] == '<') {
      ArrayList    tvVect = new ArrayList(10);
      
      current++;
      while (sig[current] != '>') {
        int             end = current; 
        String          ident; // type var name

        while (sig[end] != ':') {
          end += 1;
        }
        ident = signature.substring(current, end);
        current = ++end;

        ArrayList          bounds = new ArrayList(5); // type bound

        bounds.add(parseGenericTypeSignature(factory, signature, sig));
        while (sig[current] == ':') { // another bound ?
          current++;
          bounds.add(parseGenericTypeSignature(factory, signature, sig));
        }

        CReferenceType[]    tvBound = (CReferenceType[])bounds.toArray(new CReferenceType[bounds.size()]);

        tvVect.add(new CTypeVariable(ident, tvBound));
      }

      CTypeVariable[]   typeVariable = (CTypeVariable[])tvVect.toArray(new CTypeVariable[tvVect.size()]);

      for (int i = 0; i < typeVariable.length; i++) {
        typeVariable[i].setIndex(i);
      }
      current++;
      return typeVariable;
    } else {
      return CTypeVariable.EMPTY;
    }
  }

  /**
   * Returns an object representing the types the signature of a class
   *
   * @returns an object enclosing the supertype, interfaces and tv
   */
 public ClassSignature parseClassSignature(TypeFactory factory, String signature) {
   CTypeVariable[]      tvs;
   CReferenceType           superType;
   CReferenceType[]         inter;
   char[]               sig = signature.toCharArray();

   current = 0;
   // typeVariable
   tvs = parseTypeParameter(factory, signature, sig);
   // supertype
   superType = (CReferenceType) parseGenericTypeSignature(factory, signature, sig);

   // interfaces
   ArrayList               interfaces = new ArrayList(10);

   while (current < sig.length) {
     interfaces.add(parseGenericTypeSignature(factory, signature, sig));
   }
   inter = (CReferenceType[])interfaces.toArray(new CReferenceType[interfaces.size()]);
   
   return new ClassSignature(superType, inter, tvs);
 }

  /**
   * Returns an array of types represented by the type signature
   * For methods, the return type is the last element of the array
   */
  public MethodSignature parseMethodSignature(TypeFactory factory, String signature) {
    CTypeVariable[]     typeVariables; 
    CType[]             parameter;
    CType               returnType;
    CReferenceType[]    exceptions;
    char[]              sig = signature.toCharArray();

    current = 0;
    // typeVariable
    typeVariables = parseTypeParameter(factory, signature, sig);

    // parameter
    ArrayList	paramVec = new ArrayList();

    // verify(sig.charAt(0) == '(');
    current++;
    while (sig[current] != ')') {
      paramVec.add(parseGenericTypeSignature(factory, signature, sig));
    }
    parameter = (CType[])paramVec.toArray(new CType[paramVec.size()]); 
    // return type
    current++; // ')'
    returnType = parseGenericTypeSignature(factory, signature, sig);
    // exceptions
    ArrayList 	exVec = paramVec;

    exVec.clear();
    if ((sig.length > current) && (sig[current] =='^')) {

      while (sig.length > current) {
        exVec.add(parseGenericTypeSignature(factory, signature, sig));
      }

      exceptions = (CReferenceType[])exVec.toArray(new CReferenceType[exVec.size()]); 
    } else {
      exceptions = CReferenceType.EMPTY;
    }

    return new MethodSignature(returnType, parameter, exceptions, typeVariables);
  }

  private int current;
}
