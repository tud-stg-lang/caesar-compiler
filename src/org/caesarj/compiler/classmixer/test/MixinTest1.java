package org.caesarj.compiler.classmixer.test;

/**
 * Class implements tests for ClassMixin and MethodMixin classes
 * Special focus on compose method and new class generation
 * 
 * @version $Revision: 1.1 $ $Date: 2004-03-09 16:38:39 $
 * @author Diana Kapsa
 * 
 */

import org.apache.bcel.classfile.*;
import org.apache.bcel.Repository;

import org.caesarj.compiler.classmixer.*;

public class MixinTest1 {
		/*
		 * Names of Classes to be merged: A and B
		*/
		static private String A_Class;
		static private String B_Class;
		static private JavaClass TestClass1;
		static private JavaClass TestClass2;
	
		/*
		 * Byte representation of classes
		 */
		static private byte[] ClassBytes1;
		static private byte[] ClassBytes2;
		static private byte[] NewClass;
	
	
	
	public static void main(String[] args) {
		A_Class = "org.caesarj.compiler.classmixer.test.example1.Paid2";
		B_Class = "org.caesarj.compiler.classmixer.test.example1.Payer2";
		
		System.out.println("");
		System.out.print("Loading test classes...");
		TestClass1   = Repository.lookupClass(A_Class);
		TestClass2   = Repository.lookupClass(B_Class);
		ClassBytes1 = TestClass1.getBytes();
		ClassBytes2 = TestClass2.getBytes();
		
		System.out.println("building mixins...");
		ClassMixin ClassMixin1 = new ClassMixin(ClassBytes1);
		ClassMixin ClassMixin2 = new ClassMixin(ClassBytes2);
		System.out.println("Print mixin 1...");
		ClassMixin1.printMixin();
		System.out.println("Print mixin 2...");
		ClassMixin2.printMixin();
		
		System.out.println("Print new mixin resulting from mixin1 & mixin2...");
		System.out.println("");
		ClassMixin ComposedMixin = (ClassMixin) ClassMixin1.compose(ClassMixin2);
		JavaClass testClass = ComposedMixin.getMixinClass();
		System.out.println("Package name of new created class : " + testClass.getPackageName());
		ComposedMixin.printMixin();
		
		
		try {
			/* Dump the class to "class name".class */
			//testClass.setConstantPool(cp.getFinalConstantPool());
			/*set path for dumping "class file"*/
			testClass.dump(testClass.getClassName() + ".class");
		} catch(Exception e) { System.out.println(e.toString()); }
		System.out.print("Test finished...");
		
		
	}
}
