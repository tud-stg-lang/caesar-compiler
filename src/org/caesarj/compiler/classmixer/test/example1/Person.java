/*
 * Created on 27.01.2004
 *
  */
package org.caesarj.compiler.classmixer.test.example1;

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
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
