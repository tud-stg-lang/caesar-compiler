package org.caesarj.mixer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.util.ClassLoader;
import org.caesarj.mixer.intern.ClassModifyingVisitor;

public class Mixer {
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
	
	/**
	 * Generate the mixin list for a class. 
	 */
	public MixinList	mixinListForClass( String clazz ) throws MixerException{
		String 	list = "",
				current = clazz;
		JavaClass	javaClass;
		while(!current.equals(OBJECT_CLASS)){
			// load class
			javaClass = Repository.lookupClass(current);
			// find implemented interface (consider cclasses only)
			String[] interfaces = javaClass.getInterfaceNames();
			if (interfaces.length!=1){
				throw new MixerException("Illegal interfaces for "+current+": "+interfaces.length);
			}
			// get the name of the mixin & add it to the list
			String mixinName = implementationName( interfaces[0] );
			list = list.equals("") ? mixinName : list + ","+mixinName;
			// proceed to the super class 
			current = getSuperclassName(current);
		}
		
		return MixinList.createListFromString(list);
	}

	/**
	 * This function implements the &-operator on classfile mixins.
	 * TODO: generate class before passing to subclasses !!
	 */
	public String mix( String class1, String class2 ) throws MixerException{
		// Create mixin lists 
		MixinList list1  = mixinListForClass(class1);
		MixinList list2  = mixinListForClass(class2);
		// and linaearize them
		MixinList	mixinList = Linearizator.instance().mix(list1,list2);
		
		String	generatedClass = mixinList.generateClassName();
		
		classStack.push( generatedClass );
		
		// Create the inner mixin lists
		Set inners1, inners2, inners;
		inners1 = collectInnerClasses(class1);
		inners2 = collectInnerClasses(class2);
		
		inners = intersection(inners1,inners2);
		String [] innerClasses = (String[])inners.toArray(new  String[0]);
		
		
		// Create all inner classes
		for (int i = 0; i < innerClasses.length; i++) {
			String innerClass = innerClasses[i];
	
			String newInnerClass = mix( class1+"$"+innerClass, class2+"$"+innerClass );
		}

		// Generate the class
		generateClass(mixinList);
		
		classStack.pop();
		
		return generatedClass;
	}
	
	
	public static void setOutputDir( String dir ){
		ClassModifyingVisitor.setOutputDirectory( dir );
	}
	
    private static Mixer singleton = new Mixer();
   
    public static Mixer instance() {
        return singleton;
    }

    private Mixer() {
    }
    
    boolean	hasSuperclass( String clazzName ) throws MixerException{
    	return  !getSuperclassName(clazzName).equals(OBJECT_CLASS);	
   }
    
    public String getSuperclassName( String clazzName ) throws MixerException{
    	// Perhaps it is one of those classes we've seen before
		if (superClasses.containsKey(clazzName)){
			return (String)superClasses.get(clazzName);
		}
		// if not, we need to load the class
    	String		s;
		JavaClass 	clazz = Repository.lookupClass(clazzName);
		if (clazz == null)
			throw new MixerException("Class not found: "+clazzName);
    	s = clazz.getSuperclassName().replace('.','/');
    	// store for later use
    	superClasses.put(clazzName,s);
    	return s;
    }
    
    public String generateClass(MixinList mixinList) throws MixerException {

/*DEBUG*/	System.out.println("Mixing "+mixinList);
    	
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
										///mixinList.generateClassName(element,last);
										NameGenerator.forMixin(mixinList,element,last);
    			
    			replacedClasses.put( thisMixin, newClassName );
    			createModifiedClass( thisMixin, newClassName, superMixin);
				
    			// add the superclass of this class to the hashmap, so we don't have to load 
    			// the classfile for our generated classes later
    			superClasses.put(newClassName, superMixin);
    			thisMixin = newClassName; 
    		}
    		
     		superMixin = thisMixin;
    	}
 /*DEBUG*/	System.out.println(" generated: "+thisMixin);
    	// return the name of the created class (which has been written to thisMixin above)
        return thisMixin;
    }

    Set	intersection( Set a, Set b ){
    	HashSet	result = new HashSet();
    	Iterator	it = a.iterator();
    	while (it.hasNext()){
    		String clazz = (String)it.next();
    		if (b.contains(clazz))
    			result.add(clazz);
    	}
    	return result;
    }
    
    Set	collectInnerClasses( String clazzName ){
    	HashSet	innerClasses = new HashSet();
   		JavaClass clazz = Repository.lookupClass(clazzName);
		Attribute [] attributes = clazz.getAttributes();
		
		// check for each attribute if it is an innerclasses-attribute
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			if (attribute.getTag() == org.apache.bcel.Constants.ATTR_INNER_CLASSES)
			{
				InnerClasses inner = (InnerClasses)attribute;
				InnerClass[] inners = inner.getInnerClasses();
				for (int nr=0; nr < inners.length; nr++){
					
					int index = inners[nr].getInnerNameIndex();
					
					
					// load the name of the outer class
					String outerClass = loadClassName(	inners[nr].getOuterClassIndex(),
														inner.getConstantPool() );

					// IF this entry does not belong to this mixin THEN ignore it ...
					if (!outerClass.equals(clazzName)){
						continue;							
					}
					
					// ... ELSE add it to the vector.
					String innerClass = loadName(	inners[nr].getInnerNameIndex(), 
													inner.getConstantPool() );
					innerClasses.add(innerClass);
				}
			}
		}
		return innerClasses;
    }
    
     
 	private String loadName(int i, ConstantPool pool) {
 		ConstantUtf8	c = (ConstantUtf8)pool.getConstant(i);
 		return c.getBytes();
 	}

	private String loadClassName( int index, ConstantPool cp ){
		ConstantClass 	cclass = (ConstantClass)cp.getConstant(index);
    	
    	return cclass.getBytes(cp);
    }
    
    /**
     * This method creates a new class <code>newClass</code> which extends <code>newSuperClass</code> by
     * copying and modifiyng the classfile of <code>originalClass</code>
     */
	protected void createModifiedClass( String originalClass, String newClass, String newSuperclass ) throws MixerException{
		ClassModifyingVisitor.modify(originalClass,newClass,newSuperclass,replacedClasses);
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
