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

import org.aspectj.weaver.IHasPosition;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;
import org.caesarj.compiler.constants.CaesarConstants;
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
	protected static IdentityHashMap map = new IdentityHashMap();
	
	/**
	 * Registers the relation between the type pattern and the pointcut.
	 * 
	 * @param type
	 * @param p
	 */
	public static void register(TypePattern type, Pointcut p) {
		map.put(type, p);
	}
	
	public static void resetRegister() {
		map = new IdentityHashMap();
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
		
		// Copies the name object
		String name = new String(typeName);
		
		// Check if the map has a pointcut for this location
		if (map.containsKey(location)) {
			
			// Gets the pointcut for the location
			Pointcut p = (Pointcut) map.get(location);
			
			// Kinded Pointcuts
			if (p instanceof CaesarKindedPointcut) {
		
				CaesarKindedPointcut kp = (CaesarKindedPointcut) p;

				// TODO - this is not working...
				if (Shadow.MethodCall.equals(kp.getKind())) {
					// I am not sure why, but we don't need to append Impl for
					// this case. Actually, it doesn't work if we do
					//name += CaesarConstants.IMPLEMENTATION_EXTENSION;
				}
				if (Shadow.MethodExecution.equals(kp.getKind())) {
					//name = convertToImplName(name);
				}
				if (Shadow.FieldGet.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				if (Shadow.FieldSet.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				if (Shadow.ConstructorCall.equals(kp.getKind())) {
					System.out.println("!!Warning!!Constructors must be translated");
				}
				if (Shadow.ConstructorExecution.equals(kp.getKind())) {
					System.out.println("!!Warning!!Constructors must be translated");
				}
				if (Shadow.PreInitialization.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				if (Shadow.StaticInitialization.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				if (Shadow.Initialization.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				// TODO - I don't think it is needed
				if (Shadow.ExceptionHandler.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
				// TODO - Still couldn't find a running example
				if (Shadow.AdviceExecution.equals(kp.getKind())) {
					name = convertToImplName(name);
				}
			}
			
			// Within Pointcuts
			if (p instanceof WithinPointcut) {
				name = convertToImplName(name);
			}
			
			// WithinCode Pointcuts
			// TODO - False. We have to test for constructors
			if (p instanceof WithincodePointcut) {
				WithincodePointcut kp = (WithincodePointcut) p;
				name = convertToImplName(name);
			}
			
			// Reference Pointcuts
			if (p instanceof ReferencePointcut) {
				ReferencePointcut kp = (ReferencePointcut) p;
				name = convertToImplName(name) + "$Registry";
			}
		}
		
		CClass cclass = lookupClass(name);
		
		// If name was not found and it is different from typeName
		// it means the class was a Java Class and not a CClass.
		// So, try with the Java Type Name
		if (cclass == null) {
			cclass = lookupClass(typeName);
		}

		// Resolve the class
		if (cclass == null) {
			return ResolvedTypeX.MISSING;
		} 
		else 
		{
			return world.resolve(cclass);
		}
	}
	
	/**
	 * Converts a class name to an implementation name.
	 * Examples:
	 *   ClassA -> ClassA_Impl
	 *   ClassA$ClassB -> ClassA_Impl$ClassB_Impl
	 *   
	 * @param name the class name
	 * @return the name of its implementation class
	 */
    public String convertToImplName(String name) {        
        return 
        	name.replaceAll("\\$", CaesarConstants.IMPLEMENTATION_EXTENSION + "\\$") +
        	CaesarConstants.IMPLEMENTATION_EXTENSION;
    }
}
