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
 * $Id: JUnqualifiedInstanceCreation.java,v 1.6 2004-09-06 13:31:35 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.CMethodNotFoundError;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CThrowableInfo;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * JLS 15.9 Class Instance Creation Expressions.
 *
 * This class represents an unqualified class instance creation expression.
 */
public class JUnqualifiedInstanceCreation extends JExpression {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Construct a node in the parsing tree
     * This method is directly called by the parser
     * @param	where		the line of this node in the source code
     * @param	type		the type of the object to be created
     * @param	params		parameters to be passed to constructor
     */
    public JUnqualifiedInstanceCreation(
        TokenReference where,
        CReferenceType type,
        JExpression[] params) {
        super(where);

        this.type = type;
        this.params = params;
    }

    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------

    /**
     * Compute the type of this expression (called after parsing)
     * @return the type of this expression
     */
    public CType getType(TypeFactory factory) {
        return type;
    }

    /**
     * Returns true iff this expression can be used as a statement (JLS 14.8)
     */
    public boolean isStatementExpression() {
        return true;
    }

    // ----------------------------------------------------------------------
    // SEMANTIC ANALYSIS
    // ----------------------------------------------------------------------

    /**
     * Analyses the expression (semantically).
     * @param	context		the analysis context
     * @return	an equivalent, analysed expression
     * @exception	PositionedError	the analysis detected an error
     */
    public JExpression analyse(CExpressionContext context)
        throws PositionedError {
        TypeFactory factory = context.getTypeFactory();

        local = context.getClassContext().getCClass();

        // JLS 15.9.1 Determining the Class being Instantiated

        // If the class instance creation expression is an unqualified class
        // instance creation expression, then the ClassOrInterfaceType must name
        // a class that is accessible and not abstract, or a compile-time error
        // occurs. In this case, the class being instantiated is the class
        // denoted by ClassOrInterfaceType.
        try {
            type = (CReferenceType)type.checkType(context);            
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }

        check(
            context,
            !type.isTypeVariable(),
            KjcMessages.NEW_TVPE_VARIABLE,
            type);
        check(
            context,
            !type.getCClass().isAbstract(),
            KjcMessages.NEW_ABSTRACT,
            type);
        check(
            context,
            !type.getCClass().isInterface(),
            KjcMessages.NEW_INTERFACE,
            type);
        check(
            context,
            type.getCClass().isAccessible(local),
            KjcMessages.CLASS_NOACCESS,
            type.getCClass());

        /////////////////////////////////////////////////////////////////////////

        CType[] argsType = new CType[params.length];

        for (int i = 0; i < argsType.length; i++) {
            params[i] = params[i].analyse(context);
            argsType[i] = params[i].getType(factory);
            verify(argsType[i] != null);
        }

        //!!! review and create test cases
        context = new CExpressionContext(context, context.getEnvironment());

        try {
            constructor =
                type.getCClass().lookupMethod(
                    context,
                    local,
                    null,
                    JAV_CONSTRUCTOR,
                    argsType,
                    type.getArguments());
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }

        if (constructor == null
            || constructor.getOwner() != type.getCClass()) {
            // do not want a super constructor !
            throw new CMethodNotFoundError(
                getTokenReference(),
                null,
                type.toString(),
                argsType);
        }

        // check access
        check(
            context,
            constructor.isAccessible(local),
            KjcMessages.CONSTRUCTOR_NOACCESS,
            type);
        // JLS 6.6.2.2  Qualified Access to a protected constructor
        // very special case for protected constructors
        check(
            context,
            !constructor.isProtected()
                || constructor.getOwner().getPackage() == local.getPackage(),
            KjcMessages.CONSTRUCTOR_NOACCESS,
            type);

        if (constructor.getOwner().isNested()) {
            check(
                context,
                !constructor.getOwner().hasOuterThis()
                    || (!context.isStaticContext()
                        && (inCorrectOuter(local,
                            constructor.getOwner().getOwner()))),
                KjcMessages.INNER_INHERITENCE,
                constructor.getOwnerType(),
                local.getAbstractType());
            if (constructor.getOwner().hasOuterThis()
                && !local.descendsFrom(constructor.getOwner().getOwner())) {
                CClass itsOuterOfThis = local;

                while (!itsOuterOfThis
                    .getOwner()
                    .descendsFrom(constructor.getOwner().getOwner())) {
                    itsOuterOfThis = itsOuterOfThis.getOwner();
                }
                // anlayse creates accessor(s) to the correct this$0 field
                outerPrefix =
                    new JFieldAccessExpression(
                        getTokenReference(),
                        new JOwnerExpression(
                            getTokenReference(),
                            itsOuterOfThis),
                        JAV_OUTER_THIS).analyse(
                        context);
            }

        }

        CReferenceType[] exceptions = constructor.getThrowables();
        for (int i = 0; i < exceptions.length; i++) {
            context.getBodyContext().addThrowable(
                new CThrowableInfo(exceptions[i], this));
        }

        argsType = constructor.getParameters();

        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].convertType(context, argsType[i]);
        }

        return this;
    }

    private boolean inCorrectOuter(CClass local, CClass outer) {
        while (local != null) {
            if (local.descendsFrom(outer)) {
                return true;
            }
            local = local.getOwner();
        }
        return false;
    }

    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------

    /**
     * Generates JVM bytecode to evaluate this expression.
     *
     * @param	code		the bytecode sequence
     * @param	discardValue	discard the result of the evaluation ?
     */
    public void genCode(GenerationContext context, boolean discardValue) {
        CodeSequence code = context.getCodeSequence();

        setLineNumber(code);

        code.plantClassRefInstruction(
            opc_new,
            type.getCClass().getQualifiedName());

        if (!discardValue) {
            code.plantNoArgInstruction(opc_dup);
        }

        if (constructor.getOwner().isNested()
            && !constructor.getOwner().isStatic()
            && constructor.getOwner().hasOuterThis()) {
            // inner class
            if (outerPrefix == null
                && !(local.getOwner() != null
                    && local.getOwner().descendsFrom(
                        constructor.getOwner().getOwner()))) {
                code.plantLoadThis();
            }
            else {
                // create inner class in inner class
                if (outerPrefix == null) {
                    code.plantLoadThis();
                    code.plantFieldRefInstruction(
                        opc_getfield,
                        local.getAbstractType().getSignature().substring(
                            1,
                            local.getAbstractType().getSignature().length()
                                - 1),
                        JAV_OUTER_THIS,
                        local.getOwnerType().getSignature());
                }
                else {
                    outerPrefix.genCode(context, false);
                }
            }
        }

        for (int i = 0; i < params.length; i++) {
            params[i].genCode(context, false);
        }

        constructor.getOwner().genOuterSyntheticParams(context);

        constructor.genCode(context, true);
    }

    public void recurse(IVisitor s) {
        if(outerPrefix != null)
            outerPrefix.accept(s);
        for (int i = 0; i < params.length; i++) {
            params[i].accept(s);
        }
    }
    
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    //Walter start
    //  private JExpression[]		params;
    protected JExpression[] params;
    protected JExpression outerPrefix;
    //Walter end
    protected CClass local;
    protected CMethod constructor;
    protected CReferenceType type;
}
