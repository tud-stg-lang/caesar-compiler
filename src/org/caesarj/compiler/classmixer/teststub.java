package org.caesarj.compiler.classmixer;

/*
 * Created on 06.02.2004
 * Diana Kapsa
 * teststub for running merge algorithm
 */
 
/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
 * @author Diana Kapsa
 * 
 */
import java.util.*; 
 
import org.apache.bcel.classfile.*;
import org.apache.bcel.Repository;

public class teststub {

	/**
	 * Names of Classes to be merged: A and B
	*/
	static private String A_Class;
	static private String B_Class;
	
	/*
	 * Byte arrays to be merged
	 */
	static private byte[][] list1;
	static private byte[][] list2;
	static private byte[][] mergedList;
	/*
	* Byte arrays to be merged
	*/
	static private JavaClass[] classList1;
	static private JavaClass[] classList2;
	static private JavaClass[] newList;
	
	/*
	* printing code of method
	*/
	public static void printCode(Method[] methods) {
	 for(int i=0; i < methods.length; i++) {
		  System.out.println(methods[i]);
		  Code code = methods[i].getCode();
		  if(code != null) // Non-abstract method
			   System.out.println(code);
 	 }
	}
	
	public static void main(String[] args) {
		A_Class = "org.caesarj.compiler.classmixer.test.example1.Paid2";
		B_Class = "org.caesarj.compiler.classmixer.test.example1.Payer2";
		
		System.out.println("Loading test classes...");
		JavaClass   TestClass1   = Repository.lookupClass(A_Class);
		JavaClass   TestClass2   = Repository.lookupClass(B_Class);
		
		//JavaClass[]	clList1=TestClass1.getSuperClasses();
		LinkedList l1 = new LinkedList();
		JavaClass j1 = TestClass1;
		while(!j1.getClassName().equals(j1.getSuperclassName())){
			l1.addFirst(j1);
			j1 = Repository.lookupClass(j1.getSuperclassName());
			//System.out.println(j1.getClassName());
		};
		l1.addFirst(j1);
		JavaClass[] clList1 = new JavaClass[l1.size()];
		list1 = new byte[clList1.length][];
		//System.out.println(""+l1.size());
		for (int i=0;i<l1.size();i++){
			clList1[i] = (JavaClass) l1.get(i);
			//System.out.println(clList1[i].getClassName());
			list1[i] = clList1[i].getBytes();
		}
		
		//JavaClass[] clList2=TestClass2.getSuperClasses();
		LinkedList l2 = new LinkedList();
		JavaClass j2 = TestClass2;
		while(!j2.getClassName().equals(j2.getSuperclassName())){
			l2.addFirst(j2);
			j2 = Repository.lookupClass(j2.getSuperclassName());
			//System.out.println(j1.getClassName());
		};
		l2.addFirst(j2);
		JavaClass[] clList2 = new JavaClass[l2.size()];
		list2 = new byte[clList2.length][];
		//System.out.println(""+l2.size());
		for (int i=0;i<l2.size();i++){
			clList2[i] = (JavaClass) l2.get(i);
			//System.out.println(clList2[i].getClassName());
			list2[i] = clList2[i].getBytes();
		}
		
						
		Mixer TObject = new Mixer("test class",list1,list2);
		TObject.merge();
		
	}
}
