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
 * $Id: CaesarDeclare.java,v 1.4 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;

import org.aspectj.weaver.patterns.Declare;

/**
 * Wrapper for AspectJ-Declares
 * 
 * @author Karl Klose 
 */
public class CaesarDeclare {
    // attributes
    private Declare declare;

    // construction
    public CaesarDeclare(Declare declare) {
        this.declare = declare;
    }

    // interface
    public void resolve(CaesarScope scope) {
        declare.resolve(scope);
    }

    public Declare wrappee() {
        return declare;
    }

    /**
     * Returns an array containing the wrapped objects
     * 
     * @param declares
     *            An array of CaesarDeclares
     * @return An array of the wrappees
     */
    public static Declare[] wrappees(CaesarDeclare[] declares) {
        Declare[] ret = new Declare[declares.length];
        for (int i = 0; i < declares.length; i++)
            ret[i] = declares[i].wrappee();
        return ret;
    }
}