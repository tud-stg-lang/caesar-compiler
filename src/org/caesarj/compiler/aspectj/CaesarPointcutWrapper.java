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

import java.util.Hashtable;

import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
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

	// Information types
	public static final String INFO_TYPE_PATTERN = "typePattern";
	public static final String INFO_DECLARING_TYPE = "declaringType";
	public static final String INFO_EXCEPTION_TYPE = "exceptionType";
	public static final String INFO_ON_TYPE_SYMBOLIC = "onTypeSymbolic";
	
	/**
	 * The wrapped pointcut
	 */
	protected Pointcut wrappee = null;
	
	/**
	 * Aditional information about the wrappee
	 */
	protected Hashtable info = null;
	
	/**
	 * Constructs a wrapper for this wrappee
	 * 
	 * @param wrappee
	 */
	public CaesarPointcutWrapper(Pointcut wrappee) {
		this.wrappee = wrappee;
	}
	
	/**
	 * Gets the wrappee pointcut
	 * 
	 * @return
	 */
	public Pointcut getWrappee() {
		return wrappee;
	}
	
	/**
	 * Adds an information to the info table
	 * 
	 * @param key the key for the info
	 * @param value the value for the info
	 */
	public void addInfo(String key, Object value) {
		if (key == null || value == null) {
			return;
		}
		if (info == null) {
			info = new Hashtable();
		}
		info.put(key, value);
	}
	
	/**
	 * Gets the value for the info indicated by this key
	 * 
	 * @param key the key for the info
	 * @return the info value or null if not found
	 */
	public Object getInfo(String key) {
		if (info == null) {
			info = new Hashtable();
		}
		if (! info.containsKey(key)) {
			return null;
		}
		return info.get(key);
	}
	
	/**
	 * Checks if the wrappee is a KindedPointcut
	 * 
	 * @return true if the wrapee is a KindedPointcut, false otherwise
	 */
	public boolean isKinded() {
		return wrappee != null && wrappee instanceof CaesarKindedPointcut;
	}
	
	/**
	 * Checks if the wrappee is a WithinPointcut
	 * 
	 * @return true if the wrapee is a WithinPointcut, false otherwise
	 */
	public boolean isWithin() {
		return wrappee != null && wrappee instanceof WithinPointcut;
	}
	
	/**
	 * Checks if the wrappee is a WithincodePointcut
	 * 
	 * @return true if the wrapee is a WithincodePointcut, false otherwise
	 */
	public boolean isWithincode() {
		return wrappee != null && wrappee instanceof WithincodePointcut;
	}
	
	/**
	 * Checks if the wrappee is a HandlerPointcut
	 * 
	 * @return true if the wrapee is a HandlerPointcut, false otherwise
	 */
	public boolean isHandler() {
		return wrappee != null && wrappee instanceof HandlerPointcut;
	}
	
	/**
	 * Checks if the wrappee is a ReferencePointcut
	 * 
	 * @return true if the wrapee is a ReferencePointcut, false otherwise
	 */
	public boolean isReference() {
		return wrappee != null && wrappee instanceof ReferencePointcut;
	}
	
	/**
	 * Checks if the wrappee is a ArgsPointcut
	 * 
	 * @return true if the wrapee is a ArgsPointcut, false otherwise
	 */
	public boolean isArgs() {
		return wrappee != null && wrappee instanceof ArgsPointcut;
	}
	
	/**
	 * Checks if the wrappee is a ThisOrTargetPointcut
	 * 
	 * @return true if the wrapee is a ThisOrTargetPointcut, false otherwise
	 */
	public boolean isThisOrTarget() {
		return wrappee != null && wrappee instanceof ThisOrTargetPointcut;
	}
	
	/**
	 * Checks if the wrappee is a CflowPointcut
	 * 
	 * @return true if the wrapee is a CflowPointcut, false otherwise
	 */
	public boolean isCflow() {
		return wrappee != null && wrappee instanceof CflowPointcut;
	}
}
