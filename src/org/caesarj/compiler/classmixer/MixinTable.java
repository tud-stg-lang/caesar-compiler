package org.caesarj.compiler.classmixer;
/**
 * Implementing unique hash table of so far created mixins to
 * keep track of already created virtual classes
 * 
 * Implementation of the singleton pattern
 * 
 * Read and written only in 'compose' methods of 
 * 
 * @version $Revision: 1.1 $ $Date: 2004-03-09 16:38:39 $
 * @author Diana Kapsa
 * 
 */

import java.util.*;

public class MixinTable {
	
	private static Hashtable mTable = null;

	private MixinTable() {
	}
	
	public static synchronized Hashtable getInstance() {
		if (mTable == null) {
			mTable = new Hashtable();
		};
		return mTable;
	  } 
}
