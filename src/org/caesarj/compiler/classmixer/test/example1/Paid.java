/*
 * Created on 06.02.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.caesarj.compiler.classmixer.test.example1;

/**
 * @author Diana
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Paid extends Person{

	private int  balance;
	private int foo;
	protected String foo1;
	
	public Paid (){
			super();
			balance = 1000; 
		}
		
	public int accept(int amount){
			balance = balance + amount; 
			System.out.println("Amount payed" + amount);
			return amount;
	}

}
