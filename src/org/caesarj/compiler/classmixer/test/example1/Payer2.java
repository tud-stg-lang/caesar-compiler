/*
 * Created on 06.02.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.caesarj.compiler.classmixer.test.example1;

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
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
