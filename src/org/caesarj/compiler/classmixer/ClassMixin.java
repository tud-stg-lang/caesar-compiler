package org.caesarj.compiler.classmixer;

/*
 * Created on 02.03.2004
 *
 * representation of class based mixin
 */

/**
 * @version $Revision: 1.1 $ $Date: 2004-03-05 20:18:03 $
 * @author Diana Kapsa
 * 
 */
import java.io.*;
import java.util.Vector;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class ClassMixin extends Mixin{

	//special attributes for classes
	protected JavaClass MixinClass;
	protected byte[] mixinByteCode;
	protected MixinVector ClassMixinList;
	
	
	public ClassMixin(byte[] iCode) {
		mixinByteCode =iCode;
		ByteArrayInputStream file = new ByteArrayInputStream(mixinByteCode);
		try {
			ClassParser cParser = new ClassParser(file,"test");
			MixinClass = cParser.parse();
		} catch (Exception e) {
			System.out.println("ClassMixin::ClassMixin >> Exception has occured" + e.toString());
		}
		decompose();
	}
	
	public ClassMixin(JavaClass jClass){
		MixinClass = jClass;
		mixinByteCode = jClass.getBytes();
	}
	
	/**
	 * creating a new class by merging this mixin with input mixin
	 * new class will be generated as subclass of this class 
	 */
	public Mixin compose(Mixin m2){
		
		ClassMixin newMixin,m;
		JavaClass newClass, sndClass, fstClass;
		ClassGen newGen;
		String newClassName, superClassName, newClassFileName;
		int acc_flag;
		String[] implInterfaces;
		
		if (m2.getClass()!= this.getClass()) {
			//TODO Exception shoud be raised here
			//Classes should be only composed with classes
			return null;
		};
		m = (ClassMixin) m2;
		sndClass = m.getMixinClass(); 
		fstClass = this.MixinClass;
		
		/*
		 * instantiating new class with basic properties
		 */
		newClassName = fstClass.getClassName()+"_"+sndClass.getClassName();
		superClassName = fstClass.getClassName();
		newClassFileName = "test";
		if (fstClass.getAccessFlags()!=sndClass.getAccessFlags()){
			System.out.println("ClassMixin::compose Incompatible access flags for used classes");
			acc_flag = 1;
			//class will be made public --> default
			//TODO raise exception
		} else acc_flag = fstClass.getAccessFlags();
		String[] fstInterfaces = fstClass.getInterfaceNames() ;
		String[] sndInterfaces = sndClass.getInterfaceNames();
		Vector interfaceVector = new Vector();
		for (int i=0; i < fstInterfaces.length;i++){
			if (interfaceVector.contains(fstInterfaces[i])) interfaceVector.add(fstInterfaces[i]);
		}
		for (int i=0; i < sndInterfaces.length;i++){
			if (interfaceVector.contains(sndInterfaces[i])) interfaceVector.add(sndInterfaces[i]);
		}
		implInterfaces = new String[interfaceVector.size()];
		for (int i=0; i < implInterfaces.length;i++){ 
			String nClass = (String) interfaceVector.elementAt(i);
			implInterfaces[i]=nClass;
		}
		newGen = new ClassGen(newClassName,superClassName,newClassFileName,acc_flag,implInterfaces);
		
		/*
		 * instantiating rest of the class: constant_pool, fields, attributes...
		 */
		 
		/*
		 * linearizing mixin lists for methods and inner classes
		 */
		System.out.println("ClassMixin::compose: new Class/Mixin was generated");
		newClass = newGen.getJavaClass();
		newMixin = new ClassMixin(newClass);
		return newMixin;
	}
	
	public boolean checkEquals(Mixin m2){
		ClassMixin inMixin;

		if (m2.getClass()!=this.getClass()) {
			return false;
		} else {
			 inMixin = (ClassMixin) m2;
		}
		return inMixin.getMixinName().equals(this.MixinName);
		//return (ClassMixinList.checkEqual(inMixin.getClassMixinList()));
		
	}

	protected void decompose(){
		MixinName = MixinClass.getClassName();
		ClassMixinList = new MixinVector();
		//methods
		Method[] methodsList = MixinClass.getMethods();
		for (int i=0; i < methodsList.length; i++){
			ClassMixinList.addMixin(new MethodMixin(methodsList[i]));
		}
		//add inner classes to vector
		Attribute[] attributesList = MixinClass.getAttributes();
		/*
		DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(mixinByteCode));
		InnerClasses x = Attribute.readAttribute(dataIn, MixinClass.getConstantPool());
		for (int i=0; i < attributesList.length; i++){
			if (attributesList[i].getClass()== InnerClass.class) {
				InnerClass iClass = attributesList[i].readAttribute();
				ClassMixinList.addMixin(new ClassMixin(iClass));
			}
		}*/
		
	}

	/**
	 * @return
	 */
	public MixinVector getClassMixinList() {
		return ClassMixinList;
	}

	/**
	 * @return
	 */
	public JavaClass getMixinClass() {
		return MixinClass;
	}
	
	public void printMixin(){
			System.out.println(this.MixinClass.toString());
		}

}
