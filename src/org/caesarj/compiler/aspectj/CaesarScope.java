package org.caesarj.compiler.aspectj;

import java.io.File;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNode;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * Provides access to the ClassContext.
 * Important for pointcut checking.
 * 
 * @author Jürgen Hallpap
 */
public class CaesarScope implements IScope, CaesarConstants {

	private FjClassContext context;

	private CClass caller;

	private CaesarBcelWorld world;

	private IMessageHandler messageHandler;

	public CaesarScope(FjClassContext context, CClass caller) {
		super();

		this.world = CaesarBcelWorld.getInstance();
		this.context = context;
		this.caller = caller;

		messageHandler = world.getMessageHandler();
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

		//If the lookup retrieves a crosscutting class, then its
		//aspect registry should be returned instead.
		// Hence do a lookup for the registry.
		if ((cclass != null) 
			&& (cclass.isCrosscutting())) {
			cclass =
				lookupClass((typeName + REGISTRY_EXTENSION).intern());
		}

		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		} else {
			return world.resolve(cclass);
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
		return world.getWorld();
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

		if (location instanceof PatternNode) {
			PatternNode pattern = (PatternNode) location;
			ISourceContext sourceContext = pattern.getSourceContext();

			if (sourceContext != null) {
				return pattern.getSourceContext().makeSourceLocation(location);
			}
		}

		return new SourceLocation(
			new File(context.getCClass().getSourceFile()),
			0);
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
		return world.resolve(caller);
	}

}
