package org.caesarj.mixer;

import java.io.IOException;
import java.util.HashMap;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.util.ClassLoader;
import org.caesarj.mixer.intern.ClassModifyingVisitor;

public class Mixer {
    
    private static Mixer singleton = new Mixer();
   
    public static Mixer instance() {
        return singleton;
    }

    private Mixer() {
    }
    
    String getSuperclassName( String clazzName ) throws MixerException{
    	// Perhaps it is one of those classes we've seen before
		if (superClasses.containsKey(clazzName)){
			return (String)superClasses.get(clazzName);
		}
		// if not, we need to load the class
    	String		s;
		JavaClass 	clazz = Repository.lookupClass(clazzName);
		if (clazz == null)
			throw new MixerException("Class not found: "+clazzName);
    	else s = clazz.getSuperclassName();
    	if (s==null)	s = "java/lang/Object";
    	else  s= s.replace('.','/');
    	superClasses.put(clazzName,s);
    	return s;
    }
    
    public String generateClass(MixinList mixinList) throws MixerException {
    	
    	/* DEBUG: Disable hashcodes for better readability */
    	MixinList.appendHashcode(false);
    	
   		if (mixinList.size() < 2)	throw new MixerException(
    							"Not enough mixins for this operation: "
    							+ mixinList);
    	
   		int last = mixinList.size()-1;
   		
    	String thisMixin="", superMixin;
		superMixin = mixinList.get(last).getFullQualifiedName();
    	// Check the superclass relation, beginning with the second last
    	for (int element = last-1; element>=0; element--){
    		thisMixin  = mixinList.get(element).getFullQualifiedName();

    		// ...read superclass of the current element...
    		String superClass = getSuperclassName(thisMixin);
    		
    		// ...and check if it is equal to the one that is demanded by the mixin list.
    		if (!superClass.equals(superMixin)){
    			// Create a new class for thisMixin with superclass 'superclass' and
    			// name mixinList[element,..,0].
    			String	newClassName = 	mixinList.get(element).getPackageName() +"/"+
										mixinList.generateClassName(element,last);
    			
    			replacedClasses.put( thisMixin, newClassName );
    			createModifiedClass(thisMixin, newClassName, superMixin);
				
    			// add the superclass of this class to the hashmap, so we don't have to load 
    			// the classfile for our generated classes later
    			superClasses.put(newClassName, superMixin);
    			thisMixin = newClassName; 
    		}
    		
     		superMixin = thisMixin;
    	}
   	    	
        return thisMixin;
    }

    /**
     * This method creates a new class <code>newClass</code> which extends <code>newSuperClass</code> by
     * copying and modifiyng the classfile of <code>originalClass</code>
     */
	private void createModifiedClass( String originalClass, String newClass, String newSuperclass ) throws MixerException{
		ClassModifyingVisitor	visitor = new ClassModifyingVisitor(
													originalClass,
													newClass,
													newSuperclass,
													replacedClasses );
	
		// load class
		JavaClass	clazz = Repository.lookupClass(originalClass);
		ClassGen generator = new ClassGen(clazz);
		// and modify it
		visitor.start(clazz);
		
		// find all inner classes and run recursively?
		 		
	}

	boolean mustReplaceSignature( String signature ){
		return replacedClasses.containsKey( ClassModifyingVisitor.typeFromSignature(signature));
	}
	
	String getNewSignature( String signature ){
		String type = ClassModifyingVisitor.typeFromSignature(signature);
		type = (String) replacedClasses.get(type);
		return replaceTypeInSignature(signature,type);
	}
	
    /** This hashmap holds the superclasses of all classes we've seen yet */
    HashMap	superClasses = new HashMap();
    
    /** This vector holds all classes that has been replaced: oldName -> newname */
    HashMap replacedClasses = new HashMap();

    boolean signatureReferences( String signature, String type){
    	return ClassModifyingVisitor.typeFromSignature(signature).equals(type);
    }
    
    String replaceTypeInSignature( String signature, String type ){
    	return ClassModifyingVisitor.replaceType(signature,type);
    }
    
}
