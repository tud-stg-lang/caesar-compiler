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
