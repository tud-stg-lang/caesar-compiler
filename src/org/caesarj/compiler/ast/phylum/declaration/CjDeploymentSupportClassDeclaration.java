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
 * $Id: CjDeploymentSupportClassDeclaration.java,v 1.8 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class declaration is only for the generated deployment support classes.
 * 
 * @author Jürgen Hallpap
 */
public class CjDeploymentSupportClassDeclaration extends CjClassDeclaration {

    private CjClassDeclaration crosscuttingClass;

    private String postfix;

    public CjDeploymentSupportClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CReferenceType superClass,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment,
        CjClassDeclaration crosscuttingClass,
        String postfix) {
        this(
            where,
            modifiers,
            ident,
            superClass,
            interfaces,
            fields,
            methods,
            inners,
            initializers,
            javadoc,
            comment,
            CjPointcutDeclaration.EMPTY,
            CjAdviceDeclaration.EMPTY,
            null,
            crosscuttingClass,
            postfix);
    }

    public CjDeploymentSupportClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CReferenceType superClass,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment,
        CjPointcutDeclaration[] pointcuts,
        CjAdviceDeclaration[] advices,
        CaesarDeclare[] declares,
        CjClassDeclaration crosscuttingClass,
        String postfix) {
        super(
            where,
            modifiers,
            ident,
            superClass,
            null,
            interfaces,
            fields,
            methods,
            inners,
            initializers,
            javadoc,
            comment,
            pointcuts,
            advices,
            declares);

        this.crosscuttingClass = crosscuttingClass;
        this.postfix = postfix;
    }

    
    public void createImplicitCaesarTypes(CContext context) {
        // do nothing here
    }
    
    public void adjustSuperType(CContext context) throws PositionedError {
		// do nothing here
	}
    
    /**
     * Sets the superclass of this deployment class if needed.
     */
    public void checkInterface(CContext context) throws PositionedError {
        super.checkInterface(context); 
    }
}
