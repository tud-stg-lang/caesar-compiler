package org.caesarj.compiler.classmixer.test.example1;

/**
 * Test class
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
 * @author Diana Kapsa
 * 
 */
public class Person {
	
	private String  name;
	
	public Person(){
	}
	
	public void setName(String x){
		name = "";
		if (x != null)name=x; 
	}
	
	public String getName(){
		return this.name;
	}
}
