package org.caesarj.compiler.classmixer.test.example1;

/**
 * Test class
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
 * @author Diana Kapsa
 * 
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
