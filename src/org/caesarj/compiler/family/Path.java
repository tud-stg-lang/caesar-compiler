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
 * $Id: Path.java,v 1.25 2005-03-06 13:48:18 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;

/**
 * ...
 * 
 * @author Karl Klose
 */
public abstract class Path {       

    public static final Path NULL = new Null();
    
    private static final String ANONYMOUS_FIELD_ACCESS = "$".intern();
    
    protected Path prefix;
    protected CReferenceType type;
    
    protected boolean finalPath = false;
    
    public Path getPrefix() {
        return prefix;
    }
    
    public boolean isFinal() {
        if(finalPath) {
            if(prefix != null)
                return prefix.isFinal();
            else
                return true;
        }
        return false;
    }
    
    public void setPrefix(Path prefix) {
        this.prefix = prefix;
    }
    
    public Path(boolean finalPath, Path prefix, CReferenceType type) {
        this.finalPath = finalPath;
        this.prefix = prefix;
        this.type = type;
    }       
    
    public CReferenceType getType() {
        return type;
    }

    public Path getTypePath() throws UnpositionedError {
        Path res = type.getPath();        
        
        return res;
    }

    public Path substituteFirstAccess(Path path){
        Path p = clonePath();
        Path pred = p.getPredOf( p.getHeadPred() );        

        if(pred != null) {            
            pred.prefix = path;
            return p;
        }
        else {
            return path;
        }
    }
    
    /**
     * @return true if this path contains java elements
     */
    public boolean containsJavaElements() {
        if(getType().getCClass().isNested() && !getType().getCClass().isMixinInterface()) {
            return true;
        }   

        if(prefix != null)
            return prefix.containsJavaElements();
        else
            return false;
    }    
    
    public boolean isAssignableTo(Path other) {        
        return this==NULL || (isFinal() && other.isFinal() && other.toString().equals(this.toString()));
    }
    
    public Path getHead() {
        if(prefix != null)
            return prefix.getHead();
        else 
            return this;
    }
    
    public Path getHeadPred() {
        if(prefix == null) return null;
        if(prefix.prefix != null)
            return prefix.getHeadPred();
        else 
            return this;
    }

    public Path getPredOf(Path p){
        if (prefix == null) return null;
        if (prefix == p) return this;
        return prefix.getPredOf(p);
    }

    
    
    public abstract Path normalize() throws UnpositionedError;
    
    public abstract Path normalize2() throws UnpositionedError;
    
    protected abstract Path _normalize(Path pred, Path tail) throws UnpositionedError;
    
    public abstract Path clonePath();

    public Path append(Path other) {
        other.getHead().prefix = this;
        return other;
    }

    /**
     *
     */

    public String toString() {
        return super.toString();
    }
}
