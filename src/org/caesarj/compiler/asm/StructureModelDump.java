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
 * $Id: StructureModelDump.java,v 1.3 2005-01-24 16:52:59 aracic Exp $
 */

package org.caesarj.compiler.asm;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class StructureModelDump {

	String indent = ""; //$NON-NLS-1$
	protected PrintStream out;

	public StructureModelDump(PrintStream outArg) {
		this.out = outArg;
	}

	public void print(String indentArg, StructureNode node) {
		this.out.print(indentArg);

		printNodeHeader(this.out, node);

		if(node instanceof CaesarProgramElementNode){
			this.out.print(node.toString());
			this.out.println();
		}else if (node instanceof ProgramElementNode) {
			ProgramElementNode peNode = (ProgramElementNode) node;
			this.out.print(
				" '" + peNode.getBytecodeName() + "' '" + peNode.getBytecodeSignature() + peNode.getAccessibility()+"'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			this.out.println();

			List relations = peNode.getRelations();
			if (relations.size() > 0) {
				for (Iterator it = relations.iterator(); it.hasNext();) {
					print(indentArg + "++", (StructureNode) it.next());
				}
			}
		} else if (node instanceof LinkNode) {
			LinkNode linkNode = (LinkNode) node;
			this.out.print(" ->> "); 
			printNodeHeader(this.out, linkNode.getProgramElementNode());
			this.out.println();
		} else {
			this.out.println();
		}

		for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
			print(indentArg + "..", (StructureNode) it.next()); 
		}
	}

	protected void printNodeHeader(PrintStream outArg, StructureNode node) {
		//out.print(node.getClass().getName());

		outArg.print("[" + node.getKind() + "] " + node.getName());

		ISourceLocation srcLoc = node.getSourceLocation();
		if (srcLoc != null) {
			outArg.print("(L " + srcLoc.getLine() + ") "); 
		}
	}

}
