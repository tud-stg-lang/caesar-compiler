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
 * $Id: CaesarObject.java,v 1.7 2005-03-31 10:43:20 gasiunas Exp $
 */

package org.caesarj.runtime;

import org.caesarj.runtime.aspects.AspectRegistryIfc;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CaesarObject implements CaesarObjectIfc {
	
    protected final Object $outer;
    
    public CaesarObject(Object outer) {
        $outer = outer;
	}
    
    public Object outer() {
        return $outer;
    }
    
    public Object family() {
        return $outer;
    }
    
    public boolean familyEquals(CaesarObject other) {
        return this.$outer == other.outer();
    }
    
	public AspectRegistryIfc $getAspectRegistry() {
		return null;
	}
	
	// ... more to come ...	
}
