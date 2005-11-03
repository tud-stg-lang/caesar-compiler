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
 * $Id: CaesarBcelWorld.java,v 1.12 2005-11-03 11:39:26 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import java.lang.ref.WeakReference;

import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelWorld;
import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;

/**
 * CaesarBcelWorld.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarBcelWorld /*extends BcelWorld */{
	
	private static final String REGISTRY_SUFFIX = "_Impl$Registry";

	/* the wrapped instance */
	private BcelWorldAdapter	theWorld;

	/* this is the singleton instance */
	private static CaesarBcelWorld theInstance;

	private static String lastClassPath;
	
	/* returns the singleton instance */
	public static CaesarBcelWorld getInstance() {
		return theInstance;
	}
	
	/* resets the singleton instance */
	public static CaesarBcelWorld createInstance(String classPath) {
		theInstance = new CaesarBcelWorld(classPath);
		lastClassPath = classPath;
		return theInstance;
	}

	/* resets the singleton instance */
	public static CaesarBcelWorld createInstance() {
		theInstance = new CaesarBcelWorld(lastClassPath);
		return theInstance;
	}
	
	/* returns the BcelWorld object */
	public BcelWorld getWorld(){
		return theWorld;
	}


	/**
	 * Constructor for CaesarBcelWorld.
	 */
	private CaesarBcelWorld(String classPath) {
		//super();
		theWorld = new BcelWorldAdapter(classPath);
	}

	public ResolvedTypeX resolve(CClass cclass){
		return theWorld.resolve(cclass);
	}

	/**
	 *  BcelWorldAdapter allows access to invisible fields of BcelWorld 
	 */
	private class BcelWorldAdapter extends BcelWorld{
		
		WeakReference<ClassReader> classReader = null;
		
		public BcelWorldAdapter(String classPath) {
			super(classPath == null ? "" : classPath);
		}
		
		/**
		 * Resolves the given CClass.
		 * 
		 * @param cclass
		 */
		public ResolvedTypeX resolve(CClass cclass) {
			
			ResolvedTypeX resolvedType =
				(ResolvedTypeX) typeMap.get(
					cclass.getAbstractType().getSignature());
			if (resolvedType == null) {
				ResolvedTypeX.Name name =
					new ResolvedTypeX.Name(
						cclass.getAbstractType().getSignature(),
						theWorld);
	
				name.setDelegate(new CaesarSourceType(name, false, cclass));
	
				typeMap.put(cclass.getAbstractType().getSignature(), name);
	
	
				resolvedType = name;
			}
	
			return resolvedType;
		}
		

		/**
		 * Use declares to compare the precedence of two aspects
		 */
		public int compareByDominates(ResolvedTypeX aspect1, ResolvedTypeX aspect2) {
			/* hack: try to take the original class name */
			return super.compareByDominates(translateAspectType(aspect1), translateAspectType(aspect2));
		}
		
		/**
		 * Try to translate Registry class to the mixin name of the original aspect
		 */
		private ResolvedTypeX translateAspectType(ResolvedTypeX aspect) {
			String regName = aspect.getName();
			if (regName.endsWith(REGISTRY_SUFFIX)) {
				String ifcName = regName.substring(0, regName.length() - REGISTRY_SUFFIX.length());
				ResolvedTypeX ifcType = resolve(TypeX.forName(ifcName), true);
				if (ifcType != ResolvedTypeX.MISSING) {
					return ifcType;
				}
			}
			return aspect;			
		}
		
		public void setClassReader(ClassReader reader) {
			classReader = new WeakReference<ClassReader>(reader);
		}
		
		/**
		 * Override source context for added class files.
		 * We need this to override the handle generation for advices.
		 */
		public BcelObjectType addSourceObjectType(JavaClass jc) {
			BcelObjectType objectType = super.addSourceObjectType(jc);
			String qualifiedName = jc.getClassName().replace('.', '/');
			CCjSourceClass sourceClass = classReader.get().findSourceClass(qualifiedName);
			if (sourceClass != null) {
				String fileName = sourceClass.getSourceFile();
				objectType.getResolvedTypeX().setSourceContext(new CaesarBcelSourceContext(objectType, fileName));
			}			
			return objectType;
		}
	}
	
	public void setClassReader(ClassReader reader) {
		theWorld.setClassReader(reader);
	}

	public void setXnoInline(boolean b) {
		theWorld.setXnoInline(b);
	}

	/**
	 * @param messageHandler
	 */
	public void setMessageHandler(CaesarMessageHandler messageHandler) {
		theWorld.setMessageHandler(messageHandler);
	}

	public IMessageHandler getMessageHandler() {
		return theWorld.getMessageHandler();
	};
}
