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
 * $Id: JLocalVariableExpression.java,v 1.12 2005-09-27 13:43:03 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.ast.phylum.expression.literal.JLiteral;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.cclass.CastUtils;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBlockContext;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CExpressionContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.context.CVariableInfo;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.family.ArgumentAccess;
import org.caesarj.compiler.family.ContextExpression;
import org.caesarj.compiler.family.FieldAccess;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Root class for all expressions
 */
public class JLocalVariableExpression extends JExpression {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Construct a node in the parsing tree
     * 
     * @param where
     *            the line of this node in the source code
     */
    public JLocalVariableExpression(
        TokenReference where,
        JLocalVariable variable) {
        super(where);
        this.variable = variable;
    }

    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------

    /**
     * Returns true if this field accept assignment
     */
    public boolean isLValue(CExpressionContext context) {
        return !variable.isFinal() || !mayBeInitialized(context);
    }

    /**
     * Returns true if there must be exactly one initialization of the variable.
     * 
     * @return true if the variable is final.
     */
    public boolean isFinal() {
        return variable.isFinal();
    }

    /**
     * Returns true if this field is already initialized
     */
    public boolean isInitialized(CExpressionContext context) {
        return CVariableInfo.isInitialized(context.getBodyContext()
            .getVariableInfo(variable.getIndex()));
    }

    /**
     * Returns true if this field may be initialized (used for assignment)
     */
    private boolean mayBeInitialized(CExpressionContext context) {
        return CVariableInfo.mayBeInitialized(context.getBodyContext()
            .getVariableInfo(variable.getIndex()));
    }

    /**
     * Declares this variable to be initialized.
     *  
     */
    public void setInitialized(CExpressionContext context) {
        context.getBodyContext().setVariableInfo(
            variable.getIndex(),
            CVariableInfo.INITIALIZED);
    }

    /**
     * Returns the position of this variable in the sets of local vars
     */
    public int getPosition() {
        return variable.getPosition();
    }

    /**
     * Compute the type of this expression (called after parsing)
     * 
     * @return the type of this expression
     */
    public CType getType(TypeFactory factory) {
        return variable.getType();
    }

    public String getIdent() {
        return variable.getIdent();
    }

    /**
     * Tests whether this expression denotes a compile-time constant (JLS
     * 15.28).
     * 
     * @return true iff this expression is constant
     */
    public boolean isConstant() {
        return variable.isConstant();
    }

    /**
     * Returns the literal value of this field
     */
    public JLiteral getLiteral() {
        return (JLiteral) variable.getValue();
    }

    public JLocalVariable getVariable() {
        return variable;
    }

    // ----------------------------------------------------------------------
    // SEMANTIC ANALYSIS
    // ----------------------------------------------------------------------

    /**
     * Analyses the expression (semantically).
     * 
     * @param context
     *            the analysis context
     * @return an equivalent, analysed expression
     * @exception PositionedError
     *                the analysis detected an error
     */
    public JExpression analyse(CExpressionContext context)
        throws PositionedError {

        // IVICA: store family information
        calcFamilyType(context);
        
        // IVICA: insert cast
        if(!context.isLeftSide()) {
	        CType castType = 
	            CastUtils.instance().castFrom(
	                context, variable.getType(), context.getClassContext().getCClass());
	        
	        if(castType != null) {
	            return new CjCastExpression(
	                getTokenReference(),
	                this,
	                castType
	            );
	        }
        }
            
        
        if (!context.isLeftSide() || !context.discardValue()) {
            variable.setUsed();
        }
        if (context.isLeftSide()) {
            variable.setAssigned(getTokenReference(), context.getBodyContext());
        }

        check(
            context,
            CVariableInfo.isInitialized(context.getBodyContext()
                .getVariableInfo(variable.getIndex()))
                || (context.isLeftSide() && context.discardValue()),
            KjcMessages.UNINITIALIZED_LOCAL_VARIABLE,
            variable.getIdent());

        if (variable.isConstant() && !context.isLeftSide()) {
            return variable.getValue();
        }
        
        return this;
    }
    
