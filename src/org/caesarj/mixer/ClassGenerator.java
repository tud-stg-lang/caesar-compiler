package org.caesarj.mixer;

import java.util.Stack;
import java.util.Vector;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.caesarj.compiler.cclass.CaesarTypeSystem;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.compiler.cclass.JavaTypeNode;
import org.caesarj.mixer.intern.ClassModifyingVisitor;

public class ClassGenerator {
	public static void setOutputDir( String dir ){
		ClassModifyingVisitor.setOutputDirectory( dir );
	}
	
    private static ClassGenerator singleton = new ClassGenerator();
   
    public static ClassGenerator instance() {
        return singleton;
    }

    private ClassGenerator() {
    }

    public void generateClass(
            JavaQualifiedName mixinQN,
            JavaQualifiedName newClassQN,
            JavaQualifiedName newSuperQN,
            JavaQualifiedName newOuterQN,
			CaesarTypeSystem	typeSystem
        ) throws MixerException {
           System.out.println("Mixing "+newClassQN);
           System.out.println("\tmixin: "+mixinQN);
           System.out.println("\tsuper: "+newSuperQN);
           System.out.println("\touter: "+newOuterQN);
        	    	   				
           createModifiedClass(
               mixinQN.toString(), 
               newClassQN.toString(), 
               newSuperQN == null? "" : newSuperQN.toString(), 
               newOuterQN == null? "" : newOuterQN.toString(),
               typeSystem
           );		
        }
   
     
   
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
		ClassModifyingVisitor.modify(originalClass,newClass,newSuperclass,newOuterClass, typeSystem);
	}
	
}
