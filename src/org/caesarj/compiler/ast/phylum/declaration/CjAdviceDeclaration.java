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
 * $Id: CjAdviceDeclaration.java,v 1.8 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.aspectj.CaesarAdviceKind;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CCjAdvice;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Represents an AdviceDeclaration in the Source Code.
 * 
 * @author Jürgen Hallpap
 */
public class CjAdviceDeclaration
    extends CjMethodDeclaration
    implements CaesarConstants {

    public static final CjAdviceDeclaration[] EMPTY =
        new CjAdviceDeclaration[0];

    /** Pointcut */
    private CaesarPointcut pointcut;

    /** Kind of Advice (Before,After,AfterReturning,AfterThrowing,Around).*/
    private CaesarAdviceKind kind;

    /** The proceed method for around advices.*/
    private CjProceedDeclaration proceedMethodDeclaration;

    /** Flags, that show which extraArgument are needed (e.g. AroundClosure).*/
    private int extraArgumentFlags = 0;

    /** The parameters for the proceed-method which will be created later on for around-advices.*/
    private JFormalParameter[] proceedParameters;

    public CjAdviceDeclaration(
        TokenReference where,
        int modifiers,
        CType returnType,
        JFormalParameter[] parameters,
        CReferenceType[] exceptions,
        JBlock body,
        JavadocComment javadoc,
        JavaStyleComment[] comments,
        CaesarPointcut pointcut,
        CaesarAdviceKind kind,
        boolean hasExtraParameter) {
        super(
            where,
            modifiers,
            returnType,
            ADVICE_METHOD,
            parameters,
            exceptions,
            body,
            javadoc,
            comments);

        this.pointcut = pointcut;
        this.kind = kind;

        if (kind == CaesarAdviceKind.Around) {
            addAroundClosureParameter();
        }

        if (hasExtraParameter) {
            extraArgumentFlags |= CaesarConstants.ExtraArgument;
        }
    }

    /**
     * Adds an aroundClosure parameter to around advices.
     */
    private void addAroundClosureParameter() {

        JFormalParameter[] newParameters =
            new JFormalParameter[parameters.length + 1];

        System.arraycopy(parameters, 0, newParameters, 0, parameters.length);

        CType aroundClosureType = new CClassNameType(AROUND_CLOSURE_CLASS);

        newParameters[newParameters.length - 1] =
            new JFormalParameter(
                TokenReference.NO_REF,
                JFormalParameter.DES_GENERATED,
                aroundClosureType,
                AROUND_CLOSURE_PARAMETER,
                false);

        parameters = newParameters;

        //needed for proceed-method creation
        proceedParameters = newParameters;

    }

    public CSourceMethod checkInterface(CClassContext context)
        throws PositionedError {

        try {
            if (returnType.isReference()) {
                returnType = returnType.checkType(context);
            }
        }
        catch (UnpositionedError e) {
            // FJTODO what to do with exception
        }

        CBinaryTypeContext typeContext =
            new CBinaryTypeContext(
                context.getClassReader(),
                context.getTypeFactory(),
                context,
                (modifiers & ACC_STATIC) == 0);

        CType[] parameterTypes = new CType[parameters.length];
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].checkInterface(typeContext);
            parameterNames[i] = parameters[i].getIdent();
        }

        CCjAdvice adviceMethod =
            new CCjAdvice(
                context.getCClass(),
                ACC_PUBLIC,
                ident,
                returnType,
                parameterTypes,
                exceptions,
                body,
                pointcut,
                kind,
                extraArgumentFlags);

        setInterface(adviceMethod);

        return adviceMethod;
    }

    public boolean isAroundAdvice() {
        return kind.equals(CaesarAdviceKind.Around);
    }

    public CjProceedDeclaration getProceedMethodDeclaration() {
        return proceedMethodDeclaration;
    }

    public void setProceedMethodDeclaration(CjProceedDeclaration proceedMethodDeclaration) {
        this.proceedMethodDeclaration = proceedMethodDeclaration;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public CaesarAdviceKind getKind() {
        return kind;
    }

    public void setParameters(JFormalParameter[] parameters) {
        this.parameters = parameters;
    }

    public CaesarPointcut getPointcut() {
        return pointcut;
    }

    public JBlock getBody() {
        return body;
    }

    public void setBody(JBlock body) {
        this.body = body;
    }

    /**
     * Sets the corresponding bit in the extraArgumentFlag.
     * 
     * @param extraArgumentFlags The extraArgumentFlags to set
     */
    public void setExtraArgumentFlag(int extraArgumentFlag) {
        this.extraArgumentFlags |= extraArgumentFlag;
    }

    public JFormalParameter[] getProceedParameters() {

        if (isAroundAdvice()) {
            return proceedParameters;
        }
        else {
            return JFormalParameter.EMPTY;
        }

    }

    /**
     * @see org.caesarj.kjc.JMethodDeclaration#checkBody1(CClassContext)
     */
    public void checkBody1(CClassContext context) throws PositionedError {
        super.checkBody1(context);

        //create a method attribute for the advice
        // this has to be done after the pointcut declarations are resolved	
        getCaesarAdvice().createAttribute(
            context,
            context.getCClass(),
            parameters,
            getTokenReference());
    }

    public CCjAdvice getCaesarAdvice() {
        return (CCjAdvice)getMethod();
    }    
}