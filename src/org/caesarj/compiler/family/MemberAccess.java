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
 * $Id: MemberAccess.java,v 1.1 2005-02-07 18:23:54 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public abstract class MemberAccess extends Path {
    
    protected String name;
    
    public MemberAccess(Path prefix, String field, CReferenceType type) {
        super(prefix, type);
        name = field;
    }   
    
    public String getName() {
        return name;
    }
    
    private Path getReceiver() {
        return null;
    }
    
    public String toString() {
        return prefix+"."+name;
    }

    public Path normalize() throws UnpositionedError {
        Path typePath = type.getPath().clonePath();
        Path typePathHeadPred = typePath.getHeadPred();
        Path typePathHead = typePath.getHead();
        typePathHead.prefix = prefix.clonePath();
        
        return typePathHead._normalize(typePathHeadPred, typePath);
    }
    
    public Path normalize2() throws UnpositionedError {
        return _normalize(null, this);
    }

    protected Path _normalize(Path pred, Path tail) throws UnpositionedError {
        return prefix._normalize(this, tail);
    }       
}
