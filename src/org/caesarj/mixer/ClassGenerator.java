package org.caesarj.mixer;

import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.ByteCodeMap;
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
		CaesarTypeSystem	typeSystem
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
            typeSystem
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
			CaesarTypeSystem typeSystem ) throws MixerException {
		_modifier.modify(originalClass,newClass,newSuperclass,newOuterClass, typeSystem);
	}
	
}
