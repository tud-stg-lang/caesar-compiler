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
 * $Id: ClassGenerator.java,v 1.9 2005-11-15 16:52:23 klose Exp $
 */

package org.caesarj.mixer;

import org.caesarj.compiler.ByteCodeMap;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.mixer.intern.ClassModifier;

public class ClassGenerator {
	
	/*
	 * State variables
	 */
	private ClassModifier _modifier;
	
	/*
	 * Construction
	 */
    public ClassGenerator(String inputDir, String outputDir, ByteCodeMap byteCodeMap) 
    {
    	_modifier = new ClassModifier(inputDir, outputDir, byteCodeMap);
    }

    /*
     * Operations
     */
    public void generateClass(
	    JavaQualifiedName mixinQN,
	    JavaQualifiedName newClassQN,
	    JavaQualifiedName newSuperQN,
		JavaQualifiedName newOuterQN,
        KjcEnvironment  env
	) throws MixerException {    	
    	/*
    	System.out.println("Mixing "+newClassQN);
        System.out.println("\tmixin: "+mixinQN);
        System.out.println("\tsuper: "+newSuperQN);
        System.out.println("\touter: "+newOuterQN);
        */
        createModifiedClass(
        	mixinQN.toString(), 
            newClassQN.toString(), 
            newSuperQN == null? "" : newSuperQN.toString(), 
            newOuterQN == null? "" : newOuterQN.toString(),
            env
        );		
    }
    
    /*
     * Implementation
     */
   
    /**
     * This method creates a new class <code>newClass</code> which extends <code>newSuperClass</code> by
     * copying and modifiyng the classfile of <code>originalClass</code>
     */
	protected void createModifiedClass( 
			String originalClass, 
			String newClass, 
			String newSuperclass, 
			String newOuterClass,
            KjcEnvironment env) throws MixerException {
		_modifier.modify(originalClass,newClass,newSuperclass,newOuterClass, env);
	}
	
}
