package org.caesarj.compiler.classmixer;

/**
 * Generic implementation of merging process of given mixin hierarchies
 * with no dependency on Mixin type
 * 
 * Implements the linearizaion algorithm as introduced by E.Ernst 
 * in 'Higher-Order Hierarchies'
 * 
 * @version $Revision: 1.4 $ $Date: 2004-03-09 16:38:39 $
 * @author Diana Kapsa
 * 
 */
import java.io.*;
import java.util.*;


import org.apache.bcel.classfile.*;


public class Mixer {

	//input classes 
	private MixinVector firstMixinVector, secondMixinVector;
	  
	//complete class list for new inheritance hierarchy
	private MixinVector newMixinVector;
	  
	public Mixer(MixinVector fstVector, MixinVector sndVector) {
			firstMixinVector = fstVector;
			secondMixinVector= sndVector;
	}
	
	/**
	 * implementation of the merging algorithm
	 * method 'combine' as introduced by E.Ernst in 'Higher-Order Hierarchies'
	 */
	private void linearize(int fst, int snd, int l1, int l2){
		if ((snd < l2) && (fst < l1)){
			//System.out.println("Mixer: linearize: full lists given...");
			if ((secondMixinVector.getMixin(snd).checkEquals((firstMixinVector.getMixin(fst))))){
				//System.out.println("1. Mixer: linearize: first two elem are equal...");
				newMixinVector.addMixin(secondMixinVector.getMixin(snd));
				newMixinVector.printMixinVector();
				linearize(fst+1,snd+1,l1,l2);
			} else if (!secondMixinVector.contains(firstMixinVector.getMixin(fst))){
				//System.out.println("2. Mixer: linearize: comparing el..");
				if (newMixinVector.getLast().checkEquals(firstMixinVector.getMixin(fst))){
					//inheritance hierarchy remains unchaged
					newMixinVector.addMixin(firstMixinVector.getMixin(fst));
					newMixinVector.printMixinVector();
				} else {
					//newMixinVector.addMixin(firstMixinVector.getMixin(fst));
					Mixin newMixin = newMixinVector.getLast().compose(firstMixinVector.getMixin(fst));
					//newMixin.printMixin();
					newMixinVector.addMixin(newMixin);
					newMixinVector.printMixinVector();
				}; 
				linearize(fst+1,snd,l1,l2);
			} else if (!firstMixinVector.contains(secondMixinVector.getMixin(snd))){
				//System.out.println("3. Mixer: linearize: comparing el..");
				if (newMixinVector.getLast().checkEquals(secondMixinVector.getMixin(snd))){
					//inheritance hierarchy remains unchaged
					newMixinVector.addMixin(secondMixinVector.getMixin(snd));
				} else {
					Mixin newMixin = newMixinVector.getLast().compose(secondMixinVector.getMixin(snd));
					//newMixin.printMixin();
					newMixinVector.addMixin(newMixin);
				}; 
				linearize(fst,snd+1,l1,l2);
			} else {
				// raise exception
			}
		} else if (fst >= l1) {
			//System.out.println("Mixer: linearize: first list empty...");
			while (snd < l2){
				if (newMixinVector.getLast().equals(secondMixinVector.getMixin(snd))){
					//inheritance hierarchy remains unchaged
					newMixinVector.addMixin(secondMixinVector.getMixin(snd));
				} else {
					Mixin newMixin = newMixinVector.getLast().compose(secondMixinVector.getMixin(snd));
					newMixin.printMixin();
					newMixinVector.addMixin(newMixin);
				}; 
				snd++;
			}
		} else if (snd >= l2){
			//System.out.println("Mixer: linearize: second list empty...");
			while (fst < l1){
				if (newMixinVector.getLast().equals(firstMixinVector.getMixin(fst))){
					//inheritance hierarchy remains unchaged
					newMixinVector.addMixin(firstMixinVector.getMixin(fst));
				} else {
					Mixin newMixin = newMixinVector.getLast().compose(firstMixinVector.getMixin(fst));
					newMixin.printMixin();
					newMixinVector.addMixin(newMixin);
				}; 
				fst++;
			}
		};
		//System.out.println("Mixer::linearization: Current vector length is "+newMixinVector.vLength());
	}//linearize()

	public void mergeMixinVectors(){
		int l1, l2;
		newMixinVector = new MixinVector();
		if (firstMixinVector==null) {
			l1=0; 
		} else {
			l1 = firstMixinVector.vLength(); 
		};
		if (secondMixinVector==null) {
			l2=0; 
		} else {
			l2 = secondMixinVector.vLength(); 
		};
		System.out.println("");
		System.out.println("Mixer: merge: starting linearization process...");
		linearize(0,0,l1,l2);
	}//merge
	/**
	 * @return
	 */
	public MixinVector getFirstMixinVector() {
		return firstMixinVector;
	}

	/**
	 * @return
	 */
	public MixinVector getNewMixinVector() {
		return newMixinVector;
	}

	/**
	 * @return
	 */
	public MixinVector getSecondMixinVector() {
		return secondMixinVector;
	}

}
