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
 * $Id: ExceptionHandler.java,v 1.2 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * To reprensent exception handler with basic blocks.
 */
public class ExceptionHandler {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Construct the exception handler with the different index in
     * the source code.
     *
     * @param start the index of the first instruction protected
     * @param end the index of the last instruction protected
     * @param handle the index of the instruction called to catch the
     *   exception.
     */
    public ExceptionHandler(int start, int end, int handle) {
	firstInst = start;
	lastInst = end;
	handlerInst = handle;
	protectedBlocks = new HashSet();
    }

    // -------------------------------------------------------------------
    // SEARCH NEW INDEXS
    // -------------------------------------------------------------------
    /**
     * Search the new index of the instructions after code generation.
     *
     * Precondition : The method generate should be called on the
     *   basic block before calling this method.
     *  (the method generate calculate the position of the instructions
     *   of the basic block after generation).
     */
    public void searchIndex() {
	handlerInst = blockHandler.getStartGen();
	//find the first and the last instructions protected
	int min = Integer.MAX_VALUE;
	int max = -1;
	Iterator it = protectedBlocks.iterator();
	while (it.hasNext()) {
	    BasicBlock bb = (BasicBlock) it.next();
	    int start = bb.getStartGen();
	    int end = bb.getStartGen() + bb.getNbGen() - 1;
	    if (end >= start) {
		if (start < min)
		    min = start;
		if (end > max)
		    max = end;
	    }
	}
	firstInst = min;
	lastInst = max;
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the index of the first instruction protected.
     *
     * If this method is called before calling searchIndex, the index
     * is in the source code, else, it is in the generated code.
     *
     * @return the index of the first instruction protected.
     */
    public int getStart() {
	return firstInst;
    }

    /**
     * Get the index of the last instruction protected.
     *
     * If this method is called before calling searchIndex, the index
     * is in the source code, else, it is in the generated code.
     *
     * @return the index of the last instruction protected.
     */
    public int getEnd() {
	return lastInst;
    }

    /**
     * Get the index of the instruction which begin the catch block
     *
     * If this method is called before calling searchIndex, the index
     * is in the source code, else, it is in the generated code.
     *
     * @return the index of the instruction which begin the catch block.
     */
    public int getHandle() {
	return handlerInst;
    }

    /**
     * Get the block which is called when the exception is catch
     *
     * @return the catch begin block.
     */
    public BasicBlock getHandlerBlock() {
	return blockHandler;
    }

    /**
     * Set the block which is called when the exception is catch
     *
     * @param b the catch begin block.
     */
    public void setHandlerBlock(BasicBlock b) {
	blockHandler = b;
    }

    /**
     * Add a block to protect
     *
     * @param b the block to add
     */
    public void addProtectedBlock(BasicBlock b) {
	protectedBlocks.add(b);
    }

    /**
     * Test if a block is protected
     *
     * @param block block to test
     * @return true iff the block is protected
     */
    public boolean contains(BasicBlock block) {
	return protectedBlocks.contains(block);
    }

    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected int firstInst;
    protected int lastInst;
    protected int handlerInst;
    protected BasicBlock blockHandler;
    protected Set protectedBlocks;
}
