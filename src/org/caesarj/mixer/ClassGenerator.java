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
//    		CaesarTypeSystem	typeSystem
    		String[]	outers
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
               outers
           );		
        }


    public void generateClass(
            JavaQualifiedName mixinQN,
            JavaQualifiedName newClassQN,
            JavaQualifiedName newSuperQN,
            JavaQualifiedName newOuterQN,
			CaesarTypeSystem	typeSystem
        ) throws MixerException {
        
        	// collect all outer classes for this mixin
        	Vector	outerClasses = new Vector();
        	
        	JavaTypeNode mixinType = typeSystem.getJavaGraph().getNode(newClassQN),
    					outerType = mixinType.getOuter();
        	while ( outerType != null){
           	  outerClasses.add( outerType.getQualifiedName().getIdent() );  		
    		  outerType = outerType.getOuter();
        	}
        	
        	String[] outers = (String[])outerClasses.toArray( new String[0]);
        	
           System.out.println("Mixing "+newClassQN);
           System.out.println("\tmixin: "+mixinQN);
           System.out.println("\tsuper: "+newSuperQN);
           System.out.println("\touter: "+newOuterQN);
        	    	   				
           createModifiedClass(
               mixinQN.toString(), 
               newClassQN.toString(), 
               newSuperQN == null? "" : newSuperQN.toString(), 
               newOuterQN == null? "" : newOuterQN.toString(),
               outers
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
			String [] outers ) throws MixerException {
		ClassModifyingVisitor.modify(originalClass,newClass,newSuperclass,newOuterClass, outers);
	}
	
    
/*
    boolean signatureReferences( String signature, String type){
    	return ClassModifyingVisitor.typeFromSignature(signature).equals(type);
    }
    
    String replaceTypeInSignature( String signature, String type ){
    	return ClassModifyingVisitor.replaceType(signature,type);
    }
  */  
}
