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
 * $Id: MethodAccess.java,v 1.3 2005-02-11 18:45:22 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class MethodAccess extends MemberAccess {
    
    public MethodAccess(Path prefix, String name, CReferenceType type) {
        super(prefix, name, type);
    }
    
    public Path getTypePath() throws UnpositionedError {
        return new ContextExpression(null, 0, null);
    }       
    
    /**
     *
     */

    public Path normalize() throws UnpositionedError {
        Path typePath = getTypePath().clonePath();
        Path typePathHeadPred = typePath.getHeadPred();
        Path typePathHead = typePath.getHead();
        // keep the method name in the context
        typePathHead.prefix =  this.clonePath();
        
        return typePathHead._normalize(typePathHeadPred, typePath);
    }
    
    public Path clonePath() {
        return new MethodAccess(prefix==null ? null : prefix.clonePath(), name, type);
    }
}
