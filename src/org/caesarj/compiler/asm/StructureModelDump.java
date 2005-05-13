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
 * $Id: StructureModelDump.java,v 1.9 2005-05-13 14:23:11 thiago Exp $
 */

package org.caesarj.compiler.asm;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aspectj.asm.HierarchyWalker;
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
public class StructureModelDump extends HierarchyWalker {

	protected PrintStream out = null;
    protected CaesarJAsmManager asmManager = null;
    protected int depth = 0;
    
	public StructureModelDump(PrintStream outArg, CaesarJAsmManager asmManager) {
		this.out = outArg;
		this.asmManager = asmManager;
		this.depth = 0;
	}

	public void print() {
	    this.process(asmManager.hierarchy.getRoot());
	    printRelationshipMap(asmManager);
	}
	
	/**
	 * Print the node and advance the depth
	 */
	public void preProcess(IProgramElement node) {
	    
	    // Print the ident
	    for (int i = 0; i < depth; i++) {
	        out.print("..");
	    }
	    depth++;
	    
		// Print the node
	    if (node instanceof LinkNode) {
			LinkNode linkNode = (LinkNode) node;
			this.out.print(" ->> "); 
			printNodeHeader(this.out, linkNode);
			this.out.println();
			
	    } else if(node instanceof CaesarProgramElement){
		    ProgramElement peNode = (CaesarProgramElement) node;
			printNodeHeader(this.out, node);
			this.out.print(" {" + peNode.getHandleIdentifier() + "}");
			this.out.println();
			
		} else if (node instanceof ProgramElement) {
			ProgramElement peNode = (ProgramElement) node;
			printNodeHeader(this.out, node);
			this.out.print(
				" '" + peNode.getBytecodeName() + "' '" + peNode.getBytecodeSignature() + peNode.getAccessibility()+"'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			this.out.println();

			/*
			List relations = peNode.getRelations();
			if (relations.size() > 0) {
				for (Iterator it = relations.iterator(); it.hasNext();) {
					print(indentArg + "++", (IProgramElement) it.next());
				}
			}*/
		} else {
			this.out.println();
		}   
	}

	/**
	 * Postprocess just decrement the depth
	 */
    public void postProcess(IProgramElement node) {
        depth--;
    }
    
    
	protected void printNodeHeader(PrintStream outArg, IProgramElement node) {
	    
		outArg.print("[" + node.getKind() + "] " + node.getName());
		
		ISourceLocation srcLoc = node.getSourceLocation();
		if (srcLoc != null) {
			outArg.print("(L " + srcLoc.getLine() + ") "); 
		}
	}
	
	protected void printNodeHeader(PrintStream outArg, LinkNode node) {
	    
	    if (node.type == LinkNode.LINK_NODE_RELATIONSHIP) {
	        outArg.print("[relationship] " + node.getRelationship().getName());
	    } else {
			outArg.print("[link] " + node.getTargetElement().getName());
			
			ISourceLocation srcLoc = node.getTargetElement().getSourceLocation();
			if (srcLoc != null) {
				outArg.print("(L " + srcLoc.getLine() + ") "); 
			}
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