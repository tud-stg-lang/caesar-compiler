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
 */
package org.caesarj.classfile;

import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;

/**
 * Temporary store for local variable information.
 * @author meffert
 */
public class JLocalVariableEntry {

	JLocalVariable var;
	LocalVariableScope scope;
	Instruction insn;
	
	public JLocalVariableEntry(Instruction insn, JLocalVariable var, LocalVariableScope scope){
		this.var = var;
		this.insn = insn;
		this.scope = scope;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		if(obj instanceof JLocalVariableEntry){
			return this.getVar().equals(((JLocalVariableEntry)obj).getVar());
		}
		return false;
	}
	
	/**
	 * Generates the local variable info for this entry.
	 * @return the info for this local variable
	 */
	public LocalVariableInfo getInfo(){
		LocalVariableInfo info;
		if(this.insn == null){
			this.insn = this.scope.getStart(); // for method parameters
		}
		if(scope.isClosed()){ 
			info = new LocalVariableInfo(
					this.insn,
					this.scope.getEnd(),
					this.getVar().getIdent(), 
					this.getVar().getType().getSignature(),
					new Integer(this.getVar().getPosition()).shortValue());
		} else {
			// Throw Exception ??? 
			info = new LocalVariableInfo(
					this.insn,
					this.insn,
					this.getVar().getIdent(), 
					this.getVar().getType().getSignature(),
					new Integer(this.getVar().getPosition()).shortValue());
		}
		return info;
	}
	
	/**
	 * @return Returns the var.
	 */
	public JLocalVariable getVar() {
		return var;
	}
	
	/**
	 * @param var The var to set.
	 */
	protected void setVar(JLocalVariable var) {
		this.var = var;
	}
	
	/**
	 * @return Returns the insn.
	 */
	public Instruction getInsn() {
		return insn;
	}
	
	/**
	 * @param insn The insn to set.
	 */
	protected void setInsn(Instruction insn) {
		this.insn = insn;
	}
}

