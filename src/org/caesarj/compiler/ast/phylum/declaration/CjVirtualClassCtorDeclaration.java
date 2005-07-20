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
 * $Id: CjVirtualClassCtorDeclaration.java,v 1.6 2005-07-20 10:06:00 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.phylum.expression.JConstructorCall;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.statement.JEmptyStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.TokenReference;

/**
 * Generates:
 * 
 * if outerType != null ->
 * 
 * {ident}(Object _$outer) { super(_$outer); $outer = ({outerType})_$outer; }
 * 
 * otherwise ->
 * 
 * {ident}(Object _$outer) { super(_$outer); }
 * 
 * @author Ivica Aracic
 */
public class CjVirtualClassCtorDeclaration extends JConstructorDeclaration {

    public CjVirtualClassCtorDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CType outerType,
        TypeFactory factory) {
        super(where, modifiers, ident, new JFormalParameter[] {
            genFormalParameter(where, factory)
        }, CReferenceType.EMPTY, new JConstructorBlock(
            where,
            genSuperCall(where),
            outerType != null ? new JStatement[] {
                genOuterInitializer(where, outerType)
            } : JStatement.EMPTY), null, null, factory);
    }

    public CjVirtualClassCtorDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CType outerType,
        JBlock body,
        TypeFactory factory) {
        super(where, modifiers, ident, new JFormalParameter[] {
            genFormalParameter(where, factory)
        }, CReferenceType.EMPTY, new JConstructorBlock(
            where,
            genSuperCall(where),
            outerType != null ? new JStatement[] {
                    genOuterInitializer(where, outerType), body
            } : new JStatement[] {
                body
            }), null, null, factory);
    }

    private static JFormalParameter genFormalParameter(
        TokenReference where,
        TypeFactory factory) {
//        return new JFormalParameter(where, 0, factory
//            .createReferenceType(TypeFactory.RFT_OBJECT), "_$outer", true);
        return new JFormalParameter(where, JLocalVariable.DES_GENERATED, factory
                .createReferenceType(TypeFactory.RFT_OBJECT), "_$outer", true);
    }

    private static JConstructorCall genSuperCall(TokenReference where) {
        return new JConstructorCall(where, false, null, new JExpression[] {
            new JNameExpression(where, "_$outer")
        });
    }

    private static JStatement genOuterInitializer(
        TokenReference where,
        CType outerType) {
        
        return new JEmptyStatement(where, null);
        
        /*
        return new JExpressionStatement(where, new JAssignmentExpression(
            where,
            new JNameExpression(where, CaesarConstants.OUTER_FIELD),
            new JCastExpression(
                where,
                new JNameExpression(where, "_$outer"),
                outerType)), null);
        */
    }
}