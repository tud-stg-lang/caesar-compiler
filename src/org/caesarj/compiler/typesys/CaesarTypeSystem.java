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
 * $Id: CaesarTypeSystem.java,v 1.12 2005-11-01 16:23:42 gasiunas Exp $
 */

package org.caesarj.compiler.typesys;

import org.caesarj.compiler.CompilerBase;
import org.caesarj.compiler.Log;
import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.input.InputTypeGraph;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeGraph;
import org.caesarj.compiler.typesys.join.JoinedTypeGraph;
import org.caesarj.compiler.typesys.join.JoinedTypeNode;
import org.caesarj.compiler.typesys.visitor.DumpTypesVisitor;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeSystem {
    
	private JoinedTypeGraph joinedTypeGraph;
	private CaesarTypeGraph caesarTypeGraph;
	private JavaTypeGraph javaTypeGraph = new JavaTypeGraph();

	public CaesarTypeGraph getCaesarTypeGraph() {
		return caesarTypeGraph;
	}
	
	public JavaTypeGraph getJavaTypeGraph() {
		return javaTypeGraph;
	}
	
	public void generate(InputTypeGraph inputGraph, CompilerBase compiler) {
		try {
			joinedTypeGraph = new JoinedTypeGraph(inputGraph, compiler);
			caesarTypeGraph = new CaesarTypeGraph(joinedTypeGraph);
			
			new DumpTypesVisitor(caesarTypeGraph).run();
			
			javaTypeGraph.generateFrom(caesarTypeGraph);
			javaTypeGraph.debug();
		}
		catch (CaesarTypeSystemException e) {
			// do nothing, because a corresponding compiler error is reported
		}
    }
	
	public String findInContextOf(String classQn, String contextClassQn) {
	    String res = null;
	    
	    JoinedTypeNode prefixN = joinedTypeGraph.getNodeByName(new JavaQualifiedName(classQn));
	    JoinedTypeNode contextN = joinedTypeGraph.getNodeByName(new JavaQualifiedName(contextClassQn));
        
	    JoinedTypeNode n = prefixN.getTypeInContextOf(contextN);
        
        if(n != null)
            res = n.getQualifiedName().toString();
                    
        Log.verbose(classQn+" in context of "+contextClassQn);
        Log.verbose("\t->"+res);
        
        return res;
    }
	
	public boolean isIncrementOf(String qnFurtherbinding, String qnFurtherbound) {
        CaesarTypeNode furtherbinding = 
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbinding));
        
        CaesarTypeNode furtherbound =
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbound));
        
        if (furtherbinding != null && furtherbound != null) {
            return furtherbinding.isIncrementFor(furtherbound);
        }            
        
        return false;
    }
}
