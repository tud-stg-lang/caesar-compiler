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
 * $Id: CjAdviceMethodDeclaration.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

/**
 * Needed in order to be able to separate common JMethod Declarations from
 * AdviceMethods
 * 
 * @author Ivica Aracic
 */
public class CjAdviceMethodDeclaration extends CjMethodDeclaration {

    protected CjAdviceDeclaration advice;
    
    public CjAdviceMethodDeclaration(
        CjAdviceDeclaration advice,
        TokenReference where,
        int modifiers,
        CType returnType,
        String ident,
        JFormalParameter[] parameters,
        CReferenceType[] exceptions,
        JBlock body,
        JavadocComment javadoc,
        JavaStyleComment[] comments) {
        super(
            where,
            modifiers,
            returnType,
            ident,
            parameters,
            exceptions,
            body,
            javadoc,
            comments);
        
        this.advice = advice;
    }
    
    public CjAdviceDeclaration getAdvice() {
        return advice;
    }
}