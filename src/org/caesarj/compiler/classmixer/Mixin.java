package org.caesarj.compiler.classmixer;

/*
 * Created on 01.03.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
 * @author Diana Kapsa
 * 
 */
public abstract class Mixin {

	/*corresponing to initial class or method*/
	protected String MixinName;
	//protected Code MixinCode;
	
				
	/**
	 * analysing inner structure of mixin and building lists of inner mixins
	 */
	abstract protected void decompose();
		
	/**
	 * basic composition procedure for current mixin and new mixin
	 **/
	abstract public Mixin compose(Mixin m2);
	
	abstract public boolean checkEquals(Mixin m2);
	
	abstract void printMixin();

	/**
	 * @return
	
	public Code getMixinCode() {
		return MixinCode;
	}*/

	/**
	 * @return
	 */
	public String getMixinName() {
		return MixinName;
	}
	
	public void printMixinName(){
		System.out.println(MixinName);
	}

}
