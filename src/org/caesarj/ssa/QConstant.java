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
 * $Id: QConstant.java,v 1.1 2003-07-05 18:29:37 werner Exp $
 */

package org.caesarj.ssa;

import org.caesarj.util.InconsistencyException;
import org.caesarj.classfile.Constants;
import org.caesarj.classfile.NoArgInstruction;
import org.caesarj.classfile.PushLiteralInstruction;


/**
 * A class to represent constant operand of quadruple instructions
 */
public class QConstant extends QOperand {

    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct a null constant.
     */
    public QConstant() {
	this.nullConstant = true;
	this.type = Constants.TYP_REFERENCE;
    }

    /**
     * Construct a constant
     *
     * @param value the value of the constant
     * @param type type of the constant
     */
    public QConstant(Object value, byte type) {
	this.value = value;
	if (value instanceof String) {
	    //Because PushLiteralInstruction return TYP_INT
	    // for a String.
	    //TO REMOVE if PushLiteralInstruction change.
	    this.type = Constants.TYP_REFERENCE;
	} else {
	    this.type = type;
	}
    }

    // -------------------------------------------------------------------
    // PROPERTIES
    // -------------------------------------------------------------------
    /**
     * Test if the operand may throw an exception
     */
    public boolean mayThrowException() {
	return false;
    }

    /**
     * Test if the operand has side effects.
     */
    public boolean hasSideEffects() {
	return false;
    }

    /**
     * Return the type of the operand.
     */
    public byte getType() {
	return type;
    }

    /**
     * Test if the operand is constant
     */
    public boolean isConstant() {
	return true;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Return the value
     */
    public Object getValue() {
	return value;
    }

    /**
     * A representation of the instruction
     */
    public String toString() {
	if (nullConstant)
	    return "null";
	if (value instanceof Integer) {
	    return "" +  ((Integer) value).intValue();
	} else if (value instanceof Long) {
	    return "" +  ((Long) value).longValue();
	} else if (value instanceof Float) {
	    return "" +  ((Float) value).floatValue();
	} else if (value instanceof Double) {
	    return "" +  ((Double) value).doubleValue();
	} else if (value instanceof String) {
	    return "" +  (String) value;
	} else {
	    return "constant";
	}
    }

    /**
     * Test if this is the null constant
     */
    public boolean isNull() {
	return nullConstant;
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the classfile instructions for this operand
     *
     * @param codeGen the code generator
     */
    public void generateInstructions(CodeGenerator codeGen) {
	if (nullConstant) {
	    codeGen.addInstruction(new NoArgInstruction(Constants.opc_aconst_null));
	} else {
	    if (value instanceof Integer) {
		codeGen.addInstruction(new PushLiteralInstruction(((Integer)value).intValue()));
	    } else if (value instanceof Long) {
		codeGen.addInstruction(new PushLiteralInstruction(((Long)value).longValue()));
	    } else if (value instanceof Float) {
		codeGen.addInstruction(new PushLiteralInstruction(((Float)value).floatValue()));
	    } else if (value instanceof Double) {
		codeGen.addInstruction(new PushLiteralInstruction(((Double)value).doubleValue()));
	    } else if (value instanceof String) {
		codeGen.addInstruction(new PushLiteralInstruction((String)value));
	    }
	}
    }

    /**
     * Generate the classfile instructions for save in
     * this operand the value actually son the stack.
     *
     * This method must not be use with a constant.
     */
    public void generateStore(CodeGenerator codeGen) {
	throw new InconsistencyException("Cannot store in constant value");
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected boolean nullConstant;
    protected Object value;
    protected byte type;
}
