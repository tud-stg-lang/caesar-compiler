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
 * $Id: CaesarTypeSystem.java,v 1.9 2005-01-21 18:16:25 aracic Exp $
 */

package org.caesarj.compiler.typesys;

import org.caesarj.compiler.Log;
import org.caesarj.compiler.typesys.graph.CaesarTypeGraph;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeGraph;
import org.caesarj.compiler.typesys.visitor.AddImplicitTypesAndRelationsVisitor;
import org.caesarj.compiler.typesys.visitor.DumpTypesVisitor;
import org.caesarj.compiler.typesys.visitor.FurtherbindingVisitor;
import org.caesarj.compiler.typesys.visitor.MixinListVisitor;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeSystem {
    
	private CaesarTypeGraph caesarTypeGraph = new CaesarTypeGraph();
	private JavaTypeGraph javaTypeGraph = new JavaTypeGraph();

	public CaesarTypeGraph getCaesarTypeGraph() {
		return caesarTypeGraph;
	}
	
	public JavaTypeGraph getJavaTypeGraph() {
		return javaTypeGraph;
	}
	
	public String findInContextOf(String classQn, String contextClassQn) {
	    String res = null;
	    
	    CaesarTypeGraph g = getCaesarTypeGraph();
        CaesarTypeNode prefixN = caesarTypeGraph.getType(new JavaQualifiedName(classQn));
        CaesarTypeNode contextN = caesarTypeGraph.getType(new JavaQualifiedName(contextClassQn));
        
        CaesarTypeNode n = prefixN.getTypeInContextOf(contextN);
        
        if(n != null)
            res = n.getQualifiedName().toString();
                    
        Log.verbose(classQn+" in context of "+contextClassQn);
        Log.verbose("\t->"+res);
        
        return res;
    }
	
	public void generate() {
		DumpTypesVisitor dumpTypesVisitor = new DumpTypesVisitor(caesarTypeGraph);
		
		//dumpTypesVisitor.run();
		
		new AddImplicitTypesAndRelationsVisitor(caesarTypeGraph).run();
		new FurtherbindingVisitor(caesarTypeGraph).run();
		new MixinListVisitor(caesarTypeGraph).run();
		
		dumpTypesVisitor.run();
		
		javaTypeGraph.generateFrom(caesarTypeGraph);
		javaTypeGraph.debug();
    }

    public boolean isIncrementOf(String qnFurtherbinding, String qnFurtherbound) {
        CaesarTypeNode furtherbinding = 
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbinding));
        
        CaesarTypeNode furtherbound =
            getCaesarTypeGraph().getType(new JavaQualifiedName(qnFurtherbound));
        
        if(furtherbinding!=null && furtherbound!=null) {
            return furtherbinding.isIncrementFor(furtherbound);
        }            
        
        return false;
    }
}
