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
 * $Id: LanguageExtensions.java,v 1.1 2003-07-05 18:29:40 werner Exp $
 */

package org.caesarj.kjc;

import org.caesarj.compiler.TokenReference;

public class LanguageExtensions implements Constants {

  public final JVariableDefinition createAssertVariable(TypeFactory factory) {
    return new JVariableDefinition(TokenReference.NO_REF, 
                                   ACC_FINAL | ACC_PRIVATE | ACC_STATIC,
                                   factory.getPrimitiveType(TypeFactory.PRM_BOOLEAN), 
                                   IDENT_ASSERT,
                                   null);//new JBooleanLiteral(TokenReference.NO_REF, false));
  }
  public final JVariableDefinition createClassVariable(TypeFactory factory, String name) {
    return new JVariableDefinition(TokenReference.NO_REF, 
                                   ACC_FINAL | ACC_STATIC,
                                   factory.createReferenceType(TypeFactory.RFT_CLASS), 
                                   JAV_IDENT_CLASS+name,
                                   null); //new JBooleanLiteral(TokenReference.NO_REF, false));//null);
  }

  public final JVariableDefinition createReturnVariable(TokenReference ref, CType type) {
    return new JVariableDefinition(ref, 0, type, IDENT_RETURN, null);
  }

  public final JFieldDeclaration createAssertField(TypeFactory factory) {
    return new JFieldDeclaration(TokenReference.NO_REF,
                                 createAssertVariable(factory),
                                 true,
                                 null,
                                 null);
  }
  public final JFieldDeclaration createClassField(TypeFactory factory, String name) {
    return new JFieldDeclaration(TokenReference.NO_REF,
                                 createClassVariable(factory, name),
                                 true,
                                 null,
                                 null);
  }

  public final JStatement createInvariantCallStatement(TokenReference ref) {
    return new JExpressionStatement(ref,  
                                    new JMethodCallExpression(ref, 
                                                              null,
                                                              IDENT_INVARIANT, 
                                                              JExpression.EMPTY), 
                                    null);
  }

  JFormalParameter[] getPostconditionMethodParameter(TokenReference ref, 
                                                     JFormalParameter[] params,
                                                     CType retVal, 
                                                     CReferenceType typeName, 
                                                     int modifier) {
    boolean                 hasThis =  typeName != null && ((modifier & (ACC_STATIC | ACC_PRIVATE)) == 0);
    boolean                 hasReturn = retVal.getTypeID() != TID_VOID;
    JFormalParameter[]      parameter = new JFormalParameter[params.length + 1 + ((hasThis)?1:0) + ((hasReturn)?1:0)];
    
    int     i = 0;
    
    // must be the first because the parameter could be added later !!
    parameter[i++] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, CStdType.Object, IDENT_STORAGE, true);

    if (hasThis) {
      // for interfaces and against overriding
      parameter[i++] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, typeName, IDENT_CLASS, true);
    }
    if (hasReturn) {
      parameter[i++] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, retVal, IDENT_RETURN, true);
    }
    for (int k=0; k < params.length; k++, i++) {
      parameter[i] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, params[k].getType(), params[k].getIdent(), true);
    }
    return parameter;
  }


  JFormalParameter[] getPreconditionMethodParameter(TokenReference ref, 
                                                    JFormalParameter[] params, 
                                                    CReferenceType typeName,
                                                    int modifier) {
        boolean                 hasThis =  typeName != null && ((modifier & (ACC_STATIC | ACC_PRIVATE)) == 0);
        JFormalParameter[]      parameter = new JFormalParameter[params.length + ((hasThis) ? 1 : 0)];

        int i = 0;

        if (hasThis) {
          parameter[i++] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, typeName, IDENT_CLASS, true);
        }
        for (int k = 0; k < params.length; k++, i++) {
          parameter[i] = new JFormalParameter(ref, JLocalVariable.DES_PARAMETER, params[k].getType(), params[k].getIdent(), true);
        }
        return parameter;
  }


 public JStatement createInvariantStatement(TokenReference ref, String str) {
   return new JIfStatement(ref,
                           new JEqualityExpression(ref, 
                                                   true,
                                                   new JMethodCallExpression(ref,
                                                                             new JMethodCallExpression(ref,
                                                                                                       new JThisExpression(ref),
                                                                                                       "getClass",
                                                                                                       new JExpression[] {}),
                                                                             "getName",
                                                                             new JExpression[] {}),
                                                   new JStringLiteral(ref, str)),
                           createInvariantCallStatement(ref), 
                           null, 
                           null);
  }

}
