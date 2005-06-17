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
 * $Id: CaesarTypeGraph.java,v 1.5 2005-06-17 11:11:49 gasiunas Exp $
 */

package org.caesarj.compiler.typesys.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caesarj.compiler.typesys.java.JavaQualifiedName;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarTypeGraph {
	/** root of inner and inheritance hierarchy */
    private Set topClassRoot    = new HashSet();
    private Set inheritanceRoot = new HashSet();
    private HashMap typeMap     = new HashMap();
    
    public CaesarTypeGraph() {
    }
    
    public boolean hasType(String qualifiedName) {
        return typeMap.containsKey(qualifiedName);
    }
    
    public CaesarTypeNode getTypeCreateIfNotExsistent(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = getType(qualifiedName);
        
        if (res == null) {
            res = new CaesarTypeNode(this, qualifiedName);
            typeMap.put(qualifiedName, res);
        }
        
        return res;
    }
    
    public CaesarTypeNode getType(JavaQualifiedName qualifiedName) {
        CaesarTypeNode res = (CaesarTypeNode)typeMap.get(qualifiedName);
        return res;
    }
    
    public Set getInheritanceRoot() {
        return inheritanceRoot;
    }

    public Set getTopClassRoot() {
        return topClassRoot;
    }
      
    public Map getTypeMap() {
        return typeMap;
    }    
}
