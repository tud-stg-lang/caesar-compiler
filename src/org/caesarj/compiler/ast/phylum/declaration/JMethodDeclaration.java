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
 * $Id: JMethodDeclaration.java,v 1.8 2004-10-29 13:24:14 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;

import org.caesarj.compiler.ast.CLineError;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.expression.JCastExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JMethodCallExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.JThisExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.Constants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * This class represents a Java method declaration in the syntax tree.
 */
public class JMethodDeclaration extends JMemberDeclaration {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a method declaration node in the syntax tree.
     *
     * @param	where		the line of this node in the source code
     * @param	modifiers	the list of modifiers of the method
     * @param	returnType	the return type of the method
     * @param	ident		the name of the method
     * @param	parameters	the parameters of the method
     * @param	exceptions	the exceptions declared by the method
     * @param	body		the body of the method
     * @param	javadoc		java documentation comments
     * @param	comment		other comments in the source code
     */
    public JMethodDeclaration(
        TokenReference where,
        int modifiers,
        CType returnType,
        String ident,
        JFormalParameter[] parameters,
        CReferenceType[] exceptions,
        JBlock body,
        JavadocComment javadoc,
        JavaStyleComment[] comments) {
        super(where, javadoc, comments);

        this.modifiers = modifiers;
        this.returnType = returnType;
        this.ident = ident.intern();
        this.body = body;

        this.parameters = parameters;
        this.exceptions = exceptions;
        verify(parameters != null);
        verify(exceptions != null);
    }

    // ----------------------------------------------------------------------
    // INTERFACE CHECKING
    // ----------------------------------------------------------------------

