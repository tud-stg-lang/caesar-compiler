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
 * $Id: CField.java,v 1.3 2004-10-15 11:12:53 aracic Exp $
 */

package org.caesarj.compiler.export;

import java.util.Hashtable;

import org.caesarj.classfile.FieldInfo;
import org.caesarj.compiler.ast.phylum.declaration.JAccessorMethod;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.context.AdditionalGenerationContext;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;

/**
 * This class represents an exported member of a class (fields)
 */
public abstract class CField extends CMember {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a field export
     * 
     * @param owner
     *            the owner of this field
     * @param modifiers
     *            the modifiers on this field
     * @param ident
     *            the name of this field
     * @param type
     *            the type of this field
     * @param deprecated
     *            is this field deprecated ?
     */
    public CField(
        CClass owner,
        int modifiers,
        String ident,
        CType type,
        boolean deprecated,
        boolean synthetic) {
        super(owner, modifiers, ident, deprecated, synthetic);
        this.type = type;
    }

    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------

    /**
     * Checks whether this type is accessible from the specified class (JLS
     * 6.6).
     * 
     * @return true iff this member is accessible
     */
    //   public boolean isAccessible(CClass from) {
    //     throw new InconsistencyException("accesability depends on primary - use
    // 'isAccessible(primary, from)'");
    //   }
    /**
     * Checks whether this type is accessible from the specified class (JLS
     * 6.6).
     * 
     * @return true iff this member is accessible
     */
    public boolean isAccessible(CClass primary, CClass from) {
        if (!super.isAccessible(from)) {
            return false;
        }
        else {
            // JLS 6.6.2.1
            // Access to a protected Member
            if (isProtected() && !isStatic()
                && !(getOwner().getPackage() == from.getPackage())
                && !(primary == null)) {
                if (from.isAnonymous()
                    && primary.getCClass().descendsFrom(from.getOwner())) {
                    return true;
                }
                return primary.descendsFrom(from);
            }
            else {
                return true;
            }
        }
    }

    /**
     * @return the interface
     */
    public CField getField() {
        return this;
    }

    /**
     * @return the type of this field
     */
    public CType getType() {
        return type;
    }

    /**
     * @return the type of this field
     */
    public void setType(CType type) {
        this.type = type;
    }

    /**
     * @return true iff this field is analysed
     */
    public boolean isAnalysed() {
        return analysed;
    }

    /**
     * Set if this field is analysed
     */
    public void setAnalysed(boolean analysed) {
        this.analysed = analysed;
    }

    /**
     * @param value
     *            the value known at third pass
     */
    public void setValue(JExpression value) {
        this.value = value;
    }

    /**
     * @return the value of initializer or null
     */
    public JExpression getValue() {
        return value;
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        return getQualifiedName();
    }

    public CMethod getAccessor(
        TypeFactory typeFactory,
        CSourceClass target,
        boolean leftSide,
        boolean isSuper,
        int oper) {
        if (leftSide) {
            CSourceMethod accessorSet;

            if (accessorSetter == null) {
                accessorSetter = new Hashtable();
                accessorSet = null;
            }
            else {
                accessorSet = (CSourceMethod) accessorSetter.get(target);
            }
            if (accessorSet == null) {
                JAccessorMethod accessor = new JAccessorMethod(
                    typeFactory,
                    target,
                    this,
                    leftSide,
                    isSuper,
                    oper);
                if (oper == OPE_SIMPLE) {
                    accessorSetter.put(target, accessor.getMethod());
                }
                accessorSet = (CSourceMethod) accessor.getMethod();
            }
            return accessorSet;
        }
        else {
            CSourceMethod accessorGet;

            if (accessorGetter == null) {
                accessorGetter = new Hashtable();
                accessorGet = null;
            }
            else {
                accessorGet = (CSourceMethod) accessorGetter.get(target);
            }
            if (accessorGet == null) {
                JAccessorMethod accessor = new JAccessorMethod(
                    typeFactory,
                    target,
                    this,
                    leftSide,
                    isSuper,
                    oper);
                accessorGetter.put(target, accessor.getMethod());
                accessorGet = (CSourceMethod) accessor.getMethod();
            }
            return accessorGet;
        }
    }

    // ----------------------------------------------------------------------
    // GENERATE CLASSFILE INFO
    // ----------------------------------------------------------------------

    /**
     * Generates a sequence of bytecodes to load
     * 
     * @param code
     *            the code list
     */
    public void genLoad(GenerationContext context) {
        CodeSequence code = context.getCodeSequence();

        code.plantFieldRefInstruction(
            isStatic() ? opc_getstatic : opc_getfield,
            getPrefixName(),
            getIdent(),
            getType().getSignature());
    }

    /**
     * Generates a sequence of bytecodes to load
     * 
     * @param code
     *            the code list
     */
    public void genStore(GenerationContext context) {
        CodeSequence code = context.getCodeSequence();

        /*
         * code.plantFieldRefInstruction(isStatic() ? opc_putstatic :
         * opc_putfield, getPrefixName(), getIdent(), getType().getSignature());
         */

        // IVICA code above replaced by this:
        CClass currentClass = AdditionalGenerationContext.instance()
            .getCurrentClass();

        if (getOwner().isMixin()) {
            code.plantFieldRefInstruction(
                isStatic() ? opc_putstatic : opc_putfield,
                currentClass.getQualifiedName(),
                getIdent(),
                getType().getSignature());
        }
        else {
            code.plantFieldRefInstruction(isStatic()
                ? opc_putstatic
                : opc_putfield, getPrefixName(), getIdent(), getType()
                .getSignature());
        }
    }

    // ----------------------------------------------------------------------
    // GENERATE CLASSFILE INFO
    // ----------------------------------------------------------------------

    /**
     * Returns the constant value of a constant final field or null.
     */
    public Object getConstantValue(TypeFactory factory) {
        if (!(isFinal() && isStatic() && value != null && value.isConstant())) {
            return null;
        }
        else {
            value = value.getLiteral();

            switch (value.getType(factory).getTypeID()) {
            case TID_BYTE:
                return new Integer(value.byteValue());
            case TID_SHORT:
                return new Integer(value.shortValue());
            case TID_CHAR:
                return new Integer(value.charValue());
            case TID_INT:
                return new Integer(value.intValue());
            case TID_LONG:
                return new Long(value.longValue());
            case TID_FLOAT:
                return new Float(value.floatValue());
            case TID_DOUBLE:
                return new Double(value.doubleValue());
            case TID_NULL:
            case TID_CLASS:
                if (type.equals(factory
                    .createReferenceType(TypeFactory.RFT_STRING))) {
                    return value.stringValue();
                }
                else {
                    return null;
                }
            case TID_BOOLEAN:
                return new Integer(value.booleanValue() ? 1 : 0);
            default:
                return null;
            }
        }
    }

    /**
     * Generate the code in a class file
     */
    public FieldInfo genFieldInfo(TypeFactory factory) {
        return new FieldInfo(
            (short) getModifiers(),
            getIdent(),
            type.getSignature(),
            type.getGenericSignature(),
            getConstantValue(factory),
            isDeprecated(),
            isSynthetic());
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    private CType type;

    private boolean analysed;

    private JExpression value;

    //   private CMethod accessorSet;
    //   private CMethod accessorGet;
    private Hashtable accessorSetter;

    private Hashtable accessorGetter;
}