/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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

package org.caesarj.compiler.aspectj;

import java.util.IdentityHashMap;

import org.aspectj.org.eclipse.jdt.core.dom.Modifier;
import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.ModifiersPattern;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.TokenReference;

/**
 * 
 * The scope used when resolving Pointcuts.
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class CaesarPointcutScope extends CaesarScope {
	
	/**
	 * A map between type patterns and pointcuts.
	 * 
	 * Note that it MUST be an Identity map, because two references
	 * for similar type patterns would be the same and the last
	 * pointcut would overwrite the others (for example, if ClsA appears
	 * in 2 pointcuts, the second would overwrite).
	 */
	protected static IdentityHashMap<TypePattern, CaesarPointcutWrapper> map = 
		new IdentityHashMap<TypePattern, CaesarPointcutWrapper>();
	
	/**
	 * Registers the relation between the type pattern and the pointcut.
	 * 
	 * @param type
	 * @param p
	 */
	public static void register(TypePattern type, CaesarPointcutWrapper p) {
		map.put(type, p);
	}
	
	/**
	 * Resets the registrer
	 */
	public static void resetRegister() {
		map = new IdentityHashMap<TypePattern, CaesarPointcutWrapper>();
	}
	
	/**
	 * Gets a wrapper from the register, using this type as key
	 * 
	 * @param type
	 * @return
	 */
	public static CaesarPointcutWrapper getRegistred(TypePattern type) {
		return map.get(type);
	}
	
	/**
	 * Checks if this type is key for the register
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isRegistred(TypePattern type) {
		return map.containsKey(type);
	}
	
    /**
     * 
     * @param context
     * @param caller
     */
	public CaesarPointcutScope(FjClassContext context, CClass caller, TokenReference where) {
		super(context, caller, where);
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
		
		// Primitive types are resolved directly
		if (context.getTypeFactory().isPrimitive(typeName)) 
			return TypeX.forName(typeName);
		
		// Resolve the type
		CClass cclass = lookupClass(typeName);

		// Just process if it is not a regular Java class or it was not found
		if (cclass != null && ! cclass.isClass()) {
		
			// Copies the name object
			String name = new String(typeName);
			
			// Check if the map has a pointcut for this location
			if (map.containsKey(location)) {
				
				// Gets the pointcut for the location
				CaesarPointcutWrapper wrapper = map.get(location);
				Pointcut p = wrapper.getWrappee();
				
				// Kinded Pointcuts
				if (p instanceof CaesarKindedPointcut) {
			
					CaesarKindedPointcut kp = (CaesarKindedPointcut) p;
					
					if (Shadow.MethodCall.equals(kp.getKind())) {
						if (hasStaticModifier(kp) || kp.wasConstructor()) {
							name = cclass.getImplQualifiedName();
						}
					}
					if (Shadow.MethodExecution.equals(kp.getKind())) {
						if (hasStaticModifier(kp)) {
							name = cclass.getImplQualifiedName();
						}
					}
					if (Shadow.FieldGet.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.FieldSet.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.ConstructorCall.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.ConstructorExecution.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.PreInitialization.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.StaticInitialization.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					if (Shadow.Initialization.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
					// TODO - Still couldn't find a running example
					if (Shadow.AdviceExecution.equals(kp.getKind())) {
						name = cclass.getImplQualifiedName();
					}
				}
				
				// Within Pointcuts
				if (p instanceof WithinPointcut) {
					name = cclass.getImplQualifiedName();
				}
				
				// WithinCode Pointcuts
				if (p instanceof WithincodePointcut) {
					name = cclass.getImplQualifiedName();
				}
				
				// Reference Pointcuts
				if (p instanceof ReferencePointcut) {
					ReferencePointcut kp = (ReferencePointcut) p;
					name = cclass.getImplQualifiedName()+ "$Registry";
				}
				
				if (p instanceof ThisOrTargetPointcut) {
					name = cclass.getImplQualifiedName();
				}
			}
		
			cclass = lookupClass(name);
		}

		// Resolve the class
		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		} 
		else 
		{
			return world.get().resolve(cclass);
		}
	}
    
	/**
	 * Translates names to weaver names, replacing all / for .
	 * 
	 * @param name
	 * @return
	 */
    protected static String convertToWeaverName(String name) {
    	return name.replaceAll("\\/", "\\.");
    }
    
    /**
     * Checks if the pointcut is static.
     * 
     * @param kp a pointcut to check
     * @return true if the pointcut is static, false otherwise
     */
    protected static boolean hasStaticModifier(CaesarKindedPointcut kp) {
    	ModifiersPattern modifiers = kp.signature.getModifiers();
		if (modifiers instanceof CaesarModifiersPattern) {
			CaesarModifiersPattern m = (CaesarModifiersPattern) modifiers;
			return m.hasRequired(Modifier.STATIC);
		}
		return false;
    }
    
    /**
     * Gets the list of imported packaged for this scope.
     * The returned value is an array of imports, which includes the imported
     * packages plus the empty package ("") and the package of this scope.
     * These 2 last are important because the weaver looks for classes in these
     * packages. The empty package allows for writing fully qualified names
     * (like com.acompany.Class) and the scope package allows for simple names
     * (like Class if we are in the scope of com.acompany).
     * 
	 * @see org.aspectj.weaver.patterns.IScope#getImportedPrefixes()
	 */
	public String[] getImportedPrefixes() {
		
		FjClassContext classContext = (FjClassContext) context;
		JPackageImport[] imports =
			classContext
				.getParentCompilationUnitContext()
				.getCunit()
				.getImportedPackages();
		String[] importedPrefixes = new String[imports.length + 2];

		// Adds the imported prefixes, converting to the weaver name
		for (int i = 0; i < imports.length; i++) {
			importedPrefixes[i] = convertToWeaverName(imports[i].getName()) + ".";
		}

		// Adds the package name, so that the weaver will look for matches 
		// in this package too
		importedPrefixes[importedPrefixes.length - 2] = 
			convertToWeaverName(
					classContext
						.getParentCompilationUnitContext()
						.getCunit()
						.getCompUnitDecl()
						.getPackageName().getName()) + ".";
		
		// Adds the empty prefix
		importedPrefixes[importedPrefixes.length - 1] = "";
		
		return importedPrefixes;
	}
}
