/*
 * Created on 27.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.caesarj.compiler.classmixer.test.example1;

/**
 * @author Diana
 *
  * class for demonstrating method combination algorithm 
 */
public class Payer extends Person{
	
	
	protected int  balance;
	
	public Payer (){
			super();
			balance = 1000; 
		}
		
	public int pay(int amount){
			balance = balance - amount; 
			System.out.println("Amount payed" + amount);
			return amount;
	}

}
