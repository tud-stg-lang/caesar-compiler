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
 * $Id: EdgeLabel.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.AbstractInstructionAccessor;

/**
 * In kopi instructions, jump instructions reference an
 * InstructionAccessor. This class is use to reference an edge
 * on the control flow graph not an instruction.
 *
 * By using an edge not a basic block, you can change the basic
 * block destination of the reference.
 *
 * @author Michael Fernandez
 */
public class EdgeLabel extends AbstractInstructionAccessor {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the label with the edge
     *
     * @param edge the edge
     */
    public EdgeLabel(Edge edge) {
	this.edge = edge;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * return the edge
     *
     * @return the edge
     */
    public Edge getEdge() {
	return edge;
    }
    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected Edge edge;
}
