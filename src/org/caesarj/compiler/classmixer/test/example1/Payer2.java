package org.caesarj.compiler.classmixer.test.example1;

/**
 * Test class
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
 * @author Diana Kapsa
 * 
 */
public class Payer2 extends Payer{

	public Payer2() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void doublepay(int amount){
		balance = balance - 2*amount; 
	}
	
	public void receive(){
		//receive reminder for belated bill; 
	}

}
