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
 * $Id: KopiPostconditionStatement.java,v 1.2 2003-10-29 12:29:07 kloppenburg Exp $
 */

package org.caesarj.kjc;

import java.util.ArrayList;

import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;

/**
 * Body of postcondition method. Calls super postconditiond, creates 
 * store, ...
 */
public class KopiPostconditionStatement extends JStatement {

  // ----------------------------------------------------------------------
  // CONSTRUCTORS
  // ----------------------------------------------------------------------

  /**
   * Construct a node in the parsing tree
   * @param	where		the line of this node in the source code
   * @param	expr		the expression to throw.
   * @param	comment		the statement comment.
   */
  public KopiPostconditionStatement(TokenReference where, 
                                    JFormalParameter[]  parameters, 
                                    CType returnType, 
                                    JBlock postcdtBody) {
    super(where, null);
    this.postcdtBody = postcdtBody;
    this.parameters = parameters;
    this.returnType = returnType;
  }

  // ----------------------------------------------------------------------
  // CODE CHECKING
  // ----------------------------------------------------------------------

  /**
   * Check statement and return a pure kjc abstract tree that will be used to code generation.
   * @param	context		the actual context of analyse
   * @exception	PositionedError		if the check fails
   */
  public void analyse(CBodyContext context) throws PositionedError {
    check(context,
          !context.getMethodContext().getCMethod().isNative(),
          KjcMessages.NATIVE_WITH_COND, context.getMethodContext().getCMethod().getIdent());
    if (postcdtBody != null) {
      postcdtBody.analyse(context);
    }

    //save context for step analyseConditions
    conditionContext = context;
  }

