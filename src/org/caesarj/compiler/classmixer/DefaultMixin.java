package org.caesarj.compiler.classmixer;
/**
 * Default mixin for unknown mixin types
 * 
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:36:23 $
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