    /**
     * Second pass (quick), check interface looks good
     * Exceptions are not allowed here, this pass is just a tuning
     * pass in order to create informations about exported elements
     * such as Classes, Interfaces, Methods, Constructors and Fields
     * @return true iff sub tree is correct enough to check code
     * @exception	PositionedError	an error with reference to the source file
     */
    public CSourceMethod checkInterface(CClassContext context)
        throws PositionedError {

        // in a mixin a method may not be package-visible
        if(context.getCClass().isMixin()) {
		    check(
		        context,
		        CModifier.contains(
		            getModifiers(),
		            ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE
	            ),
		        CaesarMessages.CCLASS_PACKAGE_VISIBILITY);
        }

        
        boolean inInterface = context.getCClass().isInterface();
        boolean isExported = !(this instanceof JInitializerDeclaration);
        String ident =
            (this instanceof JConstructorDeclaration)
                ? JAV_CONSTRUCTOR
                : this.ident;

        // Collect all parsed data
        if (inInterface && isExported) {
            modifiers |= ACC_PUBLIC | ACC_ABSTRACT;
        }

        // 8.4.3 Method Modifiers
        check(
            context,
            CModifier.isSubsetOf(modifiers, getAllowedModifiers()),
            KjcMessages.METHOD_FLAGS);
        // 8.4.3.4 Navtive Methods
        // A compile-time error occurs if a native method is declared abstract.
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_NATIVE) == 0,
            KjcMessages.METHOD_ABSTRACT_NATIVE);
        // 8.4.3.1 
        // It is a compile-time error for a private method to be declared abstract.
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_PRIVATE) == 0,
            KjcMessages.METHOD_ABSTRACT_PRIVATE);
        // 8.4.3.1 
        // It is a compile-time error for a static method to be declared abstract.
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_STATIC) == 0,
            KjcMessages.METHOD_ABSTRACT_STATIC);
        // 8.4.3.1 
        // It is a compile-time error for a final method to be declared abstract.
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_FINAL) == 0,
            KjcMessages.METHOD_ABSTRACT_FINAL);
        // 8.1.2 Inner Classes and Enclosing Instances
        // Inner classes may not declare static members, unless they are compile-time constant fields

        check(
            context,
            context.getCClass().canDeclareStatic()
                || ident == JAV_STATIC_INIT
                || ((modifiers & ACC_STATIC) == 0),
            KjcMessages.INNER_DECL_STATIC_MEMBER);

        check(
            context,
            (modifiers & ACC_NATIVE) == 0 || (modifiers & ACC_STRICT) == 0,
            KjcMessages.METHOD_NATIVE_STRICT);
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0
                || (modifiers & ACC_SYNCHRONIZED) == 0,
            KjcMessages.METHOD_ABSTRACT_SYNCHRONIZED);
        check(
            context,
            (modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_STRICT) == 0,
            KjcMessages.METHOD_ABSTRACT_STRICT);

        if (inInterface && isExported) {
            check(
                context,
                CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_ABSTRACT),
                KjcMessages.METHOD_FLAGS_IN_INTERFACE,
                this.ident);
        }
        try {
            CType[] parameterTypes = new CType[parameters.length];
            CBinaryTypeContext typeContext =
                new CBinaryTypeContext(
                    context.getClassReader(),
                    context.getTypeFactory(),
                    context,
                    (modifiers & ACC_STATIC) == 0);

            returnType = returnType.checkType(typeContext);
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = parameters[i].checkInterface(typeContext);
            }

            for (int i = 0; i < exceptions.length; i++) {
                exceptions[i] =
                    (CReferenceType)exceptions[i].checkType(typeContext);
            }

            setInterface(new CSourceMethod(
                context.getCClass(),
                modifiers,
                ident,
                returnType,
                parameterTypes,
                exceptions,
                isDeprecated(),
                false,
            // not synthetic
            body));

            return (CSourceMethod)getMethod();
        }
        catch (UnpositionedError cue) {
            throw cue.addPosition(getTokenReference());
        }
    }

    // ----------------------------------------------------------------------
    // SEMANTIC ANALYSIS
    // ----------------------------------------------------------------------

    /**
     * Check expression and evaluate and alter context
     * @param context the actual context of analyse
     * @return  a pure java expression including promote node
     * @exception PositionedError Error catched as soon as possible
     */
    public void checkBody1(CClassContext context) throws PositionedError {
        check(
            context,
            context.getCClass().isAbstract() || !getMethod().isAbstract(),
            KjcMessages.METHOD_ABSTRACT_CLASSNOT,
            ident);

        checkOverriding(context);

        check(
            context,
            getMethod().getHeapForParameter() <= 255,
            KjcMessages.MANY_METHOD_PARAMETER,
            ident);

        if (body == null) {
            check(
                context,
                getMethod().isAbstract()
                    || getMethod().isNative()
                    || context.getClassContext().getCClass().isInterface(),
                KjcMessages.METHOD_NOBODY_NOABSTRACT,
                ident);
        }
        else {
            check(
                context,
                !context.getCClass().isInterface(),
                KjcMessages.METHOD_BODY_IN_INTERFACE,
                ident);

            check(
                context,
                !getMethod().isNative() && !getMethod().isAbstract(),
                KjcMessages.METHOD_BODY_NATIVE_ABSTRACT,
                ident);

            CMethodContext self =
                new CMethodContext(
                    context,
                    context.getEnvironment(),
                    getMethod(),
                    parameters);
            CBlockContext block =
                new CBlockContext(
                    self,
                    context.getEnvironment(),
                    parameters.length);

            if (!getMethod().isStatic()) {
                // add this local var
                block.addThisVariable();
            }

            for (int i = 0; i < parameters.length; i++) {
                parameters[i].analyse(block);
            }

            body.analyse(block);

            block.close(getTokenReference());
            self.close(getTokenReference());

            if (block.isReachable()
                && getMethod().getReturnType().getTypeID() != TID_VOID) {
                context.reportTrouble(
                    new CLineError(
                        getTokenReference(),
                        KjcMessages.METHOD_NEED_RETURN,
                        getMethod().getIdent()));
            }
        }
    }

    private void addBridge(CClassContext context, CMethod method)
        throws PositionedError {
        CClass local = context.getCClass();
        CType bridgeType = null;
        JBlock bridgeBody = null;

        bridgeType = method.getReturnType();
        //must not be a class (case !exactSignature)

        JExpression[] args = new JExpression[parameters.length];
        CType[] superParams = method.getParameters();
        JFormalParameter[] bridgeParameter =
            new JFormalParameter[parameters.length];

        for (int i = 0; i < args.length; i++) {
            CType btType = null;

            try {
                btType = superParams[i].getErasure(context);
            }
            catch (UnpositionedError e) {
                throw e.addPosition(getTokenReference());
            }
            bridgeParameter[i] =
                new JFormalParameter(
                    getTokenReference(),
                    JFormalParameter.DES_LOCAL_VAR,
                    btType,
                    parameters[i].getIdent(),
                    parameters[i].isFinal());

            if ((parameters[i].getType().isClassType())
                && (parameters[i].getType().getCClass()
                    != superParams[i].getCClass())) {
                try {
                    args[i] =
                        new JCastExpression(
                            getTokenReference(),
                            new JNameExpression(
                                getTokenReference(),
                                parameters[i].getIdent()),
                            parameters[i].getType().getErasure(context));
                }
                catch (UnpositionedError e) {
                    throw e.addPosition(getTokenReference());
                }
            }
            else {
                args[i] =
                    new JNameExpression(
                        getTokenReference(),
                        parameters[i].getIdent());
            }
        }

        JExpression callRealMethod =
            new JMethodCallExpression(
                getTokenReference(),
                new JThisExpression(getTokenReference()),
                ident,
                args);
        bridgeBody =
            new JBlock(
                getTokenReference(),
                new JStatement[] {
                    (bridgeType.getTypeID() == TID_VOID)
                        ? (JStatement)new JExpressionStatement(
                            getTokenReference(),
                            callRealMethod,
                            null)
                        : new JReturnStatement(
                            getTokenReference(),
                            callRealMethod,
                            null)},
                null);

        JMethodDeclaration bridgeMethod =
            new JMethodDeclaration(
                getTokenReference(),
                ((modifiers | Constants.ACC_ABSTRACT) ^ Constants.ACC_ABSTRACT),
                bridgeType,
                ident,
                bridgeParameter,
                exceptions,
                bridgeBody,
                null,
                null) {

            protected void checkOverriding(CClassContext contxt)
                throws PositionedError {
                    // nothing to do!!
    }
        };
        CSourceMethod bridge =
            (CSourceMethod)bridgeMethod.checkInterface(context);

        bridge.setSynthetic(true);
        ((CSourceClass)local).addMethod(bridge);
        bridgeMethod.checkBody1(context);
        // resolve methodcall before it can find itself
        if (bridgesToPrint == null) {
            bridgesToPrint = new ArrayList();
        }
        bridgesToPrint.add(bridgeMethod);
    }
    /**
     * Checks that overriding/hiding is correct.
     *
     * @param	context		the analysis context
     * @exception	PositionedError	the analysis detected an error
     */
    protected void checkOverriding(CClassContext context)
        throws PositionedError {
        ArrayList needBridges = new ArrayList();

        try {
            context.getCClass().checkOverriding(
                context,
                getMethod(),
                needBridges);
        }
        catch (UnpositionedError ue) {
            throw ue.addPosition(getTokenReference());
        }

        for (int i = 0; i < needBridges.size(); i++) {
            addBridge(context, (CMethod)needBridges.get(i));
        }

    }

    public void analyseConditions() throws PositionedError {
        // nothing to do
    }
    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------

    /**
     * Accepts the specified visitor
     * @param	p		the visitor
     */
    public void recurse(IVisitor s) {
        if(body != null)
            body.accept(s);
    }

    /**
     * Generates a sequence of bytescodes
     * @param	code		the code list
     */
    public void genCode(GenerationContext context) {
        throw new InconsistencyException(); // nothing to do here
    }

    /**
     * Sven:
     * body access for manipulation
     */
    public JBlock getBlockBody() {
        return body;
    }

    public void setBlockBody(JBlock b) {
        body = b;
    }

    public JFormalParameter[] getArgs() {
        return parameters;
    }
    
    public void setReturnType(CType returnType) {
        this.returnType = returnType;
    }

    public void setParameters(JFormalParameter[] newFormalParams) {
        this.parameters = newFormalParams;
    }

    public String getIdent() {
        return ident;
    }

    protected int getAllowedModifiers() {
        return ACC_PUBLIC
            | ACC_PROTECTED
            | ACC_PRIVATE
            | ACC_ABSTRACT
            | ACC_FINAL
            | ACC_STATIC
            | ACC_NATIVE
            | ACC_SYNCHRONIZED
            | ACC_STRICT;
    }
    
    public JFormalParameter[] getParameters() {
        return parameters;
    }

    public CType getReturnType() {
        return returnType;
    }

    public CReferenceType[] getExceptions() {
        return exceptions;
    }

    public int getModifiers() {
        return modifiers;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    // $$$ MOVE TO BE PRIVATE
    protected int modifiers;
    protected CType returnType;
    protected String ident;
    protected JFormalParameter[] parameters;
    protected CReferenceType[] exceptions;
    protected JBlock body;
    private ArrayList bridgesToPrint;
    

}
