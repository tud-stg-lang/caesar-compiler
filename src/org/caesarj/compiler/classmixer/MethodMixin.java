package org.caesarj.compiler.classmixer;

/**
 * Representation of method based mixin
 * 
 * @version $Revision: 1.3 $ $Date: 2004-03-05 20:36:23 $
 * @author Diana Kapsa
 * 
 */

import org.apache.bcel.classfile.*;

public class MethodMixin extends Mixin {
	
	protected Method MixinMethod;
	
	public MethodMixin(Method x){
		MixinMethod = x;
		decompose();
	}
	
	/*public MethodMixin(byte[] iCode) {
		super(iCode);
		Code = new Method();
	}*/
	
	public Mixin compose(Mixin m2){
		Mixin newMixin;
		
		if (m2.getClass()!= this.getClass()) {
			//TODO Exception shoud be raised here
			newMixin = this;
		} else {
			newMixin = new MethodMixin(MixinMethod);
			//TODO add here new Code from mixin m2
		}
		return newMixin;
	}
	
	public boolean checkEquals(Mixin m2){
		MethodMixin inMixin;

		if (m2.getClass()!=this.getClass()) {
			return false;
		} else {
			 inMixin = (MethodMixin) m2;
		}
		return (MixinMethod.equals(inMixin.getMixinMethod()));
	}
	
	protected void decompose(){
		MixinName = MixinMethod.getName();
		//MixinCode = MixinMethod.getCode();
	}

	/**
	 * @return
	 */
	public Method getMixinMethod() {
		return MixinMethod;
	}
	
	public void printMixin(){
		System.out.println(this.MixinMethod.getCode().toString());
	}

}
