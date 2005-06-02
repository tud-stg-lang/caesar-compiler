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
 * $Id: InfiniteContextExpression.java,v 1.1 2005-06-02 15:30:52 klose Exp $
 */
package org.caesarj.compiler.family;

import org.caesarj.util.InconsistencyException;
import org.caesarj.util.UnpositionedError;

/**
 * ctx(\infty). Error marker for cases, where the context could not be resolved.
 * Used for example if a plain java accessor method belongs to a type that is not
 * an outer class of the current context (see test typesys-java-plain.accessors002).
 * 
 * @author Karl Klose
 */
public class InfiniteContextExpression extends ContextExpression {
    
    public InfiniteContextExpression() {
        super(null, Integer.MAX_VALUE, null);
    }

    public ContextExpression cloneWithAdaptedK(int newK) {
        return this;
    }
    
    public void adaptK(int offset) {
    }
    
    protected Path _normalize(Path pred, Path tail) throws UnpositionedError {
        // TODO Add qualified error message 
        // TODO [Check] Is it always an error to normalize InfiniteContextExpressions?
        throw new InconsistencyException("Cannot normalize InfiniteContextExpression");
    }
    
    public boolean isAssignableTo(Path other) {
        return false;
    }
}
