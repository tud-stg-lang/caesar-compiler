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
 * $Id: CjCastExpression.java,v 1.1 2005-02-15 18:28:59 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.expression;

import org.caesarj.compiler.family.Path;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

/**
 * This class represents a caesar cast which adapts a type according to the current context
 */
public class CjCastExpression extends JCastExpression {

    public CjCastExpression(TokenReference where, JExpression expr, CType dest) {
        super(where, expr, dest);
    }   

    /**
     * the family is not affected by this cast -> delegate to the casted expression
     */
    public Path getFamily() {
        return expr.getFamily();
    }
    
    /**
     * the family is not affected by this cast -> delegate to the casted expression
     */
    public Path getThisAsFamily() {
        return expr.getThisAsFamily();
    }   
}