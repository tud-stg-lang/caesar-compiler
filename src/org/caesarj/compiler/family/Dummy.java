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
 * $Id: Dummy.java,v 1.1 2005-03-03 12:18:56 aracic Exp $
 */

package org.caesarj.compiler.family;

import org.caesarj.util.UnpositionedError;



/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class Dummy extends MemberAccess {

    public Dummy(Path prefix) {
        super(false, prefix, "*", null);
    }
    
    public Path clonePath() {
        return new Dummy(prefix==null ? null : prefix.clonePath());
    }
    
    public Path getTypePath() throws UnpositionedError {
        return new ContextExpression(null, 0, null);
    }
    
    public Path normalize() throws UnpositionedError {
        return new ContextExpression(prefix, 0, null).normalize();        
    }

    public Path normalize2() throws UnpositionedError {
        return new ContextExpression(prefix, 0, null).normalize2();        
    }
}
