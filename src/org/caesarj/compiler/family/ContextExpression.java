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
 * $Id: ContextExpression.java,v 1.12 2005-02-14 16:29:11 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.UnpositionedError;

/**
 * ctx(k)
 * 
 * @author Ivica Aracic
 */
public class ContextExpression extends Path {

    private int k = 0;
    
    public ContextExpression(Path prefix, int k, CReferenceType type) {
        super(prefix, type);
        this.k = k;
        
        if(k < 0)
            throw new InconsistencyException();
    }
        
    public int getK() {
        return k;
    }
    
    public boolean containsJavaElements() {
        if(prefix == null)
            return false;
        else 
            return prefix.containsJavaElements();
    }
    
    public String toString() {
        return (prefix==null?"":prefix.toString()+".")+"ctx("+k+")";
    }
    
    public Path normalize() {
        return this.clonePath();
    }
    
    public Path normalize2() throws UnpositionedError {
        return _normalize(null, this);
    }
    
    protected Path _normalize(Path pred, Path tail) throws UnpositionedError {
        
        //Log.verbose("normalize: "+tail);
        
        if(prefix == null) {
            return tail;
        }
        else {
            if(k == 0) {
                if(pred == null) {
                    // this one covers the case where x.y.ctx(0)
                    return tail.prefix._normalize(null, tail.prefix);
                }
                else {
                    pred.prefix = prefix;
                	return prefix._normalize(pred, tail);
                }
            }
            else if(prefix instanceof ContextExpression) {
                this.k += ((ContextExpression)prefix).getK();
                this.prefix = prefix.prefix;
                if(prefix == null)
                    return tail;
                else 
                    return this._normalize(pred, tail);
            }
            else {
                Path typePath = prefix.getTypePath().clonePath();
                Path typePathHead = typePath.getHead();
                //Path typePathHeadPred = typePath.getHeadPred();
                
                k--;                                
                typePathHead.prefix = prefix.prefix;
                prefix = typePath;
                
                return this._normalize(pred, tail);
            }
        }
    }
    
    public Path clonePath() {
        return new ContextExpression(prefix==null ? null : prefix.clonePath(), k, type);
    }

    /**
     * this is necessary since e.g. unqualified new calls from a initializer block take 
     * logically place at same level like the declaration 
     */
    public void adaptK(int offset) {
        this.k += offset;
    }
}
