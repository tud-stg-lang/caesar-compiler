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
 * $Id: CaesarScope.java,v 1.17 2005-11-03 11:39:51 gasiunas Exp $
 */

package org.caesarj.compiler.aspectj;

import java.io.File;
import java.lang.ref.WeakReference;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.bridge.IMessage.Kind;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.IHasSourceLocation;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Provides access to the ClassContext.
 * Important for pointcut checking.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarScope implements IScope, CaesarConstants {
	
	protected FjClassContext context;

	protected CClass caller;

	protected WeakReference<CaesarBcelWorld> world;

	protected IMessageHandler messageHandler;
	
	protected TokenReference where;

	public CaesarScope(FjClassContext context, CClass caller, TokenReference where) {
		super();

		this.where = where;
		this.world = new WeakReference<CaesarBcelWorld>(CaesarBcelWorld.getInstance());
		this.context = context;
		this.caller = caller;

		messageHandler = world.get().getMessageHandler();		
	}

	/**
	 * Performs a lookup for the given typeName.
	 * 
	 * @param typeName
	 * @param location
	 * 
	 * @return TypeX
	 */
	public TypeX lookupType(String typeName, IHasPosition location) {
		
		if (context.getTypeFactory().isPrimitive(typeName)) return TypeX.forName(typeName); 

		CClass cclass = lookupClass(typeName);

		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		}
		else {
			return world.get().resolve(cclass);
		}
	}

	/**
	 * Performs a lookup for the given typeName.
	 */
	protected CClass lookupClass(String typeName) {
		
		//convert qualified names to internal representation
		typeName = typeName.replace('.', '/');

		String outerClassName = typeName;
		String innerClassName = "";

		//first do a lookup for the outer class
		CClass outerClass = null;
		int index = 0;
		while (outerClass == null && index >= 0) {
			try {
				outerClass = context.lookupClass(caller, outerClassName);
			} catch (UnpositionedError e) {
			}

			index = outerClassName.lastIndexOf("/");
			if (outerClass == null && index >= 0) {
				innerClassName = outerClassName.substring(index);
				outerClassName = outerClassName.substring(0, index);
			}
		}

		//if there is no inner class, return the outer class
		if (innerClassName.length() == 0) {
			return outerClass;
		}

		//lookup the inner class
		CClass res = null;
		try {
			res =
				context.lookupClass(
					caller,
					(outerClassName + innerClassName.replace('/', '$'))
						.intern());
		} catch (UnpositionedError e) {
		}

		return res;

	}

	/**
	 * Returns the world.
	 * 
	 * @return World
	 */
	public World getWorld() {
		return world.get().getWorld();
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#lookupFormal(String)
	 */
	public FormalBinding lookupFormal(String name) {
		FormalBinding[] bindings = 
			CaesarFormalBinding.wrappees(context.getBindings());

		for (int i = 0, len = bindings.length; i < len; i++) {
			if (bindings[i].getName().equals(name))
				return bindings[i];
		}
		return null;
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#getFormal(int)
	 */
	public FormalBinding getFormal(int i) {
		return context.getBindings()[i].wrappee();
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#getFormalCount()
	 */
	public int getFormalCount() {
		return context.getBindings().length;
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#getImportedPrefixes()
	 */
	public String[] getImportedPrefixes() {
		FjClassContext classContext = (FjClassContext) context;
		JPackageImport[] imports =
			classContext
				.getParentCompilationUnitContext()
				.getCunit()
				.getImportedPackages();
		String[] importedPrefixes = new String[imports.length];

		for (int i = 0; i < imports.length; i++) {
			importedPrefixes[i] = imports[i].getName();
		}

		return importedPrefixes;
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#getImportedNames()
	 */
	public String[] getImportedNames() {
		FjClassContext classContext = (FjClassContext) context;
		JClassImport[] imports =
			classContext
				.getParentCompilationUnitContext()
				.getCunit()
				.getImportedClasses();
		String[] importedNames = new String[imports.length];

		for (int i = 0; i < imports.length; i++) {
			importedNames[i] = imports[i].getQualifiedName();
		}

		return importedNames;
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#message(Kind, IHasPosition, String)
	 */
	public void message(
		IMessage.Kind kind,
		IHasPosition location,
		String message) {

		getMessageHandler().handleMessage(
			new Message(message, kind, null, makeSourceLocation(location)));
	}

	/**
	 * @see org.aspectj.weaver.patterns.IScope#message(Kind, IHasPosition, IHasPosition, String)
	 */
	public void message(
		IMessage.Kind kind,
		IHasPosition location1,
		IHasPosition location2,
		String message) {

		message(kind, location1, message);
		message(kind, location2, message);

	}

	/**
	 * Creates the ISourceLocation for the given location.
	 */
	protected ISourceLocation makeSourceLocation(IHasPosition location) {

		if (location instanceof IHasSourceLocation) {
			IHasSourceLocation pattern = (IHasSourceLocation) location;
			ISourceContext sourceContext = pattern.getSourceContext();

			if (sourceContext != null) {
				return pattern.getSourceContext().makeSourceLocation(location);
			}
		}
		
		if (where != null) {
			return new SourceLocation(
				where.getPath(),
				where.getLine());
		}
		else {
			return new SourceLocation(
				new File(context.getCClass().getSourceFile()),
				location.getStart());
		}
	}

	/**
	 * Returns the messageHandler.
	 */
	public IMessageHandler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * 
	 */
	public ResolvedTypeX getEnclosingType() {
		return world.get().resolve(caller);
	}

}
