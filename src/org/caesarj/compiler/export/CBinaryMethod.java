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
 * $Id: CBinaryMethod.java,v 1.1 2004-02-08 16:47:45 ostermann Exp $
 */

package org.caesarj.compiler.export;

import org.caesarj.classfile.MethodDescription;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.SignatureParser;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a loaded (already compiled) class method.
 */
public class CBinaryMethod extends CMethod {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Constructs method
   * @param	owner		the owner of this method
   * @param	methodInfo	a method info from a class file
   */
  public static CBinaryMethod create(SignatureParser sigParser,
                                     TypeFactory factory, 
                                     CClass owner, 
                                     MethodInfo methodInfo){
    SignatureParser.MethodSignature     methodSignature =
      sigParser.parseMethodSignature(factory, methodInfo.getSignature());
    CType[]         paramTypes =  methodSignature.parameterTypes;

    if (methodInfo.getName().intern() == JAV_CONSTRUCTOR
        && owner.isNested() 
        && owner.hasOuterThis()) {
      // remove systhetic parameter used for 'outer this'
      CType[]   tmpParamTypes = new CType[paramTypes.length-1];
      
      System.arraycopy(paramTypes, 1, tmpParamTypes, 0, tmpParamTypes.length);
      paramTypes = tmpParamTypes; 
    }
    return new CBinaryMethod(factory, 
                             owner,
                             methodInfo, 
                             methodSignature.returnType, 
                             paramTypes,
                             methodSignature.typeVariables,
                             methodSignature.exceptions);
  }

  /**
   * Constructs method
   * @param	owner		the owner of this method
   * @param	methodInfo	a method info from a class file
   */
  private CBinaryMethod(TypeFactory factory, 
                        CClass owner, 
                        MethodInfo methodInfo, 
                        CType retType, 
                        CType[] paramTypes,
                        CTypeVariable[] typeVariable,
                        CReferenceType[] exceptions) {
    super(owner,
	  methodInfo.getModifiers(),
	  methodInfo.getName(),
          retType,
          paramTypes,
	  buildExceptionTypes(factory, methodInfo, exceptions),
          typeVariable,
	  methodInfo.isDeprecated(),
          methodInfo.isSynthetic());

    this.methodInfo = methodInfo;
  }

  private static CReferenceType[] buildExceptionTypes(TypeFactory factory, MethodInfo methodInfo, CReferenceType[] genericExceptions) {
    if (genericExceptions.length != 0) {
      return genericExceptions;
    } else {
      String[]	exceptions = methodInfo.getExceptions();

      if (exceptions == null) {
        return new CReferenceType[0];
      } else {
        CReferenceType[]	types = new CReferenceType[exceptions.length];

        for (int i = 0; i < exceptions.length; i++) {
          types[i] = factory.createType(exceptions[i], true);
        }
        return types;
      }
    }
  }

  // ----------------------------------------------------------------------
  // CHECKING
  // ----------------------------------------------------------------------

  private CType[] buildParameterTypes(TypeFactory factory, 
                                      SignatureParser sigParser,
                                      String signature) {
//     CType[]	types = sigParser.parseMethodSignature(factory, signature);
//     CType[]	paramTypes = new CType[types.length - 1];

//     System.arraycopy(types, 0, paramTypes, 0, paramTypes.length);
//     return paramTypes;
    return sigParser.parseMethodSignature(factory, signature).parameterTypes;
  }

