/*
 * Created on 27.01.2004
 *
  */
package org.caesarj.compiler.classmixer.test.example1;

/**
 * @author Diana
 *
 * class for demonstrating method combination algorithm 
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
