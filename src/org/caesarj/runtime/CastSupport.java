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
 * $Id: CastSupport.java,v 1.2 2005-02-16 13:27:54 aracic Exp $
 */

package org.caesarj.runtime;

/**
 * Support class for casting dependent types
 * 
 * @author Ivica Aracic
 */
public class CastSupport {

    /**
     * F fam1 = ..., fam2 = fam1;
     * fam1.X x1 = ...; 
     * fam2.X x2 = (fam2.X)x1;
     * 
     * The second line is mapped to:
     * fam2.X x2 = (F.X)CastSupport.cast(fam2, x1);
     * 
     * If the runtime family of x1 is not fam2, ClassCastException is thrown
     */
    public static Object cast(Object fam, Object o2) {
		if(fam != null && o2 != null) {
			if(fam instanceof CaesarObject) {
				if( ((CaesarObject)o2).family() != fam ) {
					throw new ClassCastException("illegal family");
				}
			}
			else if(!(fam instanceof Object)) {
			    throw new ClassCastException("illegal family");
			}
		}
		return o2;
	}	

}