  public void checkTypes(CBinaryTypeContext context) throws UnpositionedError {
    CBinaryTypeContext  self = new CBinaryTypeContext(context.getClassReader(), 
                                                      context.getTypeFactory(),
                                                      context,
                                                      this);

    returnType = returnType.checkType(self);

    CType[]             param = getParameters();

    for (int i=0; i < param.length; i++) {
      param[i] = param[i].checkType(self);
    }

    CReferenceType[]        exceptions = getThrowables();

    for (int i=0; i < exceptions.length; i++) {
      exceptions[i] = (CReferenceType) exceptions[i].checkType(self);
    }

    // load types for extended features (assertion)
    TypeFactory         factory = context.getTypeFactory();
    MethodDescription   preMethod = methodInfo.getPreconditionMethod();

    if (preMethod != null) {
      CReferenceType        preClass = factory.createType(preMethod.getOwner(), true);
      CType[]           params = buildParameterTypes(factory,
                                                     context.getSignatureParser(),
                                                     preMethod.getType());

      preconditionMethod = preClass.getCClass().lookupMethod(context, getOwner(), null, preMethod.getName(), params,  preClass.getCClass().getTypeVariables());
    }
    MethodDescription   postMethod = methodInfo.getPostconditionMethod();

    if (postMethod != null) {
      CReferenceType         postClass = factory.createType(postMethod.getOwner(), true);
      CType[]            params = buildParameterTypes(factory,
                                                      context.getSignatureParser(),
                                                      postMethod.getType());

      postconditionMethod = postClass.getCClass().lookupMethod(context, getOwner(), null, postMethod.getName(), params, postClass.getCClass().getTypeVariables());
    }
      
    if (methodInfo.isPostcondition() && methodInfo.getOldValueStore() != null) {
      oldValueStore = getOwner().lookupClass(getOwner(), methodInfo.getOldValueStore()).getAbstractType();
    } else {
      oldValueStore = null;
    }
 
  }
  // ----------------------------------------------------------------------
  // CHECK MATCHING
  // ----------------------------------------------------------------------

//   /**
//    * equals
//    * search if two methods have same signature
//    * @param	other		the other method
//    */
//   public boolean equals(CMethod other) {
//     CClass owner = getOwner();

//     if (!isConstructor() 
//         || !other.isConstructor()
//         || !owner.isNested() 
//         || !owner.hasOuterThis() 
//         || other instanceof CBinaryMethod) {
//       return super.equals(other);
//     } else {
//       final CType[]		parameters = getParameters();
//       final CType[]		otherParameters = other.getParameters();
      
//       // in constructors of inner classes first parameter is enclosed this
//       if (!getOwner().equals(other.getOwner())) {
//         return false;
//       } else if (getIdent() != other.getIdent()) {
//         return false;
//       } else if (parameters.length != otherParameters.length-1) {
//         return false;
//       } else {
//         for (int i = 1; i < parameters.length; i++) {
//           if (!parameters[i].equals(otherParameters[i-1])) {
//             return false;
//           }
//         }
//         return true;
//       }
//     }
//   }

//   /**
//    * Is this method applicable to the specified invocation (JLS 15.12.2.1) ?
//    * @param	ident		method invocation name
//    * @param	actuals		method invocation arguments
//    */
//   public boolean isApplicableTo(CTypeContext context, String ident, CType[] actuals, CReferenceType[] substitution) {
//     CClass owner = getOwner();

//     if (!isConstructor() 
//         || ident != JAV_CONSTRUCTOR 
//         || !owner.isNested() 
//         || !owner.hasOuterThis()) {
//       return super.isApplicableTo(context, ident, actuals, substitution);
//     } else {
//       final CType[]		parameters = getParameters();

//       if (ident != getIdent()) {
//         return false;
//       } else if (actuals.length+1 != parameters.length) {
//         return false;
//       } else {
//         for (int i = 0; i < actuals.length; i++) {
//           // method invocation conversion = assigment conversion without literal narrowing
//           // we just look at the type and do not consider literal special case
//           if (!actuals[i].isAssignableTo(context, parameters[i+1], substitution)) {
//             return false;
//           }
//         }
//         return true;
//       }
//     }
//   }

//   /**
//    * Is this method more specific than the one given as argument (JLS 15.12.2.2) ?
//    * @param	other		the method to compare to
//    */
//   public boolean isMoreSpecificThan(CTypeContext context, CMethod other, CReferenceType[] substitution) throws UnpositionedError {
//     CClass owner = getOwner();

//     if (!isConstructor() 
//         || !other.isConstructor()
//         || !owner.isNested() 
//         || !owner.hasOuterThis()
//         || other instanceof CBinaryMethod) {
//       return super.isMoreSpecificThan(context, other, substitution);
//     } else {
//       final CType[]		parameters = getParameters();
//       final CType[]		otherParameters = other.getParameters();

//       if (!getOwnerType().isAssignableTo(context, other.getOwnerType(), substitution)) {
//         return false;
//       } else if (parameters.length != otherParameters.length+1) {
//         return false;
//       } else {
//         for (int i = 0; i < otherParameters.length; i++) {
//           // method invocation conversion = assigment conversion without literal narrowing
//           // we just look at the type and do not consider literal special case
//           if (!parameters[i+1].isAssignableTo(context, otherParameters[i], substitution)) {
//             return false;
//           }
//         }
//         return true;
//       }
//     }
//   }

//   /**
//    * Has this method the same signature as the one given as argument ?
//    * NOTE: return type not considered
//    * @param	other		the method to compare to
//    */
//   public boolean hasSameSignature(CTypeContext context, CMethod other) throws UnpositionedError{
//     CClass owner = getOwner();

//     if (!isConstructor() 
//         || !other.isConstructor()
//         || !owner.isNested() 
//         || !owner.hasOuterThis()
//         || other instanceof CBinaryMethod) {
//       return super.hasSameSignature(context, other);
//     } else {
//       final CType[]		parameters = getParameters();
//       final CType[]		otherParameters = other.getParameters();

//       if (parameters.length != otherParameters.length+1) {
//         return false;
//       } else {
//         for (int i = 0; i < otherParameters.length; i++) {
//           if (!parameters[i+1].equals(otherParameters[i])) {
//             return false;
//           }
//         }
//         return true;
//       }
//     }
//   }

//   /**
//    * Returns a string representation of this method.
//    */
//   public String toString() {
//     CClass owner = getOwner();

//     if (!isConstructor() 
//         || !owner.isNested() 
//         || !owner.hasOuterThis()) {
//       return super.toString();
//     } else {
//       StringBuffer	buffer = new StringBuffer();
//       final CType[]     parameters = getParameters();

//       buffer.append(getReturnType());
//       buffer.append(" ");
//       buffer.append(getOwner());
//       buffer.append(".");
//       buffer.append(getIdent());
//       buffer.append("(");
//       for (int i = 1; i < parameters.length; i++) {
//         if (i != 1) {
//           buffer.append(", ");
//         }
//         buffer.append(parameters[i]);
//       }
//       buffer.append(")");

//       return buffer.toString();
//     }
//   }

  public String getSignature() {
    return methodInfo.getSignature();
  }
  // ----------------------------------------------------------------------
  // ACCESSORS (Extended)
  // ----------------------------------------------------------------------
  
  public boolean isInvariant() {
    return methodInfo.isInvariant();
  }

  public boolean isPrecondition() {
    return methodInfo.isPrecondition();
  }
  
  public boolean isPostcondition() {
    return methodInfo.isPostcondition();
  }

  public CMethod getPreconditionMethod() {
    return preconditionMethod;
  }

  public CMethod getPostconditionMethod() {
    return postconditionMethod;
  }

  public CReferenceType getOldValueStore() {
    return oldValueStore;
  }

  private MethodInfo    methodInfo;
  private CMethod       preconditionMethod;
  private CMethod       postconditionMethod;
  private CReferenceType    oldValueStore;
}
