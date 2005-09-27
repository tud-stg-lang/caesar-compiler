/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CjAdviceDeclaration.java,v 1.17 2005-09-27 13:42:00 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
 * @author J�rgen Hallpap
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
    
    /** Advice method ident */
    private String adviceMethodIdent = "undef";
    
    /** Relative orderNr of the advice, used to determine its precedence */
    private int orderNr = 0;
    
    /** Advice copies (needed for crosscutting view) */
    private List adviceCopies = new LinkedList(); /* List<CjAdviceDeclaration> */
    
    /** is this originally declared advice */
    private boolean declared;
    
    /** it the advice activated for weaving */
    private boolean active;
    
    /** The virtual class where this advice was declared */
    private CjVirtualClassDeclaration originalClass;
    
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
        this.orderNr = where.getLine();
        
        if (kind == CaesarAdviceKind.Around) {
            addAroundClosureParameter();
        }
        
        if (hasExtraParameter) {
            extraArgumentFlags |= CaesarConstants.ExtraArgument;
        }
        
        this.declared = true;
        this.active = true;
    }
    
    /**
     * Copy constructor
     */
    public CjAdviceDeclaration(CjAdviceDeclaration decl, CjVirtualClassDeclaration originalClass) {
    	super(decl.getTokenReference(), 
    		decl.modifiers, 
			decl.returnType, 
			decl.getIdent(),
			decl.parameters,
			decl.exceptions, 
			decl.body, 
			decl.javadoc, 
			decl.comments);
    	this.pointcut = decl.pointcut;
        this.kind = decl.kind;
        this.extraArgumentFlags = decl.extraArgumentFlags;
        this.proceedParameters = decl.proceedParameters;
        this.adviceMethodIdent = decl.adviceMethodIdent;
        this.orderNr = decl.orderNr;
        decl.addAdviceCopy(this);
        this.declared = false;
        this.active = true;
        this.originalClass = originalClass;
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
                getTokenReference(),
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

        // It there's an original class, use it to resolve the parameters types
        CBinaryTypeContext typeContext = null;
        CClassContext c = null;
        if (this.originalClass != null && this.originalClass.self != null) {            
            c = 
                originalClass.constructContext(
                        originalClass.self.getCompilationUnitContext());
            
            typeContext =
                new CBinaryTypeContext(
                        c.getClassReader(),
                        c.getTypeFactory(),
                        c,
                    (modifiers & ACC_STATIC) == 0);
        } else {
            typeContext =
                new CBinaryTypeContext(
                    context.getClassReader(),
                    context.getTypeFactory(),
                    context,
                    (modifiers & ACC_STATIC) == 0);
        }
        
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
				parameters,
                parameterTypes,
                exceptions,
                body,
                pointcut,
                kind,
                isGenerated(),
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
        
        // Create an advice context, using the class where the advice was declared.
        CClassContext adviceContext = context;
        if (this.originalClass != null && this.originalClass.self != null) {            
            adviceContext = 
                originalClass.constructContext(
                        originalClass.self.getCompilationUnitContext());
        }
        
        // Check the body using a regular context
        super.checkBody1(context);

        // create a method attribute for the advice
        // this has to be done after the pointcut declarations are resolved
        // Note that, if we don't have an originalClass, the adviceContext
        // is the normal context of the body.
        getCaesarAdvice().createAttribute(
            adviceContext,
            context.getCClass(),
            parameters,
            getTokenReference(),
			orderNr);
    }

    public CCjAdvice getCaesarAdvice() {
        return (CCjAdvice)getMethod();
    }
    
    public String getAdviceMethodIdent() {
        return adviceMethodIdent;
    }
    
    public void setAdviceMethodIdent(String ident) {
        this.adviceMethodIdent = ident;
    }
    
    public int getOrderNr() {
    	return orderNr;
    }
    
    public void setOrderNr(int orderNr) {
    	this.orderNr = orderNr;
    }
    
    public Iterator getAdviceCopies(int orderNr) {
    	return adviceCopies.iterator();
    }
    
    public void addAdviceCopy(CjAdviceDeclaration copy) {
    	adviceCopies.add(copy);
    }
    
    public boolean isActive() {
    	return active;
    }
    
    public boolean isDeclared() {
    	return declared;
    }
    
    public void deactivate() {
    	active = false;
    }
    
    public CjVirtualClassDeclaration getOriginalClass() {
    	return originalClass;
    }
}