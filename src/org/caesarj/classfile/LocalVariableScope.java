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

/**
 * @author meffert
 */
public class LocalVariableScope {

	Instruction end = null;
	
	
	public LocalVariableScope(){
		
	}
	
	/**
	 * Closes this scope and sets the end instruction.
	 * @param end instruction
	 */
	public void close(Instruction end){
		this.setEnd(end);
	}
	
	/**
	 * @return if this scope is closed.
	 */
	public boolean isClosed(){
		return this.getEnd() != null;
	}
	
	/**
	 * @return Returns the end instruction.
	 */
	public Instruction getEnd() {
		return end;
	}
	/**
	 * @param end The end instruction to set.
	 */
	protected void setEnd(Instruction end) {
		this.end = end;
	}
}
