package org.caesarj.compiler.classmixer;

/**
 * Main class for initiating merging process of mixin hierarchies
 * Wrapper for the actual mixer implementation.
 * Initilisation by rather names of classes or direct class hierarchies
 * 
 * @version $Revision: 1.1 $ $Date: 2004-03-09 16:38:39 $
 * @author Diana Kapsa
 * 
 */
import java.io.*;
import java.util.*;

import org.apache.bcel.classfile.*;

public class MixerWrapper {

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
  //private int helperClassIndex;
	
  private Hashtable MixinTable;
  //Hashtable must be final
  //TODO implement singleton pattern
  	
	public MixerWrapper(String class1, String class2){
	//TODO constructor based on Class names
	//self-constructing the inheritance hierarchy	
	}
		
	public MixerWrapper(String x, byte[][] a,byte[][] b) {
		firstByteList = a;
		secondByteList = b;
		virtualClassName = x;
		//MixinTable = new Hashtable();
		//initClassLists();
		initMixinLists();
	}
	
	public void merge(){
		Mixer mixerObject = new Mixer(firstMixinVector,secondMixinVector);
		mixerObject.mergeMixinVectors();
		newMixinVector = mixerObject.getNewMixinVector();
		ClassMixin newClassMixin = (ClassMixin) newMixinVector.getLast();
		newClass = newClassMixin.getMixinClass();
		virtualClassName = newClass.getClassName();
		//TODO Check: hat das noch hier etwas zu suchen??!!
		buildNewClassName(virtualClassName);
	}
	
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
		//System.out.println("");
		//System.out.println("First Mixin List:");
		for (int i=0;i < firstByteList.length; i++ ){
			firstMixinVector.addMixin(new ClassMixin(firstByteList[i]));
			firstMixinVector.getLast().printMixinName();
		}
		//System.out.println("");
		//System.out.println("Second Mixin List:");
		for (int i=0;i < secondByteList.length; i++ ){
			secondMixinVector.addMixin(new ClassMixin(secondByteList[i]));
			secondMixinVector.getLast().printMixinName();
		}
	}//initMixinLists

	private void buildNewClassName(String vClassName){
		//virtualClassName = firstMixinVector.getLast().getMixinName()+ "_"+ secondMixinVector.getLast().getMixinName();
		shortClassName= String.valueOf(vClassName.hashCode());
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
	public JavaClass getNewClass() {
		return newClass;
	}


}
