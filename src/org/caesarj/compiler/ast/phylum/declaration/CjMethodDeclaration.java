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
 * $Id: CjMethodDeclaration.java,v 1.8 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

// FJKEEP we will need this one later
public class CjMethodDeclaration extends JMethodDeclaration {

    public CjMethodDeclaration(
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
    }

}
