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
 * $Id: StructureModelDump.java,v 1.7 2005-04-16 09:51:51 thiago Exp $
 */

package org.caesarj.compiler.asm;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 */
public class StructureModelDump {

	String indent = ""; //$NON-NLS-1$
	protected PrintStream out;

	public StructureModelDump(PrintStream outArg) {
		this.out = outArg;
	}

	public void print(CaesarJAsmManager asmManager) {
	    print("", asmManager.getHierarchy().getRoot());
	    printRelationshipMap(asmManager);
	}
	
	protected void print(String indentArg, IProgramElement node) {
		this.out.print(indentArg);

		printNodeHeader(this.out, node);

		if(node instanceof CaesarProgramElement){
		    ProgramElement peNode = (CaesarProgramElement) node;
			this.out.print(" {" + peNode.getHandleIdentifier() + "}");
			this.out.println();
		} else if (node instanceof ProgramElement) {
			ProgramElement peNode = (ProgramElement) node;
			this.out.print(
				" '" + peNode.getBytecodeName() + "' '" + peNode.getBytecodeSignature() + peNode.getAccessibility()+"'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			this.out.println();

			List relations = peNode.getRelations();
			if (relations.size() > 0) {
				for (Iterator it = relations.iterator(); it.hasNext();) {
					print(indentArg + "++", (IProgramElement) it.next());
				}
			}
		/*} else if (node instanceof LinkNode) {
			LinkNode linkNode = (LinkNode) node;
			this.out.print(" ->> "); 
			printNodeHeader(this.out, linkNode.getProgramElementNode());
			this.out.println();*/
		} else {
			this.out.println();
		}

		for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
			print(indentArg + "..", (IProgramElement) it.next()); 
		}
	}

	protected void printNodeHeader(PrintStream outArg, IProgramElement node) {
		//out.print(node.getClass().getName());

		outArg.print("[" + node.getKind() + "] " + node.getName());

		ISourceLocation srcLoc = node.getSourceLocation();
		if (srcLoc != null) {
			outArg.print("(L " + srcLoc.getLine() + ") "); 
		}
	}
	
	protected void printRelationshipMap(CaesarJAsmManager asmManager) {
	    System.out.println("Dumping Relationship Map");
        IHierarchy hierarchy = asmManager.getHierarchy();
        IRelationshipMap map = asmManager.getRelationshipMap();
		Set entries = map.getEntries();
	    Iterator i = entries.iterator();
	    while(i.hasNext()) {
	        List relationships = map.get((String) i.next());
	        Iterator j = relationships.iterator();
	        while(j.hasNext()) {
	            IRelationship relationship = (IRelationship) j.next();
	            System.out.println("Relationship '" + relationship.getName() + "' of kind '" + relationship.getKind() + "' has " + relationship.getTargets().size() + " target(s) ");
	            System.out.println("   source handle -->" + relationship.getSourceHandle());
	            Iterator k = relationship.getTargets().iterator();
	            while(k.hasNext()) {
	                IProgramElement element = hierarchy.findElementForHandle((String) k.next());
	                System.out.println("  -> '" + element.getName() + "' of kind '" + element.getKind() + "' with handle " + element.getHandleIdentifier());
	            }
	        }
	    }
	}

}
