package org.caesarj.mixer2;

import java.util.Stack;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.mixer2.intern.ClassModifyingVisitor;

public class ClassGenerator {
	public static String OBJECT_CLASS = "java/lang/Object";
	
	protected Stack	classStack = new Stack();
	
	public	String getContext(){
		if (classStack.size()==0)	return "";
		return (String)classStack.peek();
	}
	
	public static String	implementationName( String interfaceName ){
		String packageName, components[];
		String [] parts = interfaceName.replace('.','/').split("/");
		if (parts.length < 2){
			packageName = ""; 
			components = parts[0].split("\\$");
		}
		else{
			packageName = parts[0];
			components = parts[1].split("\\$");
		}
		
		String result = packageName.equals("")? "" : packageName + "/";
		for (int i=0;i<components.length-1;i++){
			result = result+components[i]+"_Impl$";
		}
		result = result + components[ components.length-1 ]+"_Impl";
		return result;
	} 
	
	static String removeImpl( String name ){
		return name.substring(0, name.length()-5);
	}
	
	public static String	interfaceName( String implementationName ){
		String packageName, components[];
		String [] parts = implementationName.replace('.','/').split("/");
		if (parts.length < 2){
			packageName = ""; 
			components = parts[0].split("\\$");
		}
		else{
			packageName = parts[0];
			components = parts[1].split("\\$");
		}
		
		String result = packageName.equals("")? "" : packageName + "/";
		for (int i=0;i<components.length-1;i++){
			result = result+removeImpl(components[i])+"$";
		}
		result = result + removeImpl( components[ components.length-1 ] );
		return result;
	}
	
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
        JavaQualifiedName newOuterQN
    ) throws MixerException {

       System.out.println("Mixing "+newClassQN);
       System.out.println("\tmixin: "+mixinQN);
       System.out.println("\tsuper: "+newSuperQN);
       System.out.println("\touter: "+newOuterQN);
    	    	   				
       createModifiedClass(
           mixinQN.toString(), 
           newClassQN.toString(), 
           newSuperQN == null? "" : newSuperQN.toString(), 
           newOuterQN == null? "" : newOuterQN.toString() 
       );		
    }


    
     
   
    /**
     * This method creates a new class <code>newClass</code> which extends <code>newSuperClass</code> by
     * copying and modifiyng the classfile of <code>originalClass</code>
     */
	protected void createModifiedClass( String originalClass, String newClass, String newSuperclass, String newOuterClass ) throws MixerException {
		ClassModifyingVisitor.modify(originalClass,newClass,newSuperclass,newOuterClass);
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
