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
    
    public String generateClass(String destinationPackage, MixinList mixinList) throws MixerException {
    	// Make sure that the destinationpackage uses '/' as delimiter and ends with a '/'.
    	if (!destinationPackage.equals("")){
    		destinationPackage.replace('.','/');
    		if (!destinationPackage.endsWith("/"))	destinationPackage += "/";
    	}
    	
    	int last = mixinList.size()-1;
    	while ( mixinList.get(last).getFullQualifiedName().equals("java/lang/Object")){
    		last--;
    		if (last < 1)	throw new MixerException(
    							"Not enough mixins for this operation (need >2 != Object): "
    							+ mixinList);
    	}
    	
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
    			
    			createModifiedClass(thisMixin, newClassName, superMixin);

				
    			// add the superclass of this class to the hashmap, so we don't have to load 
    			// the classfile for our generated classes
    			superClasses.put(newClassName, superMixin);
    			thisMixin = newClassName; 
    		}
    		
    		System.out.println( thisMixin + " extends "+superMixin);

    		superMixin = thisMixin;
    	}
    	
     	// construct the name of the resulting class 
    	String resultingClassName = thisMixin;//destinationPackage + mixinList.generateClassName();
   	    	
        return resultingClassName;
    }

    /**
     * This method creates a new class <code>newClass</code> which extends <code>newSuperClass</code> by
     * copying and modifiyng the classfile of <code>originalClass</code>
     */
	private void createModifiedClass( String originalClass, String newClass, String newSuperclass ) throws MixerException{
		// load class
		JavaClass	clazz = Repository.lookupClass(originalClass);
		ClassGen generator = new ClassGen(clazz);
		
		// find the CP entry for the old class and superclass symbols and modify it
		ConstantPoolGen cp = generator.getConstantPool();
		/*DEBUG*/System.out.println(originalClass+" -> "+newClass+" : "+newSuperclass);
		/*DEBUG*/System.out.println("Constant pool of class "+newClass+":\n"+cp);

		int classNameIndex = generator.getClassNameIndex(),
			superclassNameIndex = generator.getSuperclassNameIndex();
		
		ConstantClass 	cc = (ConstantClass)cp.getConstant(classNameIndex),
						csc = (ConstantClass)cp.getConstant(superclassNameIndex);
		
		classNameIndex = cc.getNameIndex();
		superclassNameIndex = csc.getNameIndex();
		
//		/*DEBUG*/System.out.println("classNameIndex is "+classNameIndex);
		
		cp.setConstant(classNameIndex, new ConstantUtf8(newClass));
		cp.setConstant(superclassNameIndex, new ConstantUtf8(newSuperclass));
		
		generator.setConstantPool(cp);
		
		// modify the local variable this
		Method[] methods = generator.getMethods();
		for (int i=0; i<methods.length; i++){
			Method method = methods[i];
			LocalVariableTable table = method.getLocalVariableTable();
			LocalVariable[] locals = table.getLocalVariableTable();
			
			for (int j=0; j<locals.length; j++){
				LocalVariable local = locals[j];
				if (local.getName().equals("this")){
					int signatureIndex = local.getSignatureIndex();
					/*DEBUG*/System.out.println("this index: "+signatureIndex);
					cp.setConstant(signatureIndex, new ConstantUtf8("L"+newClass+";"));
				}
			}
		}
		
		
		// find all invokestatic calls and modify them
		
		// find all methods, that have been implicitly overriden in the hierarchy and change the dispatch
		
		
		// write the classfile
		try {
			generator.getJavaClass().dump("bin/"+newClass+".class");
		} catch (IOException e) {
			throw new MixerException(e);
		}
		
		
		// find all inner classes and run recursively
		
	}

    /** This hashmap holds the superclasses of all classes we've seen yet */
    HashMap	superClasses = new HashMap();

}