    public void calcFamilyType(CContext context) throws PositionedError {
    	try {
            CType type = variable.getType();            
	        if(type.isReference()) {
                
                int k = 0;
                                
                CContext ctx = context.getBlockContext();
                
                if (variable instanceof JFormalParameter) {
                    // find next MethodContext
                    while (! (ctx instanceof CMethodContext)) {
                        ctx = ctx.getParentContext();
                        k++;
                    }
                } 
                else if (variable instanceof JVariableDefinition) {
                    // find next block-context that declares this variable
                    do {
                        if (! (ctx instanceof CBodyContext)){                            
                            throw new InconsistencyException("Cannot find "+variable.getIdent());
                        }
                        
                        if(ctx instanceof CBlockContext) {                           
                            CBlockContext block = (CBlockContext)ctx;
                            if (block.containsVariable(variable.getIdent())) {
                                break;
                            }
                        } 

                        // we only want block context
                        ctx = ctx.getParentContext();
                        k++;                        
                    } while (true);                    
                }
                
                ContextExpression ctxExpr = new ContextExpression(null, k, null);
                
                if(variable instanceof JFormalParameter) {
                    thisAsFamily = new ArgumentAccess(this.isFinal(), ctxExpr, (CReferenceType)variable.getType(), variable.getIndex());
                }
                else {
                    thisAsFamily = new FieldAccess(this.isFinal(), ctxExpr, variable.getIdent(), (CReferenceType)variable.getType());
                }
                
	            if(type.isCaesarReference())
	                family = thisAsFamily.normalize();
	        }
        }
        catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
    }

    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------
    public boolean equals(Object o) {
        return (o instanceof JLocalVariableExpression)
            && variable.equals(((JLocalVariableExpression) o).variable);
    }

    /**
     * Generates JVM bytecode to evaluate this expression.
     * 
     * @param code
     *            the bytecode sequence
     * @param discardValue
     *            discard the result of the evaluation ?
     */
    public void genCode(GenerationContext context, boolean discardValue) {
        CodeSequence code = context.getCodeSequence();

        if (!discardValue) {
            setLineNumber(code);
            variable.genLoad(context);
        }
    }

    /**
     * Generates JVM bytecode to store a value into the storage location denoted
     * by this expression.
     * 
     * Storing is done in 3 steps : - prefix code for the storage location (may
     * be empty), - code to determine the value to store, - suffix code for the
     * storage location.
     * 
     * @param code
     *            the code list
     */
    public void genStartStoreCode(GenerationContext context) {
        // nothing to do here
    }

    /**
     * Generates JVM bytecode to for compound assignment, pre- and postfix
     * expressions.
     * 
     * @param code
     *            the code list
     */
    public void genStartAndLoadStoreCode(
        GenerationContext context,
        boolean discardValue) {
        genCode(context, discardValue);
    }

    /**
     * Generates JVM bytecode to store a value into the storage location denoted
     * by this expression.
     * 
     * Storing is done in 3 steps : - prefix code for the storage location (may
     * be empty), - code to determine the value to store, - suffix code for the
     * storage location.
     * 
     * @param code
     *            the code list
     * @param discardValue
     *            discard the result of the evaluation ?
     */
    public void genEndStoreCode(GenerationContext context, boolean discardValue) {
        CodeSequence code = context.getCodeSequence();
        TypeFactory factory = context.getTypeFactory();

        if (!discardValue) {
            int opcode;

            if (getType(factory).getSize() == 2) {
                opcode = opc_dup2;
            }
            else {
                opcode = opc_dup;
            }
            code.plantNoArgInstruction(opcode);
        }
        variable.genStore(context);
    }

    public void recurse(IVisitor s) {
        variable.accept(s);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("JLocalVariableExpression[");
        buffer.append(variable);
        buffer.append("]");
        return buffer.toString();
    }
    
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    JLocalVariable variable;
}