  public void analyseConditions()  throws PositionedError {
    if (conditionContext == null) {
      return;
    }

    // Analyse super postconditions
    TokenReference      ref = getTokenReference();
    CMethod             method = conditionContext.getMethodContext().getCMethod();
    CClass              local = conditionContext.getClassContext().getCClass();
    boolean             hasReturnType = returnType.getTypeID() != TID_VOID;
    ArrayList           methods = new ArrayList(); // methodCalls to postconditions
    ArrayList           storeClasses = new ArrayList(); // storeFields
    CMethod[]           superMethods =  ((CSourceMethod)method).getSuperMethods();
    int                 storeIndex = 0;
    CReferenceType      superTypeOfStore = null;

    // don't call supermethod twice, remove some
    for (int i = 0; i < superMethods.length; i++) {
      for (int j = 0; j <  superMethods.length; j++) {
        if (superMethods[j] != null && superMethods[j].includesSuperMethod(superMethods[i])) {
          superMethods[i] = null;
          break;
        }
      }
    }

    if ((!method.isStatic()) && (!method.isPrivate()) && (superMethods.length > 0)) {
      for (int index = 0; index < superMethods.length; index++) {
        CMethod         superPostcondition = superMethods[index];
        
        if (superPostcondition == null) {
          continue;
        }

        boolean         hasStore = superPostcondition.getOldValueStore() != null;

        check(conditionContext, superPostcondition.isPostcondition(),  KjcMessages.NOT_A_POSTCONDITION);

        int             i = 0;
        boolean         isInInterface = superPostcondition.getOwner().isAssertionClass();
        JExpression[]   params = new JExpression[parameters.length + ((hasStore/* || isInInterface*/) ? 1 : 0) + ((hasReturnType) ? 1 : 0)+1];
        // interface methods have no store
        if (hasStore) {
          if (storeIndex == 0) {
            params[i] = new JNameExpression(ref, IDENT_STORAGE);
            superTypeOfStore = superPostcondition.getOldValueStore();
          } else {
          // multiple overriding: store object is a field in $storage
            String              storeName = (IDENT_SUPER_STORAGE+(storeIndex-1)).intern();
            JExpression         initialize = new JUnqualifiedInstanceCreation(TokenReference.NO_REF,
                                                                              superPostcondition.getOldValueStore(),
                                                                              JExpression.EMPTY);
            JFieldDeclaration   fieldDecl = new JFieldDeclaration(TokenReference.NO_REF,
                                                                  new JVariableDefinition(TokenReference.NO_REF,
                                                                                          ACC_PROTECTED | ACC_FINAL,
                                                                                          superPostcondition.getOldValueStore(),
                                                                                          storeName,
                                                                                          initialize),
                                                                  null,
                                                                  null);
            storeClasses.add(fieldDecl);
            params[i] =   new KopiStoreFieldAccessExpression(ref, 
                                                             new JNameExpression(ref, IDENT_STORAGE), 
                                                             fieldDecl,
                                                             superPostcondition.getOldValueStore());
          } 
          ++storeIndex;
          ++i;
        }
        if (isInInterface) {
          params[i++] = new JThisExpression(ref);
        } else {
          params[i++] = new JNullLiteral(ref); //new JCastExpression(ref, new JNullLiteral(ref), superPostcondition.getOwner().getAbstractType());;
        }
        if (hasReturnType) {
          params[i++] = new JNameExpression(ref, IDENT_RETURN);
        }
      for (int j = 0; j < parameters.length; j++) {
        params[i++] = new JNameExpression(ref, parameters[j].getIdent());
      }

      JStatement        methodcall = 
        new JExpressionStatement(ref,
                                 new KopiMethodCallExpression(ref, superPostcondition, params), 
                                 null);
      methods.add(methodcall);
      }
    }
 
    JStatement[]         statements = (JStatement[])methods.toArray(new JStatement[methods.size()]);

    impl = new JBlock(ref, statements, null);
    impl.analyse(conditionContext);

    // create Old Value Store Class (Inner class) if necessary
    JFieldDeclaration[]         fields = conditionContext.getMethodContext().getStoreFields();
    
    if (storeClasses.size() > 0) {
      // if more than one supermethod with a store: add store class fields
      JFieldDeclaration[]       storeC = (JFieldDeclaration[])storeClasses.toArray(new JFieldDeclaration[storeClasses.size()]);
      JFieldDeclaration[]       tmp = new JFieldDeclaration[fields.length+storeC.length];
      
      System.arraycopy(storeC, 0, tmp, 0, storeC.length);
      System.arraycopy(fields, 0, tmp, storeC.length, fields.length);
      fields = tmp;
    }

    if (fields.length == 0) {
      if (superTypeOfStore != null) {
          // this method use no store, but the store must be created for
          // super conditions checks.
          ((CSourceMethod) method).setOldValueStore(superTypeOfStore);
      }
      conditionContext = null;
      return;
    } else {
      KopiStoreClassDeclaration         storeClass 
        = new KopiStoreClassDeclaration(method.isStatic(),
                                        conditionContext.getClassContext().getNextStoreIndex(),
                                        fields,
                                        conditionContext.getTypeFactory());

      if (superTypeOfStore != null) {
        storeClass.setSuperClass(superTypeOfStore);
      }
      storeClass.generateInterface(conditionContext.getClassReader(), 
                                   local,
                                   local.getQualifiedName() + "$");
      
      storeClass.join(conditionContext.getCompilationUnitContext());
      storeClass.checkInterface(conditionContext.getCompilationUnitContext());
      storeClass.checkInitializers(conditionContext);
      storeClass.checkTypeBody(conditionContext);
      try {
        ((CSourceMethod) method).setOldValueStore((CReferenceType)new CClassOrInterfaceType(storeClass.getCClass()).checkType(conditionContext));
      } catch (UnpositionedError e) {
        throw e.addPosition(getTokenReference());
      }
      conditionContext = null;
      return;
    }
  }

  // ----------------------------------------------------------------------
  // CODE GENERATION
  // ----------------------------------------------------------------------
  /**
   * Accepts the specified visitor
   * @param	p		the visitor
   */
  public void accept(KjcVisitor p) {
    if (postcdtBody != null) {
      postcdtBody.accept(p);
    }
    impl.accept(p);
  }

  /**
   * Generates a sequence of bytescodes
   * @param	code		the code list
   */
  public void genCode(GenerationContext context) {
    if (postcdtBody != null) {
      postcdtBody.genCode(context);
    }
    impl.genCode(context); 
  }

  // ----------------------------------------------------------------------
  // PRIVATE DATA MEMBERS
  // ----------------------------------------------------------------------

  private JBlock                        postcdtBody;
  private JStatement                    impl = null;
  private JFormalParameter[]            parameters;
  private CType                         returnType;
  private CBodyContext                  conditionContext;
}
