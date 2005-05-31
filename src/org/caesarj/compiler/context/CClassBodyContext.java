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
 * $Id: CClassBodyContext.java,v 1.6 2005-05-31 09:01:33 meffert Exp $
 */

package org.caesarj.compiler.context;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.declaration.JMethodDeclaration;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.util.TokenReference;

/**
 * @author Karl Klose
 */
public class CClassBodyContext extends CMethodContext {

    public static final String METHOD_NAME = "$TEMPORARY_METHOD_ENTRY"; 
    
    CMethod method;
    JMethodDeclaration	decl;
    
    public CClassBodyContext( CClassContext cctx, KjcEnvironment env ){
        super(cctx, env, null);
        method =         new CSourceMethod(
			                getClassContext().getCClass(),ACC_PRIVATE,METHOD_NAME,new CVoidType(),
			                new JFormalParameter[0], new CType[0], new CReferenceType[0],false, false,
			                	new JBlock(
			                	        TokenReference.NO_REF,
			                	        new JStatement[0],
			                	        new JavaStyleComment[0]
			                	)
			            	);

		decl  =        new JMethodDeclaration(
			                TokenReference.NO_REF,
			                ACC_PUBLIC,
			                new CVoidType(),
			                METHOD_NAME,
			                new JFormalParameter[0],
			                new CReferenceType[0],
			               	new JBlock(
			               	     TokenReference.NO_REF,
			            	        new JStatement[0],
			            	        new JavaStyleComment[0]
			            	),
			            	new JavadocComment("",false,false),
			            	new JavaStyleComment[0]
			        );
			    }

    
    
    
    public CMethod getCMethod() {
        return method;
    }
    
    public JMethodDeclaration getMethodDeclaration() {
        return decl;
     }
    
    
}
