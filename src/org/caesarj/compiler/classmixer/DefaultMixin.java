package org.caesarj.compiler.classmixer;

/*
 * Created on 05.03.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @version $Revision: 1.1 $ $Date: 2004-03-05 20:18:03 $
 * @author Diana Kapsa
 * 
 */
public class DefaultMixin extends Mixin{

	/**
	 * 
	 */
	public DefaultMixin() {
		System.out.println("DefaultMixin:: init: DefaultMixin was generated");
	}
	
	protected void decompose(){}
		
	/**
	 * basic composition procedure for current mixin and new mixin
	 **/
	public Mixin compose(Mixin m2){
		return new DefaultMixin();
	}

	public boolean checkEquals(Mixin m2){
		return true;
	}
	
	public void printMixin(){
		System.out.println("DefaultMixin:: printMixin: empty Mixin");
	}


}
