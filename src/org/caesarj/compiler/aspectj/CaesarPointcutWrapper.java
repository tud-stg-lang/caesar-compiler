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

import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

/**
 * Represents a Wrapper to an AspectJ Pointcut class.
 * It stores the original pointcut as a wrappee but also stores
 * other information that may be used to register the pointcut
 * when parsing.
 * 
 * @author Thiago Tonelli Bartolomei <thiagobart@gmail.com>
 *
 */
public class CaesarPointcutWrapper {

	/**
	 * The wrapped pointcut
	 */
	protected Pointcut wrappee = null;
	
	/**
	 * Is not nul if this pointcut represents the mixin of a type
	 */
	protected TypePattern originalType = null;
	
	protected TypePattern typePattern = null;
	protected TypePattern declaringType = null;
	protected TypePattern exceptionType = null;
	protected TypePattern onTypeSymbolic = null;
	
	/**
	 * Constructs a wrapper for this wrappee
	 * 
	 * @param wrappee
	 */
	public CaesarPointcutWrapper(Pointcut wrappee) {
		this.wrappee = wrappee;
	}
	
	/**
	 * Constructs a wrapper for this wrappee's mixins
	 * 
	 * @param wrappee
	 */
	public CaesarPointcutWrapper(Pointcut wrappee, TypePattern originalType) {
		this.wrappee = wrappee;
		this.originalType = originalType;
	}
	
	/**
	 * Checks if the wrappee is a KindedPointcut
	 * 
	 * @return true if the wrappee is a KindedPointcut, false otherwise
	 */
	public boolean isKinded() {
		return wrappee != null && wrappee instanceof CaesarKindedPointcut;
	}
	
	/**
	 * Checks if the wrappee is a WithinPointcut
	 * 
	 * @return true if the wrappee is a WithinPointcut, false otherwise
	 */
	public boolean isWithin() {
		return wrappee != null && wrappee instanceof WithinPointcut;
	}
	
	/**
	 * Checks if the wrappee is a WithincodePointcut
	 * 
	 * @return true if the wrappee is a WithincodePointcut, false otherwise
	 */
	public boolean isWithincode() {
		return wrappee != null && wrappee instanceof WithincodePointcut;
	}
	
	/**
	 * Checks if the wrappee is a HandlerPointcut
	 * 
	 * @return true if the wrappee is a HandlerPointcut, false otherwise
	 */
	public boolean isHandler() {
		return wrappee != null && wrappee instanceof HandlerPointcut;
	}
	
	/**
	 * Checks if the wrappee is a ReferencePointcut
	 * 
	 * @return true if the wrappee is a ReferencePointcut, false otherwise
	 */
	public boolean isReference() {
		return wrappee != null && wrappee instanceof ReferencePointcut;
	}
	
	/**
	 * Checks if the wrappee is a ArgsPointcut
	 * 
	 * @return true if the wrappee is a ArgsPointcut, false otherwise
	 */
	public boolean isArgs() {
		return wrappee != null && wrappee instanceof ArgsPointcut;
	}
	
	/**
	 * Checks if the wrappee is a ThisOrTargetPointcut
	 * 
	 * @return true if the wrappee is a ThisOrTargetPointcut, false otherwise
	 */
	public boolean isThisOrTarget() {
		return wrappee != null && wrappee instanceof ThisOrTargetPointcut;
	}
	
	/**
	 * Checks if the wrappee is a CflowPointcut
	 * 
	 * @return true if the wrappee is a CflowPointcut, false otherwise
	 */
	public boolean isCflow() {
		return wrappee != null && wrappee instanceof CflowPointcut;
	}
	
	/**
	 * Checks if the pointcut should represent the DeclaringType's Mixin
	 * 
	 * @return true, if the pointcut should represent a mixin, false otherwise
	 */
	public boolean isMixin() {
		return this.originalType != null;
	}
	
	/**
	 * Prints this pointcut's wrappee
	 */
	public String toString() {
		if (this.isMixin()) {
			return "Mixin: " + this.wrappee.toString();
		} else {
			return this.wrappee.toString();
		}
	}

	// Getters and setters
	
	/**
	 * Gets the wrappee pointcut
	 * 
	 * @return
	 */
	public Pointcut getWrappee() {
		return wrappee;
	}
	
	public TypePattern getDeclaringType() {
		return declaringType;
	}

	public void setDeclaringType(TypePattern declaringType) {
		this.declaringType = declaringType;
	}

	public TypePattern getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(TypePattern exceptionType) {
		this.exceptionType = exceptionType;
	}

	public TypePattern getOnTypeSymbolic() {
		return onTypeSymbolic;
	}

	public void setOnTypeSymbolic(TypePattern onTypeSymbolic) {
		this.onTypeSymbolic = onTypeSymbolic;
	}

	public TypePattern getTypePattern() {
		return typePattern;
	}

	public void setTypePattern(TypePattern typePattern) {
		this.typePattern = typePattern;
	}
	
	public void setWrappee(Pointcut wrappee) {
		this.wrappee = wrappee;
	}

	public TypePattern getOriginalType() {
		return originalType;
	}

	public void setOriginalType(TypePattern originalType) {
		this.originalType = originalType;
	}
}
