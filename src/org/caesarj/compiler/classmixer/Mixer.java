package org.caesarj.compiler.classmixer;

/*
 * Created on 02.03.2004
 *
 * main class for initiating merging process of mixin hierarchies
 */

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
 * @author Diana Kapsa
 * 
 */
import java.io.*;
import java.util.*;


import org.apache.bcel.classfile.*;


public class Mixer {

  //input classes 
  private JavaClass[] firstClassList,secondClassList;
  private byte[][] firstByteList,secondByteList;
  private MixinVector firstMixinVector, secondMixinVector;
  
  //new built target class 
  private String virtualClassName;
  private String shortClassName;
  private JavaClass newClass;
  
  //complete class list for new inheritance hierarchy
  private JavaClass[] newClassList;
  private MixinVector newMixinVector;
  //Starting index of helper classes within new class list
  private int helperClassIndex;
	
  private static Hashtable MixinTable;
  	
	public Mixer(String class1, String class2){
	//TODO constructor based on Class names
	//self-constructing the inheritnce hierarchy	
	}
	
	public Mixer(String x, byte[][] a,byte[][] b) {
		firstByteList = a;
		secondByteList = b;
		virtualClassName = x;
		MixinTable = new Hashtable();
		//initClassLists();
		initMixinLists();
		buildNewClassName();
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

	public void merge(){
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

	
	/*
	 * procedure for initializing other JavaClass lists
	 * Is this required?!?
	 */
	private void initClassLists(){
		InputStream file;
		ClassParser cParser;
		JavaClass	tClass;
		firstClassList = new JavaClass[firstByteList.length];
		for (int i = 0; i < firstByteList.length; i++) {
			file = new ByteArrayInputStream(firstByteList[i]);
			try {
				cParser = new ClassParser(file,"test");
				firstClassList[i] = cParser.parse();
			} catch (Exception e) {
				System.out.println("Mixer >> Exception has occured" + e.toString());
			}
		}
		
		secondClassList = new JavaClass[secondByteList.length];
		for (int i = 0; i < secondByteList.length; i++) {
					file = new ByteArrayInputStream(secondByteList[i]);
					try {
						cParser = new ClassParser(file,"test");
						secondClassList[i] = cParser.parse();
					} catch (Exception e) {
						System.out.println("Mixer >> Exception has occured" + e.toString());
					}
		}
	}//initClassLists
	
	/**
	 * procedure for initializing both mixin lists
	 *
	 */
	private void initMixinLists(){
		firstMixinVector = new MixinVector();
		secondMixinVector = new MixinVector();
		System.out.println("");
		System.out.println("First Mixin List:");
		for (int i=0;i < firstByteList.length; i++ ){
			firstMixinVector.addMixin(new ClassMixin(firstByteList[i]));
			firstMixinVector.getLast().printMixinName();
		}
		System.out.println("");
		System.out.println("Second Mixin List:");
		for (int i=0;i < secondByteList.length; i++ ){
			secondMixinVector.addMixin(new ClassMixin(secondByteList[i]));
			secondMixinVector.getLast().printMixinName();
		}
		
	}//initMixinLists
	
	private void buildNewClassName(){
		virtualClassName = firstMixinVector.getLast().getMixinName()+ "_"+ secondMixinVector.getLast().getMixinName();
		shortClassName= String.valueOf(virtualClassName.hashCode());
		//TODO replace procedure for building real class name with MD5
		shortClassName = shortClassName.replace('0','a');
		shortClassName = shortClassName.replace('1','b');
		shortClassName = shortClassName.replace('2','c');
		shortClassName = shortClassName.replace('3','d');
		shortClassName = shortClassName.replace('4','e');
		shortClassName = shortClassName.replace('5','f');
		shortClassName = shortClassName.replace('6','g');
		shortClassName = shortClassName.replace('7','h');
		shortClassName = shortClassName.replace('8','i');
		shortClassName = shortClassName.replace('9','j');
		//System.out.println("Mixer:: buildNewClassName "+ shortClassName+" "+virtualClassName);
	}
	

/**
 * @return
 */
public JavaClass[] getNewClassList() {
	return newClassList;
}

/**
 * @return
 */
public byte[][] getSecondByteList() {
	return secondByteList;
}

/**
 * @return
 */
public JavaClass[] getSecondClassList() {
	return secondClassList;
}

/**
 * @return
 */
public MixinVector getSecondMixinVector() {
	return secondMixinVector;
}

/**
 * @return
 */
public String getVirtualClassName() {
	return virtualClassName;
}

/**
 * @return
 */
public byte[][] getFirstByteList() {
	return firstByteList;
}

/**
 * @return
 */
public JavaClass[] getFirstClassList() {
	return firstClassList;
}

/**
 * @return
 */
public MixinVector getFirstMixinVector() {
	return firstMixinVector;
}

/**
 * @return
 */
public int getHelperClassIndex() {
	return helperClassIndex;
}

/**
 * @return
 */
public JavaClass getNewClass() {
	return newClass;
}

}
