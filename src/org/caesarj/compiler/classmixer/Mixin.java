package org.caesarj.compiler.classmixer;


/**
 * Abstract class for general mixin representation
 * 
 * <p> Implementations can be class based mixins, method
 * based mixins or default mixins
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
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
