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
 * $Id: FieldAccess.java,v 1.10 2005-02-04 19:08:54 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FieldAccess extends Path {

    int paramPos;
    private String name;
    
    public FieldAccess(Path prefix, String field, CReferenceType type) {
        this(prefix, field, type, -1);
    }

    public FieldAccess(Path prefix, String field, CReferenceType type, int paramPos) {
        super(prefix, type);
        this.name = field;
        this.paramPos = paramPos;
    }

    public int getParamPos() {
        return paramPos;
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

    protected Path _normalize(Path pred, Path tail) throws UnpositionedError {
        return prefix._normalize(this, tail);
    }
    
    protected Path clonePath() {
        return new FieldAccess(prefix==null ? null : prefix.clonePath(), name, type);
    }
}
