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
public class Paid2 extends Paid {

	/**
	 * 
	 */
	public Paid2() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void doubleaccept(int amount){
			foo1 = "Was paid amount:" + 2*amount; 
		}
	
		public void send(){
			//send reminder for belated bill; 
		}

}
