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
 * $Id: MethodOptimizer.java,v 1.3 2005-05-12 10:38:34 meffert Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.CodeInfo;
import org.caesarj.classfile.HandlerInfo;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.LineNumberInfo;
import org.caesarj.classfile.MethodInfo;

/**
 * Optimize a method using the SSA framework.
 *
 * @author: Michael Fernandez
 */
public class MethodOptimizer {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    public MethodOptimizer(MethodInfo method, CodeInfo info) {
	cfg = new ControlFlowGraph(method, info);

	this.info = info;
    }

    CodeInfo info;

    public CodeInfo generateCode() {
	Instruction[] instructions = cfg.getInstructions();
	HandlerInfo[] handlers = cfg.getHandlerInfos(instructions);
	CodeInfo info  = new CodeInfo(instructions,
				      handlers,
				      new LineNumberInfo[0],
				      //CM: new LocalVariableInfo[0]);
				      this.info.getLocalVariables());
	return info;

    }
    // -------------------------------------------------------------------
    // STATIC METHOD TO RUN OPTIMIZATIONS ON A CLASS
    // -------------------------------------------------------------------
    public static CodeInfo optimize(MethodInfo method, CodeInfo info, SSAOptions options) {
	MethodOptimizer methOptim = new MethodOptimizer(method, info);
	return methOptim.generateCode();
    }
    public static CodeInfo optimize(MethodInfo method, SSAOptions options) {
	return optimize(method, method.getCodeInfo(), options);
    }


    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    //the control flow graph of the method.
    protected ControlFlowGraph cfg;
}