package org.caesarj.compiler.classmixer;

/*
 * Created on 02.03.2004
 *
 * realization of a vector of mixins
 */

/**
 * @version $Revision: 1.2 $ $Date: 2004-03-05 20:25:05 $
 * @author Diana Kapsa
 * 
 */
import java.util.Vector;

public class MixinVector {

	private Vector list;
	private int count;
	
	public MixinVector() {
		list = new Vector();
		count =0;
	}
	
	//TODO implement contructor for initiating entire list at once
	
	/*
	 * comparison of two vectors
	 * check: alternative implementation with containsAll in Vector
	 */
	public boolean checkEqual(MixinVector v2){
		Vector l2 = v2.getList();
		if (list.size()!= v2.vLength()) return false;
		for (int i=0; i < list.size(); i++){
			if (!l2.contains(list.get(i))) return false;
		}
		return true;
	}
	
	public void addMixin(Mixin el){
		list.add(el);
		count++;
	}
	
	public Mixin getMixin(int position){
		return (Mixin) list.get(position);
	}
	
	public Mixin getLast(){
		if (count < 1) return new DefaultMixin(); 
		return (Mixin) list.lastElement();
	}
	
	public int vLength(){
		return count;
	}
	
	public boolean contains(Mixin m){
		return list.contains(m);
	}
	
	public void printMixinVector(){
		for (int i=0; i<count;i++){
			Mixin m = (Mixin) list.elementAt(i);
			m.printMixinName();
		}
	}

	/**
	 * @return
	 */
	public Vector getList() {
		return list;
	}

}
