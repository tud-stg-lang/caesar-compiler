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
 * $Id: CaesarProgramElementNode.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.asm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;

/**
 * @author meffert
 *
 * This class represents the different nodes in the structure model. Offering
 * new caesar specific node kinds for e.g. virtual classes.
 * Note that the structure model does not exclusivly use CaesarProgramElementNodes,
 * but also uses StructureNodes (from aspectj), added by an asmadapter for relations
 * and links between (? TODO [question]: or only as Node.getRelations() ?).
 */
public class CaesarProgramElementNode extends ProgramElementNode {

	/**
	 * List of CaesarProgramElementNodes representing the formal parameters.
	 */
	protected List parameters;
	
	/**
	 * Represents the type for nodes of kind PARAMETER.
	 */
	protected String type;
	
	/**
	 * Represents the return type for nodes of kind METHOD
	 */
	protected String returnType;
	
	private List modifiers = new ArrayList();
	private Kind kind;
	
	public CaesarProgramElementNode() {
		super();
	}
	
    public CaesarProgramElementNode(
    	String signature, 
    	Kind kind, 
    	int modifiers, 
        ISourceLocation sourceLocation,
        List children,
		List parameters,
		String returnType,
		String type) 
    {
    	super(
    			signature,
				convertKind(kind),
				sourceLocation,
				modifiers,
				"", // formalComment ??
				children);
    	this.parameters = parameters;
    	this.returnType = returnType;
    	this.type = type;
    	this.modifiers = genModifiers(modifiers);
    	this.kind = kind;
    }	

    public List getParameters() {
		return parameters;
	}
	
