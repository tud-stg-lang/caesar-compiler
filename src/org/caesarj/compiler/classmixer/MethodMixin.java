package org.caesarj.compiler.classmixer;

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
 * @author Diana Kapsa
 * 
 * representation of method based mixin
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
