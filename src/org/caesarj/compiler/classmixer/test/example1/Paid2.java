package org.caesarj.compiler.classmixer.test.example1;
/**
 * Test class
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
 * @author Diana Kapsa
 * 
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