	public String getType() {
		return type;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public List getModifiers() {
        return modifiers;
    }
	
	public String toString() {
		return "[" + getKind() + "] " + getName();
	}
	
	
	// copied from ProgramElementNode and edited to add Caesar specific Kinds like VirtualClass
	
	/**
	 * Uses "typesafe enum" pattern.
	 */
	public static class Kind implements Serializable {
		
		public static final Kind PROJECT = new Kind("project");
		public static final Kind PACKAGE = new Kind("package");
		public static final Kind FILE = new Kind("file");
		public static final Kind FILE_JAVA = new Kind("java source file");
		public static final Kind FILE_ASPECTJ = new Kind("aspect source file");
		public static final Kind FILE_LST = new Kind("build configuration file");
		public static final Kind CLASS = new Kind("class");
		public static final Kind INTERFACE = new Kind("interface");
		public static final Kind ASPECT = new Kind("aspect");
		public static final Kind INITIALIZER = new Kind("initializer");
		public static final Kind INTRODUCTION = new Kind("introduction");
		public static final Kind CONSTRUCTOR = new Kind("constructor");
		public static final Kind METHOD = new Kind("method");
		public static final Kind FIELD = new Kind("field");  
		public static final Kind POINTCUT = new Kind("pointcut");
		public static final Kind ADVICE = new Kind("advice");
		public static final Kind DECLARE_PARENTS = new Kind("declare parents");
		public static final Kind DECLARE_WARNING = new Kind("declare warning");
		public static final Kind DECLARE_ERROR = new Kind("declare error");
		public static final Kind DECLARE_SOFT = new Kind("declare soft");
		public static final Kind CODE = new Kind("decBodyElement");
		public static final Kind ERROR = new Kind("error");
		// Caesar-specific kinds
		public static final Kind IMPORTS = new Kind("import declarations");
		public static final Kind CLASS_IMPORT = new Kind("class import");
		public static final Kind PACKAGE_IMPORT = new Kind("package import");
		public static final Kind VIRTUAL_CLASS = new Kind("virtual class");
		public static final Kind PARAMETER = new Kind("parameter");
		public static final Kind ADVICE_REGISTRY = new Kind("advice registry");

		public static final Kind[] ALL = { PROJECT, PACKAGE, FILE, FILE_JAVA, 
			FILE_ASPECTJ, FILE_LST, CLASS, INTERFACE, ASPECT,
			INITIALIZER, INTRODUCTION, CONSTRUCTOR, METHOD, FIELD, POINTCUT, ADVICE, 
			DECLARE_PARENTS, DECLARE_WARNING, DECLARE_ERROR, DECLARE_SOFT, CODE, ERROR,
			IMPORTS, CLASS_IMPORT, PACKAGE_IMPORT, VIRTUAL_CLASS, PARAMETER, ADVICE_REGISTRY };
		
		public static Kind getKindForString(String kindString) {
			for (int i = 0; i < ALL.length; i++) {
				if (ALL[i].toString().equals(kindString)) return ALL[i];	
			}
			return ERROR;
		}
		
		private final String name;
		
		private Kind(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}	
		
		public static List getNonAJMemberKinds() {
			List list = new ArrayList();
			list.add(METHOD);
			list.add(FIELD);
			list.add(CONSTRUCTOR);
			return list;
		}
		
		public boolean isMemberKind() {
			return this == FIELD
				|| this == METHOD
				|| this == CONSTRUCTOR
				|| this == POINTCUT
				|| this == ADVICE;
		}
		
		public boolean isTypeKind() {
			return this == CLASS
				|| this == INTERFACE
				|| this == ASPECT;	
		}

		public boolean isSourceFileKind() {
			return this == FILE_ASPECTJ
				|| this == FILE_JAVA;
		}
		
		public boolean isDeclareKind() {
			return name.startsWith("declare");	
		} 
		
		public boolean isCaesarKind() {
			return this == IMPORTS
				|| this == CLASS_IMPORT
				|| this == PACKAGE_IMPORT
				|| this == VIRTUAL_CLASS
				|| this == PARAMETER
				|| this == ADVICE_REGISTRY;
		}

		// The 4 declarations below are necessary for serialization
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private Object readResolve() throws ObjectStreamException {
			return ALL[ordinal];
		}
	}
	
	// introduced to convert CaesarProgramElement.Kind into ProgramElementNode.Kind
	public static ProgramElementNode.Kind convertKind(CaesarProgramElementNode.Kind kind){
		// TODO: what should happen with Caesar-Kinds? 
		// Now ERROR will b e returned! Except for VIRTUAL_CLASS, 
		// the return value there is CLASS.
		if(kind == CaesarProgramElementNode.Kind.VIRTUAL_CLASS){
			return ProgramElementNode.Kind.CLASS;
		}
		return ProgramElementNode.Kind.getKindForString(kind.toString());
	}
	
	public Kind getCaesarKind(){
		return kind;
	}
	
	// copied from ProgramElementNode to correct the behavior of genModifiers(int).
	
	//XXX these names and values are from org.eclipse.jdt.internal.compiler.env.IConstants
	private static int AccPublic = 0x0001;
	private static int AccPrivate = 0x0002;
	private static int AccProtected = 0x0004;
	private static int AccPrivileged = 0x0006;  // XXX is this right?
	private static int AccStatic = 0x0008;
	private static int AccFinal = 0x0010;
	private static int AccSynchronized = 0x0020;
	private static int AccVolatile = 0x0040;
	private static int AccTransient = 0x0080;
	private static int AccNative = 0x0100;
	private static int AccInterface = 0x0200;
	private static int AccAbstract = 0x0400;
	private static int AccStrictfp = 0x0800;
	
	public static List genModifiers(int modifiers) {
		//System.out.println("genModifiers called in CaesarProgramElementNode");
		List modifiersList = new ArrayList();
		if ((modifiers & AccStatic) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.STATIC);
		if ((modifiers & AccFinal) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.FINAL);
		if ((modifiers & AccSynchronized) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.SYNCHRONIZED);
		if ((modifiers & AccVolatile) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.VOLATILE);
		if ((modifiers & AccTransient) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.TRANSIENT);
		if ((modifiers & AccNative) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.NATIVE);
		if ((modifiers & AccAbstract) != 0) modifiersList.add(CaesarProgramElementNode.Modifiers.ABSTRACT);
		return modifiersList;		
	}
}
