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
 * $Id: CaesarTypeGraph.java,v 1.6 2005-11-01 16:23:42 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caesarj.compiler.typesys.input.InputTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.join.JoinedTypeGraph;
import org.caesarj.compiler.typesys.join.JoinedTypeNode;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeGraph {
	/** root of inner and inheritance hierarchy */
    private Map<JavaQualifiedName, CaesarTypeNode> typeMap = new HashMap<JavaQualifiedName, CaesarTypeNode>();
    
    private JoinedTypeGraph joinedGraph;
    
    public CaesarTypeGraph(JoinedTypeGraph joinedGraph) {
    	this.joinedGraph = joinedGraph;
    }
    
    public boolean hasType(String qualifiedName) {
        return typeMap.containsKey(qualifiedName);
    }
    
    public CaesarTypeNode getOrCreateType(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = getType(qualifiedName);
        
        if (res == null) {
            res = new CaesarTypeNode(this, qualifiedName);
            typeMap.put(qualifiedName, res);
        }
        
        return res;
    }
    
    public CaesarTypeNode getType(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = typeMap.get(qualifiedName);
        return res;
    }
    
    public JoinedTypeNode getJoinedNode(JavaQualifiedName qualifiedName) {
        return joinedGraph.getNodeByName(qualifiedName);
    }
    
    public List<CaesarTypeNode> topLevelTypes() {
    	return wrapInputNodeList(joinedGraph.getInputGraph().topLevelTypes());	
	}
      
    public Map getTypeMap() {
        return typeMap;
    }
    
    public List<CaesarTypeNode> wrapList(List<JoinedTypeNode> lst) {
		List<CaesarTypeNode> newLst = new ArrayList<CaesarTypeNode>(lst.size());
		for (JoinedTypeNode jn : lst) {
			newLst.add(wrapJoinedNode(jn));
		}
		return newLst;
	}
    
    public List<CaesarTypeNode> wrapInputNodeList(List<InputTypeNode> lst) {
		List<CaesarTypeNode> newLst = new ArrayList<CaesarTypeNode>(lst.size());
		for (InputTypeNode jn : lst) {
			newLst.add(wrapInputNode(jn));
		}
		return newLst;
	}
	
    public CaesarTypeNode wrapJoinedNode(JoinedTypeNode n) {
		if (n == null) 
			return null;
		else
			return getOrCreateType(n.getQualifiedName());
	}
    
    public CaesarTypeNode wrapInputNode(InputTypeNode n) {
		if (n == null) 
			return null;
		else
			return getOrCreateType(n.getQualifiedName());
	}
}
